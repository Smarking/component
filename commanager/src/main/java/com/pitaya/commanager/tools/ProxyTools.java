package com.pitaya.commanager.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.commanager.exception.MethodInfo;
import com.pitaya.commanager.exception.ParameterThreadModeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Smarking on 17/12/12.
 */

public class ProxyTools {

    private static final String TAG = "ProxyTools";

    private static final Method OBJECT_EQUALS = getObjectMethod("equals", Object.class);

    private static final ConcurrentHashMap<Method, ArrayList<ParameterThreadModeInfo>> mParameterTypeCacheMap = new ConcurrentHashMap();

    private static Method getObjectMethod(String name, Class... types) {
        try {
            // null 'types' is OK.
            return Object.class.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static boolean equalsInternal(Object me, Object other) {
        if (other == null) {
            return false;
        }
        return me.hashCode() == other.hashCode();
    }

    private final static Executor mBackgroundThreadPool = Executors.newCachedThreadPool();
    //TODO 可以借鉴Eventbus mMainHandler.sendEmptyMessage() 这样的话有内存泄漏，最好的做法是，sendEmptyMessage通知。但是不传递有效内容
    private static Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MethodInfo methodInfo = (MethodInfo) msg.obj;
            try {
                methodInfo.method.invoke(methodInfo.target, methodInfo.args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 动态代理支持线程切换
     */
    private static class ThreadProxyHandler implements InvocationHandler {
        Class<?> interfaceName;
        Object target;
        Subscribe outsideThreadMode;

        public ThreadProxyHandler(final Class<?> interfaceName, final Object target, @Nullable Subscribe threadMode) {
            this.interfaceName = interfaceName;
            this.target = target;
            outsideThreadMode = threadMode;
        }

        @Override
        public Object invoke(Object proxy, final Method method, Object... args)
                throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
                if (method.equals(OBJECT_EQUALS)) {
                    return equalsInternal(proxy, args[0]);
                }
                return method.invoke(this, args);
            }

            if (unRegister(method)) {
                target = null;
                return null;
            }

            if (target == null) {
                Log.e(TAG, "target object is null");
                return null;
            }

            //属性注解
            final Object[] reallyArgs = processArgs(method, args);

            boolean isMainThread = false;
            if (Looper.getMainLooper() == Looper.myLooper()) {
                isMainThread = true;
            }

            //外部指定执行线程
            Subscribe thread = outsideThreadMode;

            //方法注解
            if (thread == null) {
                thread = method.getAnnotation(Subscribe.class);
            }

            if (thread == null) {
                //类注解
                thread = interfaceName.getAnnotation(Subscribe.class);
            }

            //default ThreadMode.POSTING
            if (thread == null) {
                return invoke(method, target, reallyArgs);
            }

            //当前线程
            if (thread.threadMode().equals(ThreadMode.POSTING)) {
                return invoke(method, target, reallyArgs);
            }

            //主线程
            if (thread.threadMode().equals(ThreadMode.MAIN)) {
                if (isMainThread) {
                    return invoke(method, target, reallyArgs);
                }

                Message message = Message.obtain();
                message.obj = new MethodInfo(method, target, reallyArgs);
                mMainHandler.sendMessage(message);
                return null;
            }

            //后台线程
            if (thread.threadMode().equals(ThreadMode.BACKGROUND)) {
                if (isMainThread) {
                    mBackgroundThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            invoke(method, target, reallyArgs);
                        }
                    });
                    return null;

                }
                return invoke(method, target, reallyArgs);
            }

            //异步
            if (thread.threadMode().equals(ThreadMode.ASYNC)) {
                mBackgroundThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        invoke(method, target, reallyArgs);
                    }
                });
                return null;
            }

            return invoke(method, target, reallyArgs);
        }

        private Object invoke(Method method, Object target, Object[] args) {
            //已经被释放了
            if (target == null) {
                Log.d(TAG, "target is null ," + method.toString());
                return null;
            }
            try {
                long timeOld = System.currentTimeMillis();
                Object result = method.invoke(target, args);
                Log.d(TAG, String.format("%1$dms | ThreadName:%2$s | MethodName:%3$s ", System.currentTimeMillis() - timeOld, Thread.currentThread().getName(), method.toString()));
                return result;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        private boolean unRegister(Method method) {
            return false;
        }

        /**
         * 处理入参Args，判断入参类型是否有需要做线程切换的回调，支持入参的注解、入参所属于类注解、入参类内部方法注解。判断有没有上述三种情况。
         * 有的话，对当前arg转化为Proxy。
         * <p>
         * 是否扫描过、扫描结果为null、扫描结果不为null。存在三种状态
         *
         * @param method
         * @param args
         * @return
         */
        private Object[] processArgs(Method method, Object[] args) {
            Log.d(TAG, "————————————————————" + method.getDeclaringClass() + " " + method.getName());
            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] annotations = method.getParameterAnnotations();

            Log.d(TAG, "parameterTypes :" + getArrayInfo(parameterTypes));
            Log.d(TAG, "ParameterAnnotations :" + getArrayInfo(annotations));
            Log.d(TAG, "before :" + getArrayInfo(args));

            if (args == null || args.length == 0) {
                Log.d(TAG, "——————————end——————————");
                return null;
            }

            //已经扫描过，直接用缓存的结果
            if (mParameterTypeCacheMap.containsKey(method)) {
                try {
                    if (mParameterTypeCacheMap.get(method).isEmpty()) {
                        return args;
                    }
                    for (ParameterThreadModeInfo info : mParameterTypeCacheMap.get(method)) {
                        setOneArg(args, info);
                    }
                    return args;
                } finally {
                    Log.d(TAG, "after :" + getArrayInfo(args));
                    Log.d(TAG, "——————————end from cache——————————");
                }
            }

            initCacheMap(method);

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterClass = parameterTypes[i];
                if (parameterClass == null) {
                    continue;
                }
                if (!parameterClass.isInterface()) {
                    continue;
                }

                if (isContainKeyword(parameterClass.getClass(), "java") ||
                        isContainKeyword(parameterClass.getClass(), "android")) {
                    continue;
                }

                Subscribe thread = null;
                boolean isFromParameterAnnotation = false;

                //作为入参是否存在注解
                if (annotations.length > 0 &&
                        annotations[i].length > 0 &&
                        annotations[i][0] instanceof Subscribe) {
                    thread = (Subscribe) annotations[i][0];
                    isFromParameterAnnotation = true;
                }

                //入参类的注解
                if (thread == null) {
                    thread = parameterClass.getAnnotation(Subscribe.class);
                }

                if (thread == null) {
                    //入参类内部的方法注解
                    for (Method paramMethod : parameterClass.getMethods()) {
                        thread = paramMethod.getAnnotation(Subscribe.class);
                        if (thread != null) {
                            break;
                        }
                    }
                }

                if (thread == null) {
                    continue;
                }

                ParameterThreadModeInfo pThreadModeInfo = new ParameterThreadModeInfo(i, isFromParameterAnnotation, thread, parameterClass);
                setOneArg(args, pThreadModeInfo);
                mParameterTypeCacheMap.get(method).add(pThreadModeInfo);
            }

            Log.d(TAG, "after :" + getArrayInfo(args));
            Log.d(TAG, "——————————end——————————");
            return args;
        }


        /**
         * ConcurrentHashMap无法保证一组操作的原子性
         *
         * @param method
         */
        private void initCacheMap(Method method) {
            if (!mParameterTypeCacheMap.containsKey(method)) {
                synchronized (ThreadProxyHandler.class) {
                    if (!mParameterTypeCacheMap.containsKey(method)) {
                        mParameterTypeCacheMap.put(method, new ArrayList<ParameterThreadModeInfo>());
                    }
                }
            }
        }

        private void setOneArg(Object[] args, ParameterThreadModeInfo info) {
            if ((args[info.index] instanceof Proxy)) {
                //已经是代理了
            } else {
                //如果是入参的注解的话，需要传入，如下：
                //void cancelCheckoutVip(Context context, String body, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<String> resultCallback);
                args[info.index] = ProxyTools.create(info.parameterClass, args[info.index], info.isFromParameterAnnotation ? info.threadMode : null);
            }
        }

        private String getArrayInfo(Object[] args) {
            StringBuffer buffer = new StringBuffer();
            if (args == null) {
                buffer.append("null");
            }
            for (Object obj : args) {
                buffer.append((obj == null ? "null" : obj.toString()) + "---");
            }
            return buffer.toString();
        }

        private boolean isContainKeyword(Class<?>[] classes, String keyword) {
            if (classes == null || classes.length == 0) {
                return false;
            }
            for (Class c : classes) {
                if (c.getName().contentEquals(keyword)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isContainKeyword(Class c, String keyword) {
            if (c == null) {
                return false;
            }
            return c.getName().contentEquals(keyword);
        }
    }

    public static <T> T create(final Class<T> interfaceName, final Object target) {
        return create(interfaceName, target, null);
    }

    public static <T> T create(final Class<T> interfaceName, final Object target, Subscribe threadMode) {
        return (T) Proxy.newProxyInstance(interfaceName.getClassLoader(),
                new Class<?>[]{interfaceName},
                new ThreadProxyHandler(interfaceName, target, threadMode));
    }
}
