package com.pitaya.commanager.tools;

import android.support.annotation.Nullable;

import com.pitaya.comannotation.annotation.Unbinder;

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

public class ComBroadcastTools {
    //定义组件支持的广播  到这里来注册

    private static String TAG = "ComBroadcastTools";
    private Map<Class<?>, List<?>> mTypesBySubscriber = new HashMap<>();

    private static volatile ComBroadcastTools mInstance;

    private ComBroadcastTools() {
    }

    public static ComBroadcastTools getInstance() {
        if (mInstance == null) {
            synchronized (ComBroadcastTools.class) {
                if (mInstance == null) {
                    mInstance = new ComBroadcastTools();
                }
            }
        }
        return mInstance;
    }

    public static Class getInterface(Object interfaceInstance) {
        Class<?>[] interfaces = interfaceInstance.getClass().getInterfaces();

        if (interfaces == null) {
            throw new IllegalArgumentException("addProtocol protocolImpl not implement interface");
        }

        if (interfaces.length > 1) {
            throw new IllegalArgumentException("addProtocol protocolImpl implement more than one interface");
        }

        return interfaces[0];
    }

    /**
     * ,全局状态，对应的unBinder
     */
    private final ConcurrentHashMap<Object, Unbinder> mUnBinderCacheMap = new ConcurrentHashMap();

    public ConcurrentHashMap<Object, Unbinder> getUnBinderCacheMap() {
        return mUnBinderCacheMap;
    }

    /**
     * 必须是通过ProcessArgsTools.processArgs转化而来的Proxy，否则存在内存泄漏，
     * 因为ThreadProxyHandler.invoke中只能收集到转化为Proxy的入参，未收集到的如果加入mUnBinderCacheMap则无法释放，
     * 引起进程级别的内存泄漏
     *
     * @param proxyInstance
     * @return
     */
    public Unbinder registerEventReceiver(Object proxyInstance) {
        Class<?> interfaceClass = getInterface(proxyInstance);
        if (!(proxyInstance instanceof Proxy)) {
            throw new IllegalArgumentException(proxyInstance.getClass() + " is not Proxy.class, Please use Annotation Subscribe");
        }
        Unbinder unbinder = registerEventReceiver(interfaceClass, proxyInstance);

        mUnBinderCacheMap.put(proxyInstance, unbinder);
        return unbinder;
    }

    /**
     * 注册状态接受者
     *
     * @param tInterfaceClass
     * @param interfaceInstance
     * @return
     */
    protected Unbinder registerEventReceiver(final Class<?> tInterfaceClass, final Object interfaceInstance) {
        synchronized (mTypesBySubscriber) {
            List oList = mTypesBySubscriber.get(tInterfaceClass);
            if (oList == null) {
                oList = new ArrayList();
                mTypesBySubscriber.put(tInterfaceClass, oList);
            }
            oList.add(interfaceInstance);
        }

        Unbinder unbinder = new Unbinder() {
            @Override
            public void unbind() {
                synchronized (mTypesBySubscriber) {
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
                        synchronized (mTypesBySubscriber) {
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
