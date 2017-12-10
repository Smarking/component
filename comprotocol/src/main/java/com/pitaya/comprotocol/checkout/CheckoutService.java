package com.pitaya.comprotocol.checkout;

import com.pitaya.comannotation.Callback3;
import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.comprotocol.vippay.bean.Coupon;

import java.util.List;

/**
 * Created by Smarking on 17/12/10.
 */

public interface CheckoutService {
    //TODO 如何解决指定入参的执行线程呢
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void sortVipPayCouponsAndUpdateView(List<Coupon> couponList, @Subscribe(threadMode = ThreadMode.MAIN) Callback3<List<Coupon>> sortResult);

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void calculateDiscountAndUpdateView(Coupon selectedCoupon, @Subscribe(threadMode = ThreadMode.MAIN) Callback3<Integer> calculateResult);
}
