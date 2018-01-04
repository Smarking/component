package com.pitaya.comcallback;

/**
 * A one-argument and one-result callback.
 *
 * @param <P>
 * @param <R>
 */
@Deprecated
public interface Callback2<P, R> {
    R call(P param);
}
