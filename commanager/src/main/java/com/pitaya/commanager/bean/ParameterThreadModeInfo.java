package com.pitaya.commanager.bean;

import com.pitaya.comannotation.annotation.Subscribe;

public class ParameterThreadModeInfo {
    public int index;
    public boolean isFromParameterAnnotation = false;
    //isFromParameterAnnotation为Ture时 才生效
    public Subscribe threadMode;
    public Class<?> parameterClass;

    public ParameterThreadModeInfo(int index, boolean isFromParameterAnnotation, Subscribe threadMode, Class<?> parameterClass) {
        this.index = index;
        this.isFromParameterAnnotation = isFromParameterAnnotation;
        this.threadMode = threadMode;
        this.parameterClass = parameterClass;
    }
}