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

    @Override
    public Class<?>[] getEvent() {
        //TODO 不在这里注册的不允许对外发布 广播场景
        return new Class[]{VipPayComProtocol.LoginStatus.class, VipPayComProtocol.LogoutStatus.class};
    }
}
