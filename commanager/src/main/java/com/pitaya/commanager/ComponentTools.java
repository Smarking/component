package com.pitaya.commanager;

import android.support.annotation.Nullable;
import android.util.Log;

import com.pitaya.comannotation.Unbinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public synchronized Unbinder registerStatusReceiver(final Object interfaceInstance) {
        Class<?>[] interfaces = interfaceInstance.getClass().getInterfaces();

        if (interfaces == null) {
            throw new IllegalArgumentException("addProtocol protocolImpl not implement interface");
        }

        if (interfaces.length > 1) {
            throw new IllegalArgumentException("addProtocol protocolImpl implement more than one interface");
        }
        return registerStatusReceiver(interfaces[0], interfaceInstance);
    }


    /**
     * 注册状态接受者
     *
     * @param tInterfaceClass
     * @param interfaceInstance
     * @return
     */
    public synchronized Unbinder registerStatusReceiver(final Class<?> tInterfaceClass, final Object interfaceInstance) {
        List oList = mTypesBySubscriber.get(tInterfaceClass);
        if (oList == null) {
            oList = new ArrayList();
            mTypesBySubscriber.put(tInterfaceClass, oList);
        }
        oList.add(interfaceInstance);

        return new Unbinder() {
            @Override
            public synchronized void unbind() {
                List oList = mTypesBySubscriber.get(tInterfaceClass);
                if (oList != null
                        && !oList.isEmpty()
                        && oList.contains(interfaceInstance)
                        && oList.remove(interfaceInstance)) {
                    Log.d(TAG, tInterfaceClass.getName() + " remove successful");
                } else {
                    Log.d(TAG, tInterfaceClass.getName() + " remove error");
                }
            }
        };
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
    public <T> T getCallback(Class<T> tInterfaceClass, final MultiResultHandler multiResultHandler) {
        return getCallback(tInterfaceClass, multiResultHandler, null);
    }

    @Deprecated
    public <T> T getCallback(final Class<T> tInterfaceClass, final MultiResultHandler multiResultHandler, final NoSubscriberHandler noSubscriberHandler) {

        return (T) Proxy.newProxyInstance(tInterfaceClass.getClassLoader(), new Class<?>[]{tInterfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        List<T> instanceList = (List<T>) mTypesBySubscriber.get(tInterfaceClass);

                        if (instanceList == null || instanceList.isEmpty()) {
                            Log.e(TAG, "instanceList is Empty");
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
