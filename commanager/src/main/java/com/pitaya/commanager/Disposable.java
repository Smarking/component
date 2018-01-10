package com.pitaya.commanager;

/**
 * Created by Smarking on 18/1/5.
 */

public interface Disposable {
    /**
     * destroy 防止内存泄漏
     * Dispose the resource, the operation should be idempotent.
     */
    void dispose();
}
