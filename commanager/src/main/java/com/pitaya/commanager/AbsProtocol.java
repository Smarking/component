package com.pitaya.commanager;

import com.pitaya.commanager.tools.ELog;

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

        ELog.e(TAG, eventReceiver.getClass().getName() + " unRegister to " + mComLifecycle.getComponentName());
        return false;
    }
}
