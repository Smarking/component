package com.pitaya.vippay.component;

import com.pitaya.commanager.BaseComLifecycle;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.vippay.component.protocol.VipPayComProtocolImpl;
import com.pitaya.vippay.util.VipPermissionHelper;

/**
 * Created by Smarking on 17/12/12.
 */
@SuppressWarnings("unused")
public class VipPayComponent extends BaseComLifecycle {

    @Override
    protected boolean onInstall() {
        //TODO AOP判断是否有核销权限
        addProtocol(new VipPayComProtocolImpl());
        return true;
    }

    @Override
    public void onUserCenterChanged() {
        super.onUserCenterChanged();
        //请求后端判断是否会员核销权限
        VipPermissionHelper.getInstance().refreshPermission();
    }

    @Override
    public String getComponentName() {
        return VipPayComProtocol.ComponentName;
    }

    @Override
    public Class<?>[] getEvent() {
        //不在这里登记,不允许注册事件到当前组件。VipPayComProtocolImpl.registerEventReceiver
        return new Class[]{VipPayComProtocol.LoginEvent.class, VipPayComProtocol.LogoutEvent.class};
    }
}
