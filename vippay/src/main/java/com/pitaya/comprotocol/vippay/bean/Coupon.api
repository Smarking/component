package com.pitaya.comprotocol.vippay.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Coupon implements Parcelable {
    public int id;
    public String name;
    public int rule;
    public float discount;

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rule=" + rule +
                ", discount=" + discount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coupon coupon = (Coupon) o;

        if (rule != coupon.rule) return false;
        return Float.compare(coupon.discount, discount) == 0;
    }

    @Override
    public int hashCode() {
        int result = rule;
        result = 31 * result + (discount != +0.0f ? Float.floatToIntBits(discount) : 0);
        return result;
    }

    public Coupon(int id, String name, int rule, float discount) {
        this.id = id;
        this.name = name;
        this.rule = rule;
        this.discount = discount;
    }

    protected Coupon(Parcel in) {
        id = in.readInt();
        name = in.readString();
        rule = in.readInt();
        discount = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(rule);
        dest.writeFloat(discount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };
}