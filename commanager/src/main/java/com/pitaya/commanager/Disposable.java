package com.pitaya.commanager;

/**
 * Created by Smarking on 18/1/5.
 */

public interface Disposable {
    /**
     * Dispose the resource, the operation should be idempotent.
     */
    void dispose();
}
