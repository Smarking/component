package com.pitaya.comprotocol.checkout;

import android.content.Context;

import com.pitaya.comannotation.ProtocolName;
import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.comcallback.Callback1;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.bean.Coupon;

import java.util.List;

/**
 * Created by Smarking on 17/12/10.
 */
@ProtocolName("CheckoutComProtocol")
public interface CheckoutComProtocol {

    /**
     * 一个协议可对应多个 组件
     */
    String ComponentName = "com.pitaya.checkout.component.CheckoutComponent";//火锅
    String ComponentName2 = "";//烧烤

    void openCheckoutPage(Context context);

    //TODO 如何解决指定入参的执行线程呢
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void sortVipPayCouponsAndUpdateView(List<Coupon> couponList, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<List<Coupon>> sortResult);

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void calculateDiscountAndUpdateView(Order order, Coupon selectedCoupon, @Subscribe(threadMode = ThreadMode.MAIN) Callback1<Float> calculateResult);
}
