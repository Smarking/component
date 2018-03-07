package com.pitaya.comprotocol.checkout.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Smarking on 17/12/10.
 */

public class Order implements Parcelable {
    public String orderId;//订单ID
    public Float amount;//订单总金额，单位元
    public Float receivable;//应收，单位元

    public Order() {
    }

    protected Order(Parcel in) {
        orderId = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readFloat();
        }
        if (in.readByte() == 0) {
            receivable = null;
        } else {
            receivable = in.readFloat();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(amount);
        }
        if (receivable == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(receivable);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
