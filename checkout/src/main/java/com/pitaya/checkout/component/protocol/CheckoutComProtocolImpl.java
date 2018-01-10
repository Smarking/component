package com.pitaya.checkout.component.protocol;

import android.content.Context;

import com.pitaya.checkout.CheckoutActivity;
import com.pitaya.comannotation.comcallback.Callback1;
import com.pitaya.commanager.AbsProtocol;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.bean.Coupon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Smarking on 17/12/12.
 */

public class CheckoutComProtocolImpl extends AbsProtocol implements CheckoutComProtocol {

    @Override
    public void openCheckoutPage(Context context) {
        CheckoutActivity.launch(context);
    }

    @Override
    public void sortVipPayCoupons(List<Coupon> couponList, Callback1<List<Coupon>> sortResult) {
        Collections.sort(couponList, new Comparator<Coupon>() {
            @Override
            public int compare(Coupon o1, Coupon o2) {
                int compare = Float.compare(o1.discount, o2.discount);
                if (compare == 0) {
                    if (o1.rule == o2.rule) {
                        return 0;
                    }
                    if (o1.rule > o2.rule) {
                        return 1;
                    }
                    if (o1.rule < o2.rule) {
                        return -1;
                    }
                }

                return compare;
            }
        });

        try {
            //TODO 模拟耗时任务，存在内存泄漏
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }

        sortResult.call(couponList);
    }

    @Override
    public void calculateDiscount(Order order, Coupon selectedCoupon, Callback1<Float> calculateResult) {
        calculateResult.call(((1 - selectedCoupon.discount) * order.amount));
    }
}
