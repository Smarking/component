package com.pitaya.commanager;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pitaya.comannotation.ProtocolName;
import com.pitaya.comannotation.Unbinder;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ComManager {
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
            } catch (Exception e) {
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
                mComponents.get(componentClassName).unInstall();
                mComponents.remove(componentClassName);
            }
        } finally {
            mWriteLock.unlock();
        }
    }

    /**
     * 获取组件能力接口
     *
     * @param tProtocolClass 协议类class
     * @param <T>
     * @return
     */
    public <T> T getProtocol(Class<T> tProtocolClass) {
        if (!tProtocolClass.isInterface()) {
            return null;
        }
        ProtocolName annotation = tProtocolClass.getAnnotation(ProtocolName.class);
        if (annotation == null) {
            throw new NullPointerException("getProtocol protocolName is null , protocolClass is " + tProtocolClass.getName());
        }
        String protocolName = annotation.value();

        mReadLock.lock();
        try {
            Iterator<Map.Entry<String, ComLifecycle>> iterator = mComponents.entrySet().iterator();
            while (iterator.hasNext()) {
                ComLifecycle comLifecycle = iterator.next().getValue();
                T protocol = comLifecycle.getProtocol(protocolName);
                if (protocol != null) {
                    return protocol;
                }
            }
            return null;
        } finally {
            mReadLock.unlock();
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

    /**
     * 不依赖与
     *
     * @param tInterfaceClass
     * @param statusReceiver
     * @return
     */
    public Unbinder registerStatusReceiver(final Class<?> tInterfaceClass, final Object statusReceiver) {
        return ComponentTools.getInstance().registerStatusReceiver(tInterfaceClass, statusReceiver);
    }

    public <T> T getReceiver(Class<T> tInterfaceClass) {
        return ComponentTools.getInstance().getCallback(tInterfaceClass);
    }

    public <T> T getReceiver(Class<T> tInterfaceClass, ComponentTools.MultiResultHandler multiResultHandler) {
        return ComponentTools.getInstance().getCallback(tInterfaceClass, multiResultHandler);
    }

    public <T> T getReceiver(Class<T> tInterfaceClass, ComponentTools.MultiResultHandler multiResultHandler, ComponentTools.NoSubscriberHandler noSubscriberHandler) {
        return ComponentTools.getInstance().getCallback(tInterfaceClass, multiResultHandler, noSubscriberHandler);
    }


}

