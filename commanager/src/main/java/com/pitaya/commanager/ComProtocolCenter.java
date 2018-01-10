package com.pitaya.commanager;

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Smarking on 17/12/12.
 */

public class ComProtocolCenter {
    private ReentrantReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.WriteLock mWriteLock = mReadWriteLock.writeLock();
    ReentrantReadWriteLock.ReadLock mReadLock = mReadWriteLock.readLock();

    private Map<String, Object> mProtocols = new ConcurrentHashMap<>();

    public void addProtocol(@NonNull String protocolName, @NonNull Object protocolImpl) {
        if (protocolName == null || protocolImpl == null) {
            return;
        }
        mWriteLock.lock();
        try {
            mProtocols.put(protocolName, protocolImpl);
        } finally {
            mWriteLock.unlock();
        }
    }

    public Object getProtocol(@NonNull String protocolName) {
        if (protocolName == null) {
            return null;
        }
        mReadLock.lock();
        try {
            return mProtocols.get(protocolName);
        } finally {
            mReadLock.unlock();
        }
    }

    public void removeProtocol(@NonNull String protocolName) {
        if (protocolName == null) {
            return;
        }
        mWriteLock.lock();
        try {
            mProtocols.remove(protocolName);
        } finally {
            mWriteLock.unlock();
        }
    }

    public void removeAllProtocol() {
        mWriteLock.lock();
        try {
            mProtocols.clear();
        } finally {
            mWriteLock.unlock();
        }
    }


}
