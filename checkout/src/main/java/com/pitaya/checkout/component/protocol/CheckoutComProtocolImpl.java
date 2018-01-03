package com.pitaya.checkout.component.protocol;

import com.pitaya.comcallback.Callback1;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.bean.Coupon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Smarking on 17/12/12.
 */

public class CheckoutComProtocolImpl implements CheckoutComProtocol {

    @Override
    public void sortVipPayCouponsAndUpdateView(List<Coupon> couponList, Callback1<List<Coupon>> sortResult) {
        Collections.sort(couponList, new Comparator<Coupon>() {
            @Override
            public int compare(Coupon o1, Coupon o2) {
                if (o1.rule == o2.rule) {
                    return Float.compare(o1.discount, o2.discount);
                }
                if (o1.rule > o2.rule) {
                    return 1;
                }
                if (o1.rule < o2.rule) {
                    return -1;
                }
                return 0;
            }
        });

        sortResult.call(couponList);
    }

    @Override
    public void calculateDiscountAndUpdateView(Order order, Coupon selectedCoupon, Callback1<Float> calculateResult) {
        calculateResult.call((selectedCoupon.discount * order.amount));
    }
}
