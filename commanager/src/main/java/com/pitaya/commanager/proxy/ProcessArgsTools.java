package com.pitaya.commanager.proxy;

import com.pitaya.comannotation.annotation.Subscribe;
import com.pitaya.commanager.bean.ParameterThreadModeInfo;
import com.pitaya.commanager.tools.ELog;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Smarking on 18/1/6.
 */

public enum ProcessArgsTools {
    Instance;

    private static final String TAG = "ProcessArgsTools";
    private final ConcurrentHashMap<Method, ArrayList<ParameterThreadModeInfo>> mParameterTypeCacheMap = new ConcurrentHashMap();
    private ReentrantLock reentrantLock = new ReentrantLock();

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
    public Object[] processArgs(List<WeakReference<Proxy>> callbackList, Method method, Object[] args) {
        ELog.d(TAG, "————————————————————" + method.getDeclaringClass() + " " + method.getName());
        Type[] getGenericParameterTypes = method.getGenericParameterTypes();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        ELog.d(TAG, "GenericParameterTypes :" + getArrayInfo(getGenericParameterTypes));
        ELog.d(TAG, "ParameterTypes :" + getArrayInfo(parameterTypes));
        ELog.d(TAG, "ParameterAnnotations :" + getArrayInfo(annotations));
        ELog.d(TAG, "before :" + getArrayInfo(args));

        if (args == null || args.length == 0) {
            ELog.d(TAG, "——————————end——————————");
            return null;
        }

        //已经扫描过，直接用缓存的结果
        if (mParameterTypeCacheMap.containsKey(method)) {
            try {
                if (mParameterTypeCacheMap.get(method).isEmpty()) {
                    return args;
                }
                for (ParameterThreadModeInfo info : mParameterTypeCacheMap.get(method)) {
                    setOneArg(callbackList, args, info);
                }
                return args;
            } finally {
                ELog.d(TAG, "after :" + getArrayInfo(args));
                ELog.d(TAG, "——————————end from cache——————————");
            }
        }

        initCacheMap(method);

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterClass = parameterTypes[i];
            if (parameterClass == null) {
                continue;
            }
            //只判断当前类是否为接口，不判断超类的接口
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
            setOneArg(callbackList, args, pThreadModeInfo);
            mParameterTypeCacheMap.get(method).add(pThreadModeInfo);
        }

        ELog.d(TAG, "after :" + getArrayInfo(args));
        ELog.d(TAG, "——————————end——————————");
        return args;
    }


    /**
     * ConcurrentHashMap无法保证一组操作的原子性
     *
     * @param method
     */
    private void initCacheMap(Method method) {
        if (!mParameterTypeCacheMap.containsKey(method)) {
            reentrantLock.lock();
            try {
                if (!mParameterTypeCacheMap.containsKey(method)) {
                    mParameterTypeCacheMap.put(method, new ArrayList<ParameterThreadModeInfo>());
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    private void setOneArg(List<WeakReference<Proxy>> callbackList, Object[] args, ParameterThreadModeInfo info) {
        if ((args[info.index] instanceof Proxy)) {
            //已经是代理了
        } else {
            //如果是入参的注解的话，需要传入，如下：
            //void cancelCheckoutVip(Context context, String body, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<String> resultCallback);
            Object object = ProxyTools.create(info.parameterClass, args[info.index], info.isFromParameterAnnotation ? info.threadMode : null);
            callbackList.add(new WeakReference(object));
            args[info.index] = object;
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
