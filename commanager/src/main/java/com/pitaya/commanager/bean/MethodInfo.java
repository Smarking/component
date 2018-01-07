package com.pitaya.commanager.bean;

import java.lang.reflect.Method;

public class MethodInfo {
    public Method method;
    public Object target;
    public Object[] args;
    /*package*/ int flags;

    /*package*/ MethodInfo next;
    private static final Object sPoolSync = new Object();
    private static MethodInfo sPool;
    private static volatile int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;

    private MethodInfo() {
    }

    public static MethodInfo obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                MethodInfo m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new MethodInfo();
    }

    public static MethodInfo obtain(Method method, Object target, Object[] args) {
        MethodInfo m = obtain();
        m.method = method;
        m.target = target;
        m.args = args;
        return m;
    }

    private static boolean gCheckRecycle = true;
    /*package*/ static final int FLAG_IN_USE = 1 << 0;


    /*package*/ boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    /*package*/ void markInUse() {
        flags |= FLAG_IN_USE;
    }

    void markUnUse() {
        flags &= ~FLAG_IN_USE;
    }

    public void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This message cannot be recycled because it "
                        + "is still in use.");
            }
            return;
        }
        recycleUnchecked();
    }

    /**
     * Recycles a Message that may be in-use.
     * Used internally by the MessageQueue and Looper when disposing of queued Messages.
     */
    void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = 0;

        target = null;
        args = null;
        method = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
}