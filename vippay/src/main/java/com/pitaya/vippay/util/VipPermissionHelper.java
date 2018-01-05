package com.pitaya.vippay.util;

import com.pitaya.baselib.network.ApiFactory;
import com.pitaya.baselib.network.ApiResponse;
import com.pitaya.vippay.network.VipPayService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VipPermissionHelper {
    private static volatile VipPermissionHelper mInstance;
    public static final String PERMISSION_ERROR = "无会员收银权限，请去管家后台开通";
    private VipPayService mPayService;
    private String mPermissionString;

    private VipPermissionHelper() {
        mPayService = ApiFactory.getApi(VipPayService.class);
    }

    public static VipPermissionHelper getInstance() {
        if (null == mInstance) {
            synchronized (VipPermissionHelper.class) {
                if (null == mInstance) {
                    mInstance = new VipPermissionHelper();
                }
            }
        }
        return mInstance;
    }


    public synchronized boolean hasVipCashPermission() {
        // 核销权限 CASH
        String permissionString = mPermissionString;
        if (permissionString == null) {
            refreshPermission();
            return false;
        }
        Boolean b = null;
        try {
            b = Boolean.valueOf(mPermissionString);
        } catch (Throwable e) {

        }

        if (b == null) {
            refreshPermission();
            return false;
        }

        return b.booleanValue();
    }

    public void refreshPermission() {
        mPayService.getVipPermission()
                .onErrorReturnItem(new ApiResponse<String>(200, "true", null, true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiResponse<String>>() {
                    @Override
                    public void accept(ApiResponse<String> stringApiResponse) throws Exception {
                        mPermissionString = stringApiResponse.getData();
                    }
                });

    }


}

