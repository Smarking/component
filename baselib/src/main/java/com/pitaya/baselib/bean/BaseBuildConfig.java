package com.pitaya.baselib.bean;

public class BaseBuildConfig {
    public String deviceId;
    public String osType;
    public boolean envOffline;
    /**
     * TYPE_BETA = 101;
     * TYPE_TEST = 201;
     * TYPE_ST = 301;
     * TYPE_PROD = 401;
     */
    public int envUrlType;

}
