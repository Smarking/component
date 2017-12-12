package com.pitaya.comprotocol.vippay;

import android.content.Context;

import com.pitaya.comannotation.ProtocolName;
import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.comcallback.Callback1;
import com.pitaya.comcallback.Callback2;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

import java.util.List;

/**
 * Created by Smarking on 17/12/10.
 */
@SuppressWarnings("unused")
@ProtocolName("VipPayComProtocol")
public interface VipPayComProtocol {

    String ComponentName = "com.pitaya.vippay.component.VipPayComponent";

    /**
     * 开启会员支付优惠列表Dialog
     *
     * @param context
     * @param order
     * @param callback
     * @return
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    Unbinder openVipCampaignDialog(Context context, Order order, VipCampaignCallback callback); //TODO  VipCampaignCallback 支持跨页面传递回调、支持回调线程切换

    /**
     * 开启会员储值Dialog
     *
     * @param context
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    void openVipAssetPayDialog(Context context);

    /**
     * 会员结账
     *
     * @param context
     * @param body
     * @param confirmCallback
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void confirmCheckoutVip(Context context, String body, ConfirmCallback confirmCallback);

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void cancelCheckoutVip(Context context, String body, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<String> resultCallback);//TODO 支持属性注解线程切换

    @Subscribe(threadMode = ThreadMode.POSTING)
    VipUserInfo getLoginInfo();

    @Subscribe(threadMode = ThreadMode.POSTING)
    List<Coupon> getVipPayRule();

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface LoginStatus extends Callback1<VipUserInfo> {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface LogoutStatus extends Callback2<VipUserInfo, String> {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface VipCampaignCallback {
        void onPresetPay(String result);

        void onError(String msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface ConfirmCallback {
        void onPresetPay(String result);

        void onCheckout(String result);
    }
}
