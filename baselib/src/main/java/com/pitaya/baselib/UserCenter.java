package com.pitaya.baselib;

import com.pitaya.baselib.bean.BaseUserInfo;

/**
 * Created by Smarking on 17/12/12.
 */

public class UserCenter {

    private BaseUserInfo mBaseUserInfo;

    private UserCenter() {
    }

    private static volatile UserCenter mInstance;

    public static UserCenter getInstance() {
        if (mInstance == null) {
            synchronized (UserCenter.class) {
                if (mInstance == null) {
                    mInstance = new UserCenter();
                }
            }
        }
        return mInstance;
    }

    public void init(BaseUserInfo baseUserInfo) {
        mBaseUserInfo = new BaseUserInfo();
        mBaseUserInfo.loginToken = baseUserInfo.loginToken;
        mBaseUserInfo.loginUserName = baseUserInfo.loginUserName;
        mBaseUserInfo.poiId = baseUserInfo.poiId;
        mBaseUserInfo.tenantId = baseUserInfo.tenantId;
    }

    public String getLoginToken() {
        return mBaseUserInfo == null ? null : mBaseUserInfo.loginToken;
    }
}
