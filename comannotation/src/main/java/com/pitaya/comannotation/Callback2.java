package com.pitaya.comannotation;

/**
 * A one-argument and one-result callback.
 *
 * @param <P>
 * @param <R>
 */
public interface Callback2<P, R> {
    R call(P param);
}
