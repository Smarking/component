package com.pitaya.comannotation.comcallback;

/**
 * A one-argument callback.
 *
 * @param <P> the first argument type
 */
public interface Callback1<P> {
    void call(P param);
}
