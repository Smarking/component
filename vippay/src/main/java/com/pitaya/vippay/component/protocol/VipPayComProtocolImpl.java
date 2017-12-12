package com.pitaya.vippay.component.protocol;

import android.content.Context;
import android.support.annotation.Nullable;

import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.comcallback.Callback1;
import com.pitaya.commanager.ComponentTools;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

import java.util.List;

/**
 * Created by Smarking on 17/12/12.
 */

public class VipPayComProtocolImpl implements VipPayComProtocol {
    @Override
    public Unbinder openVipCampaignDialog(Context context, Order order, VipCampaignCallback callback) {
        //打开页面

        //TODO 返回Unbinder
        return ComponentTools.getInstance().registerStatusReceiver(VipCampaignCallback.class, callback);
    }

    @Override
    public void openVipAssetPayDialog(Context context) {

    }

    @Override
    public void confirmCheckoutVip(Context context, String body, ConfirmCallback confirmCallback) {

    }

    @Override
    public void cancelCheckoutVip(Context context, String body, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<String> resultCallback) {

    }

    @Nullable
    @Override
    public VipUserInfo getLoginInfo() {
        return null;
    }

    @Nullable
    @Override
    public List<Coupon> getVipPayRule() {
        return null;
    }
}
