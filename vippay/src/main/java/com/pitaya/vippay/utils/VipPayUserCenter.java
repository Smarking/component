package com.pitaya.vippay.utils;

import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

import java.util.List;

/**
 * Created by Smarking on 18/1/2.
 */

public class VipPayUserCenter {

    private VipUserInfo mVipUserInfo;

    private List<Coupon> mCouponList;
    private static VipPayUserCenter mInstance = new VipPayUserCenter();

    private VipPayUserCenter() {

    }

    public static VipPayUserCenter getInstance() {
        return mInstance;
    }

    public VipUserInfo getVipUserInfo() {
        return mVipUserInfo;
    }

    public void setVipUserInfo(VipUserInfo vipUserInfo) {
        this.mVipUserInfo = vipUserInfo;
    }

    public List<Coupon> getCouponList() {
        return mCouponList;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.mCouponList = couponList;
    }
}
