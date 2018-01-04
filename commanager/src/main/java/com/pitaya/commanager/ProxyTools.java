package com.pitaya.commanager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Smarking on 17/12/12.
 */

public class ProxyTools {

    private static final String TAG = "ProxyTools";

    private static final Method OBJECT_EQUALS =
            getObjectMethod("equals", Object.class);

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

    public static <T> T create(final Class<T> interfaceName, final Object target) {
        return (T) Proxy.newProxyInstance(interfaceName.getClassLoader(),
                new Class<?>[]{interfaceName},
                new ThreadHandler(interfaceName, target));
    }


    private static class ThreadHandler implements InvocationHandler {
        Class<?> interfaceName;
        Object target;

        public ThreadHandler(final Class<?> interfaceName, final Object target) {
            this.interfaceName = interfaceName;
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, final Method method, final Object... args)
                throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
                if (method.equals(OBJECT_EQUALS)) {
                    return equalsInternal(proxy, args[0]);
                }
                return method.invoke(this, args);
            }

            if (target == null) {
                Log.e(TAG, "target object is null");
                return null;
            }

            boolean isMainThread = false;
            if (Looper.getMainLooper() == Looper.myLooper()) {
                isMainThread = true;
            }

            //TODO 支持方法、类注解，暂时不支持属性注解
            Subscribe thread = method.getAnnotation(Subscribe.class);
            if (thread == null) {
                thread = interfaceName.getAnnotation(Subscribe.class);
            }

            if (thread == null) {
                return method.invoke(target, args);
            }

            if (thread.threadMode().equals(ThreadMode.POSTING)) {
                return method.invoke(target, args);
            }

            if (thread.threadMode().equals(ThreadMode.MAIN)) {
                if (isMainThread) {
                    return method.invoke(target, args);
                }

                Message message = Message.obtain();
                message.obj = new MethodInfo(method, target, args);
                mMainHandler.sendMessage(message);
                return null;
            }

            if (thread.threadMode().equals(ThreadMode.BACKGROUND)) {
                if (isMainThread) {
                    mBackgroundThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                method.invoke(target, args);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return null;

                }
                return method.invoke(target, args);
            }


            if (thread.threadMode().equals(ThreadMode.ASYNC)) {
                mBackgroundThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(target, args);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return null;
            }

            return method.invoke(target, args);

        }
    }

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

    private static Executor mBackgroundThreadPool = Executors.newCachedThreadPool();

    private static class MethodInfo {
        public Method method;
        public Object target;
        public Object[] args;

        public MethodInfo(Method method, Object target, Object[] args) {
            this.method = method;
            this.target = target;
            this.args = args;
        }
    }

//    public static <T> T create(T target) {
//        if (target == null) {
//            throw new NullPointerException("addProtocol protocolImpl is null");
//        }
//
//        Class<?>[] interfaces = target.getClass().getInterfaces();
//
//        if (interfaces == null) {
//            throw new IllegalArgumentException("addProtocol protocolImpl not implement interface");
//        }
//
//        if (interfaces.length > 1) {
//            throw new IllegalArgumentException("addProtocol protocolImpl implement more than one interface");
//        }
//        //TODO 可能有Crash
//        return (T) create(interfaces[0], target);
//    }
}
