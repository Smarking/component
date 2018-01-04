package com.pitaya.commanager.exception;

import com.pitaya.comannotation.Subscribe;

public class ParameterThreadModeInfo {
    public int index;
    public boolean isFromParameterAnnotation = false;
    public Subscribe threadMode;
    public Class<?> parameterClass;

    public ParameterThreadModeInfo(int index, boolean isFromParameterAnnotation, Subscribe threadMode, Class<?> parameterClass) {
        this.index = index;
        this.isFromParameterAnnotation = isFromParameterAnnotation;
        this.threadMode = threadMode;
        this.parameterClass = parameterClass;
    }
}