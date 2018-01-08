package com.pitaya.commanager.exception;

/**
 * An {@link RuntimeException} thrown in cases something went wrong inside EventBus.
 * 
 * @author Markus
 * 
 */
public class ComException extends RuntimeException {

    private static final long serialVersionUID = -2912559384646531479L;

    public ComException(String detailMessage) {
        super(detailMessage);
    }

    public ComException(Throwable throwable) {
        super(throwable);
    }

    public ComException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}