package com.pitaya.comprotocol.vippay.bean;

public class Coupon {
    public int id;
    public String name;
    public int rule;
    public float discount;
    public boolean isSelected = false;//View 字段

    public Coupon(int id, String name, int rule, float discount) {
        this.id = id;
        this.name = name;
        this.rule = rule;
        this.discount = discount;
    }
}