package com.pitaya.comprotocol.vippay.bean;

/**
 * Created by Smarking on 17/12/11.
 */

public class VipUserInfo {
    public String vipName;
    public String vipId;
    public double vipMoney;

    public VipUserInfo(String vipName, String vipId, double vipMoney) {
        this.vipName = vipName;
        this.vipId = vipId;
        this.vipMoney = vipMoney;
    }

    @Override
    public String toString() {
        return "VipUserInfo{" +
                "vipName='" + vipName + '\'' +
                ", vipId='" + vipId + '\'' +
                ", vipMoney=" + vipMoney +
                '}';
    }
}
