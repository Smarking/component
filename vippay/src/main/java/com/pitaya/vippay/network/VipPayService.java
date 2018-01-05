package com.pitaya.vippay.network;

import com.pitaya.baselib.network.ApiResponse;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Smarking on 18/1/2.
 */

public interface VipPayService {

    @GET("api/vippay/getVipPermission")
    Observable<ApiResponse<String>> getVipPermission();

    @POST("api/vippay/login")
    Observable<ApiResponse<VipUserInfo>> loginVipPay(@Body String phoneNum);

    @POST("api/vippay/logout")
    Observable<ApiResponse<String>> logoutVipPay(@Body String vipid);

    @GET("api/vippay/getCouponList")
    Observable<ApiResponse<List<Coupon>>> getCouponList(@Query("vipid") String vipid);

    @POST("api/vippay/presetPay")
    Observable<ApiResponse<String>> presetPay(@Body Coupon coupon);

    @POST("api/vippay/pay")
    Observable<ApiResponse<String>> pay(@Body Coupon coupon);
}
