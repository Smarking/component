package com.pitaya.commanager.exception;

import java.lang.reflect.Method;

public class MethodInfo {
    public Method method;
    public Object target;
    public Object[] args;

    public MethodInfo(Method method, Object target, Object[] args) {
        this.method = method;
        this.target = target;
        this.args = args;
    }
}