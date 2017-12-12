package com.pitaya.vippay.component;

import com.pitaya.commanager.BaseComLifecycle;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.vippay.component.protocol.VipPayComProtocolImpl;

/**
 * Created by Smarking on 17/12/12.
 */
@SuppressWarnings("unused")
public class VipPayComponent extends BaseComLifecycle {

    @Override
    protected boolean onInstall() {
        addProtocol(new VipPayComProtocolImpl());
        return true;
    }

    @Override
    public void onUserCenterChanged() {
        super.onUserCenterChanged();
        //请求后端判断 是否会员核销权限

        //网络请求
    }

    @Override
    public String getComponentName() {
        return VipPayComProtocol.ComponentName;
    }

}
