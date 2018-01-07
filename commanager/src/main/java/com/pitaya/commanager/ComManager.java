package com.pitaya.commanager;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pitaya.comannotation.ProtocolName;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.commanager.proxy.ProxyTools;
import com.pitaya.commanager.tools.ComponentTools;
import com.pitaya.commanager.tools.ELog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ComManager {
    //TODO* 保证线程安全，一组操作的原子性（事务）
    private Map<String, ComLifecycle> mComponents = new LinkedHashMap<>();
    private ReentrantReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.WriteLock mWriteLock = mReadWriteLock.writeLock();
    ReentrantReadWriteLock.ReadLock mReadLock = mReadWriteLock.readLock();
    private static volatile ComManager mInstance;

    private ComManager() {
    }

    public static ComManager getInstance() {
        if (mInstance == null) {
            synchronized (ComManager.class) {
                if (mInstance == null) {
                    mInstance = new ComManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 安装组件
     *
     * @param componentClassName 组件类名
     */
    public ComLifecycle installComponent(Application application, @NonNull String componentClassName) {
        if (TextUtils.isEmpty(componentClassName)) {
            throw new NullPointerException("installComponent componentClassName is null");
        }

        mWriteLock.lock();
        try {
            if (mComponents.containsKey(componentClassName)) {
                return mComponents.get(componentClassName);
            }

            try {
                Class clazz = Class.forName(componentClassName);
                ComLifecycle lifecycle = (ComLifecycle) clazz.newInstance();
                lifecycle.install(application);
                mComponents.put(componentClassName, lifecycle);
                return lifecycle;
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;

        } finally {
            mWriteLock.unlock();
        }
    }

    /**
     * 组件是否可用
     *
     * @param componentClassName 组件类名
     * @return
     */
    public boolean isAlive(@NonNull String componentClassName) {
        mReadLock.lock();
        try {
            ComLifecycle comLifecycle = mComponents.get(componentClassName);
            return comLifecycle == null ? false : comLifecycle.isAlive();
        } finally {
            mReadLock.unlock();
        }
    }

    /**
     * 卸载组件
     *
     * @param componentClassName 组件类名
     */
    public void unInstallComponent(@NonNull String componentClassName) {
        if (TextUtils.isEmpty(componentClassName)) {
            return;
        }
        mWriteLock.lock();
        try {
            if (mComponents.containsKey(componentClassName)) {
                ComLifecycle comLifecycle = mComponents.remove(componentClassName);
                comLifecycle.unInstall();
            }
        } finally {
            mWriteLock.unlock();
        }
    }

    /**
     * TODO  re（protocol） unre(pro)
     * 获取组件能力接口
     *
     * @param tProtocolClass 协议类class
     * @param <T>
     * @return
     */
    public <T> T getProtocolAndBind(final Object object, final Class<T> tProtocolClass) {
        if (!tProtocolClass.isInterface()) {
            return null;
        }
        ProtocolName annotation = tProtocolClass.getAnnotation(ProtocolName.class);
        if (annotation == null) {
            throw new NullPointerException("getProtocolAndBind protocolName is null , protocolClass is " + tProtocolClass.getName());
        }
        String protocolName = annotation.value();

        mReadLock.lock();
        try {
            Iterator<Map.Entry<String, ComLifecycle>> iterator = mComponents.entrySet().iterator();
            while (iterator.hasNext()) {
                ComLifecycle comLifecycle = iterator.next().getValue();
                T protocol = comLifecycle.getProtocol(protocolName);
                if (protocol != null) {

                    //线程安全
                    initCacheMap(object);

                    if (mDisposeCacheMap.get(object).containsKey(tProtocolClass)) {
                        return (T) mDisposeCacheMap.get(object).get(tProtocolClass);
                    } else {
                        Object proxyProtocolImpl = ProxyTools.create(protocol.getClass().getInterfaces()[0],
                                Disposable.class, protocol);
                        mDisposeCacheMap.get(object).put(tProtocolClass, ((Disposable) proxyProtocolImpl));
                        return (T) proxyProtocolImpl;
                    }
                }
            }

            //返回一个代理，防止调用NullPointException
            return (T) Proxy.newProxyInstance(tProtocolClass.getClassLoader(), new Class<?>[]{tProtocolClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            ELog.e("ComManager", "ComManager not support " + tProtocolClass.getName());
                            return null;
                        }
                    });
        } finally {
            mReadLock.unlock();
        }
    }

    public void unBind(Object object) {
        clearCacheMap(object);
    }

    private static final ConcurrentHashMap<Object, Map<Class, Disposable>> mDisposeCacheMap = new ConcurrentHashMap();
    private final ReentrantLock mReentrantLock = new ReentrantLock();

    /**
     * ConcurrentHashMap无法保证一组操作的原子性
     *
     * @param object
     */
    private void initCacheMap(Object object) {
        if (!mDisposeCacheMap.containsKey(object)) {
            mReentrantLock.lock();
            try {
                if (!mDisposeCacheMap.containsKey(object)) {
                    mDisposeCacheMap.put(object, new HashMap<Class, Disposable>());
                }
            } finally {
                mReentrantLock.unlock();
            }
        }
    }

    private void clearCacheMap(Object object) {
        mReentrantLock.lock();
        try {
            Map<Class, Disposable> map = mDisposeCacheMap.remove(object);
            if (map == null) {
                return;
            }
            for (Map.Entry<Class, Disposable> protocol : map.entrySet()) {
                protocol.getValue().dispose();
            }
            map.clear();
        } finally {
            mReentrantLock.unlock();
        }
    }

    public void notifyBuildConfigChanged() {
        mReadLock.lock();
        try {
            Iterator<Map.Entry<String, ComLifecycle>> iterator = mComponents.entrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().getValue().onBuildConfigChanged();
            }
        } finally {
            mReadLock.unlock();
        }
    }

    public void notifyUserCenterChanged() {
        mReadLock.lock();
        try {
            Iterator<Map.Entry<String, ComLifecycle>> iterator = mComponents.entrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().getValue().onUserCenterChanged();
            }
        } finally {
            mReadLock.unlock();
        }
    }

    public <T> T getEventReceiver(Class<T> eventInterfaceClass) {
        return ComponentTools.getInstance().getCallback(eventInterfaceClass);
    }

    public <T> T getGlobalOnlyOneCallbackReceiver(Class<T> eventInterfaceClass) {
        return ComponentTools.getInstance().getCallback(eventInterfaceClass);
    }
}

