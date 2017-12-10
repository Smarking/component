package com.pitaya.comannotation;

/**
 * A one-argument callback.
 *
 * @param <P> the first argument type
 */
public interface Callback1<P> {
    void call(P param);
}
