package com.pitaya.checkout.component;

import com.pitaya.checkout.component.protocol.CheckoutComProtocolImpl;
import com.pitaya.commanager.BaseComLifecycle;

/**
 * Created by Smarking on 17/12/12.
 */
@SuppressWarnings("unused")
public class CheckoutComponent extends BaseComLifecycle {

    @Override
    protected boolean onInstall() {
        addProtocol(new CheckoutComProtocolImpl());
        return true;
    }

    @Override
    public void onUserCenterChanged() {
        super.onUserCenterChanged();
    }

    @Override
    public String getComponentName() {
        return CheckoutComProtocolImpl.ComponentName;
    }

    @Override
    public Class<?>[] getEvent() {
        //TODO 不在这里注册的不允许对外发布 广播场景
        return null;
    }
}
