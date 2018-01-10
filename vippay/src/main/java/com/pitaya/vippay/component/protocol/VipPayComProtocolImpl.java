package com.pitaya.vippay.component.protocol;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.pitaya.baselib.network.ApiFactory;
import com.pitaya.baselib.network.ApiResponse;
import com.pitaya.comannotation.annotation.Subscribe;
import com.pitaya.comannotation.annotation.ThreadMode;
import com.pitaya.comannotation.comcallback.Callback1;
import com.pitaya.commanager.AbsProtocol;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;
import com.pitaya.vippay.dialog.VerifyPhoneDialog;
import com.pitaya.vippay.network.VipPayService;
import com.pitaya.vippay.util.VipPayUserCenter;
import com.pitaya.vippay.util.VipPermissionHelper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Smarking on 17/12/12.
 */

public class VipPayComProtocolImpl extends AbsProtocol implements VipPayComProtocol {

    VipPayService mVipPayService = ApiFactory.getApi(VipPayService.class);

    @Override
    public void openVipCampaignDialog(FragmentActivity context, Order order, VipCampaignCallback callback) {
        //无核销权限
        if (!VipPermissionHelper.getInstance().hasVipCashPermission()) {
        }

        //TODO 如何获取一次流程的生命周期回调呢？ 不允许这样用了
        registerGlobalOnlyOneCallback(callback);

        //打开页面
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        VerifyPhoneDialog.newInstance(fragmentManager, order).show(fragmentManager, VerifyPhoneDialog.class.getName());
    }

    @Override
    public void openVipAssetPayDialog(Context context) {
        // to do something  使用会员金额
    }

    @Override
    public void confirmCheckoutVip(Coupon coupon, final ConfirmCallback confirmCallback) {
        mVipPayService.pay(coupon)
                .onErrorReturnItem(new ApiResponse<String>(200, "succeed", null, true))
                .map(new Function<ApiResponse<String>, ApiResponse<String>>() {
                    @Override
                    public ApiResponse<String> apply(ApiResponse<String> stringApiResponse) throws Exception {
                        Thread.sleep(3000);
                        return stringApiResponse;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiResponse<String>>() {
                    @Override
                    public void accept(ApiResponse<String> stringApiResponse) throws Exception {
                        confirmCallback.onCheckout("succeed");
                    }
                });
    }

    @Override
    public void cancelCheckoutVip(Context context, String body, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<String> resultCallback) {
        // to do something 取消支付
    }

    @Override
    public void requestLogout() {
        if (VipPayUserCenter.getInstance().getVipUserInfo() == null) {
            return;
        }
        mVipPayService.logoutVipPay(VipPayUserCenter.getInstance().getVipUserInfo().vipId)
                .onErrorReturnItem(new ApiResponse<String>(200, "succeed", null, true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiResponse<String>>() {
                    @Override
                    public void accept(ApiResponse<String> stringApiResponse) throws Exception {
                        VipPayUserCenter.getInstance().setCouponList(null);
                        VipPayUserCenter.getInstance().setVipUserInfo(null);
                    }
                });
    }

    @Nullable
    @Override
    public VipUserInfo getLoginInfo() {
        return VipPayUserCenter.getInstance().getVipUserInfo();
    }

    @Nullable
    @Override
    public List<Coupon> getVipPayRule() {
        return VipPayUserCenter.getInstance().getCouponList();
    }

    @Override
    public void registerEventReceiver(LoginEvent eventReceiver) {
        super.registerEventReceiver(eventReceiver);
    }

    @Override
    public void registerEventReceiver(LogoutEvent eventReceiver) {
        super.registerEventReceiver(eventReceiver);
    }
}
