package com.pitaya.commanager;

import com.pitaya.comannotation.annotation.Unbinder;
import com.pitaya.commanager.tools.ComponentTools;
import com.pitaya.commanager.tools.ELog;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Smarking on 18/1/5.
 */

public abstract class AbsProtocol {
    private static final String TAG = "AbsProtocol";
    private final ConcurrentHashMap<Class, Unbinder> mUnBinderCacheMap = new ConcurrentHashMap();
    protected ComLifecycle mComLifecycle;

    public final void setComponent(ComLifecycle comLifecycle) {
        mComLifecycle = comLifecycle;
    }

    public final ComLifecycle getComponent() {
        return mComLifecycle;
    }

    public final boolean isRegisteredEvent(Object eventReceiver) {
        Class<?>[] classes = mComLifecycle.getEvent();
        if (classes == null) {
            return false;
        }

        Class<?>[] eventInterfaces = eventReceiver.getClass().getInterfaces();
        if (eventInterfaces == null) {
            ELog.e(TAG, "-isRegisteredEvent- is not from interface, " + eventReceiver.getClass().getName());
            return false;
        }

        if (eventInterfaces.length > 1) {
            ELog.e(TAG, "-isRegisteredEvent- more than one interface, " + eventReceiver.getClass().getName());
            return false;
        }

        for (Class c : classes) {
            if (c.equals(eventInterfaces[0])) {
                return true;
            }
        }

        ELog.e(TAG, eventReceiver.getClass().getName() + " unbind to " + mComLifecycle.getComponentName());
        return false;
    }

    /**
     * 设置全局callback，只有一份
     *
     * @param proxyCallback
     */
    public final void registerGlobalOnlyOneCallback(Object proxyCallback) {
        if (!(proxyCallback instanceof Proxy)) {
            throw new IllegalArgumentException(proxyCallback.getClass() + " is not Proxy.class, Please use Annotation Subscribe");
        }

        Class interfaceClass = ComponentTools.getInterfaceClass(proxyCallback);

        Unbinder unbinder = mUnBinderCacheMap.remove(interfaceClass);
        if (unbinder != null) {
            unbinder.unbind();
        }
        unbinder = ComponentTools.getInstance().registerEventReceiver(proxyCallback);
        mUnBinderCacheMap.put(interfaceClass, unbinder);
    }

    public final boolean registerEventReceiver(Object proxyEventReceiver) {
        if (!(proxyEventReceiver instanceof Proxy)) {
            throw new IllegalArgumentException(proxyEventReceiver.getClass() + " is not Proxy.class, Please use Annotation Subscribe");
        }

        if (!isRegisteredEvent(proxyEventReceiver)) {
            return false;
        }
        ComponentTools.getInstance().registerEventReceiver(proxyEventReceiver);
        return true;
    }
}
