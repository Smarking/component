package com.pitaya.commanager;

import com.pitaya.comannotation.Unbinder;
import com.pitaya.commanager.tools.ComponentTools;
import com.pitaya.commanager.tools.ELog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Smarking on 18/1/5.
 */

public abstract class AbsProtocol {
    private static final String TAG = "AbsProtocol";

    ComLifecycle mComLifecycle;

    public void setComponent(ComLifecycle comLifecycle) {
        mComLifecycle = comLifecycle;
    }

    public ComLifecycle getComponent() {
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

        ELog.e(TAG, eventReceiver.getClass().getName() + " unBind to " + mComLifecycle.getComponentName());
        return false;
    }

    private final ConcurrentHashMap<Class, Unbinder> mUnBinderCacheMap = new ConcurrentHashMap();

    /**
     * 设置全局callback，只有一份
     *
     * @param callback
     */
    public final void registerGlobalOnlyOneCallback(Object callback) {
        Class interfaceClass = ComponentTools.getInterfaceClass(callback);

        Unbinder unbinder = mUnBinderCacheMap.remove(interfaceClass);
        if (unbinder != null) {
            unbinder.unbind();
        }
        unbinder = ComponentTools.getInstance().registerEventReceiver(callback);
        mUnBinderCacheMap.put(interfaceClass, unbinder);
    }

    public final boolean registerEventReceiver(Object eventReceiver) {
        if (!isRegisteredEvent(eventReceiver)) {
            return false;
        }
        ComponentTools.getInstance().registerEventReceiver(eventReceiver);
        return true;
    }
}
