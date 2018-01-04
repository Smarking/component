package com.pitaya.comprotocol.vippay;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.pitaya.comannotation.ProtocolName;
import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.comcallback.Callback1;
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

    /*****当前协议对应组件列表*****/
    String ComponentName = "com.pitaya.vippay.component.VipPayComponent";


    /******组件可提供的能力列表****/

    /**
     * 开启会员支付优惠列表Dialog
     *
     * @param context
     * @param order
     * @param callback
     * @return
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    Unbinder openVipCampaignDialog(FragmentActivity context, Order order, VipCampaignCallback callback); //TODO  VipCampaignCallback 支持跨页面传递回调、支持回调线程切换

    /**
     * 开启会员储值Dialog
     *
     * @param context
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    void openVipAssetPayDialog(Context context);

    /**
     * 会员结账服务接口
     *
     * @param body
     * @param confirmCallback
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void confirmCheckoutVip(Coupon body, ConfirmCallback confirmCallback);

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void cancelCheckoutVip(Context context, String body, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<String> resultCallback);//TODO 支持属性注解线程切换

    @Subscribe(threadMode = ThreadMode.POSTING)
    void requestLogout();

    @Subscribe(threadMode = ThreadMode.POSTING)
    VipUserInfo getLoginInfo();

    @Subscribe(threadMode = ThreadMode.POSTING)
    List<Coupon> getVipPayRule();

    /**
     * 这个方法有毒，容易造成内存泄漏
     * @param statusReceiver
     * @param <T>
     * @return
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    <T> Unbinder registerStatusReceiver(T statusReceiver);


    /******组件状态****/

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface LoginStatus extends Callback1<VipUserInfo> {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface LogoutStatus extends Callback1<VipUserInfo> {
    }


    /******Callback****/

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface VipCampaignCallback extends Unbinder {
        /**
         * 排序后的优惠券列表，给副屏展示
         *
         * @param sortedList
         */
        void onSortedCouponList(List<Coupon> sortedList);

        /**
         * 选中的优惠，给副屏展示
         *
         * @param calculateInfo
         */
        void onSelectedCoupon(String calculateInfo);

        /**
         * 预结账成功回调，给副屏展示
         *
         * @param coupon
         */
        void onPresetPay(Coupon coupon);

        void onError(String msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    interface ConfirmCallback {
        void onCheckout(String result);
    }
}
