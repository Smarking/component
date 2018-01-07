package com.pitaya.commanager.tools;

import android.support.annotation.Nullable;

import com.pitaya.comannotation.Unbinder;
import com.pitaya.commanager.proxy.ProxyTools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Smarking on 17/12/4.
 */

public class ComponentTools {
    //定义组件支持的广播  到这里来注册

    private static String TAG = "ComponentTools";
    private Map<Class<?>, List<?>> mTypesBySubscriber = new HashMap<>();

    private static volatile ComponentTools mInstance;

    private ComponentTools() {
    }

    public static ComponentTools getInstance() {
        if (mInstance == null) {
            synchronized (ComponentTools.class) {
                if (mInstance == null) {
                    mInstance = new ComponentTools();
                }
            }
        }
        return mInstance;
    }

    public static Class getInterfaceClass(Object interfaceInstance) {
        Class<?>[] interfaces = interfaceInstance.getClass().getInterfaces();

        if (interfaces == null) {
            throw new IllegalArgumentException("addProtocol protocolImpl not implement interface");
        }

        if (interfaces.length > 1) {
            throw new IllegalArgumentException("addProtocol protocolImpl implement more than one interface");
        }

        return interfaces[0];
    }


    private final ConcurrentHashMap<Object, Unbinder> mUnBinderCacheMap = new ConcurrentHashMap();

    public ConcurrentHashMap<Object, Unbinder> getUnBinderCacheMap() {
        return mUnBinderCacheMap;
    }

    public synchronized Unbinder registerEventReceiver(Object interfaceInstance) {
        Class<?> interfaceClass = getInterfaceClass(interfaceInstance);
        if (!(interfaceInstance instanceof Proxy)) {
            interfaceInstance = ProxyTools.create(interfaceClass, interfaceInstance);
        }
        Unbinder unbinder = registerEventReceiver(interfaceClass, interfaceInstance);

        mUnBinderCacheMap.put(interfaceInstance, unbinder);
        return unbinder;
    }

    /**
     * 注册状态接受者
     *
     * @param tInterfaceClass
     * @param interfaceInstance
     * @return
     */
    protected synchronized Unbinder registerEventReceiver(final Class<?> tInterfaceClass, final Object interfaceInstance) {
        List oList = mTypesBySubscriber.get(tInterfaceClass);
        if (oList == null) {
            oList = new ArrayList();
            mTypesBySubscriber.put(tInterfaceClass, oList);
        }
        oList.add(interfaceInstance);

        Unbinder unbinder = new Unbinder() {
            @Override
            public synchronized void unbind() {
                List oList = mTypesBySubscriber.get(tInterfaceClass);
                if (oList != null
                        && !oList.isEmpty()
                        && oList.contains(interfaceInstance)
                        && oList.remove(interfaceInstance)) {
                    ELog.d(TAG, tInterfaceClass.getName() + " remove successful");
                } else {
                    ELog.d(TAG, tInterfaceClass.getName() + " remove error or has removed");
                }
            }
        };
        return unbinder;
    }

    public <T> T getCallback(Class<T> tInterfaceClass) {
        return getCallback(tInterfaceClass, null);
    }

    /**
     * MultiResultHandler  merge result from multi to single
     * <p>
     * 仅支持同线程
     *
     * @param tInterfaceClass
     * @param multiResultHandler
     * @param <T>
     * @return
     */
    @Deprecated
    protected <T> T getCallback(Class<T> tInterfaceClass, final MultiResultHandler multiResultHandler) {
        return getCallback(tInterfaceClass, multiResultHandler, null);
    }

    @Deprecated
    protected <T> T getCallback(final Class<T> tInterfaceClass, final MultiResultHandler multiResultHandler, final NoSubscriberHandler noSubscriberHandler) {

        return (T) Proxy.newProxyInstance(tInterfaceClass.getClassLoader(), new Class<?>[]{tInterfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        List<T> instanceList = (List<T>) mTypesBySubscriber.get(tInterfaceClass);

                        if (instanceList == null || instanceList.isEmpty()) {
                            ELog.e(TAG, "instanceList is Empty");
                            if (noSubscriberHandler != null) {
                                noSubscriberHandler.onNoSubscriber();
                            }
                            return null;
                        }

                        if (instanceList.size() == 1) {
                            return method.invoke(instanceList.get(0), args);
                        }

                        List resultList = new ArrayList();
                        for (T instance : instanceList) {
                            Object obj = method.invoke(instance, args);
                            resultList.add(obj);
                        }
                        if (multiResultHandler != null) {
                            return multiResultHandler.convert(resultList);
                        }
                        return null;
                    }
                });
    }


    public interface MultiResultHandler<T> {
        /**
         * @param resultList subElement maybe null
         * @return
         */
        T convert(@Nullable List<T> resultList);
    }

    public interface NoSubscriberHandler {
        /**
         * no Subscriber
         */
        void onNoSubscriber();
    }
}
