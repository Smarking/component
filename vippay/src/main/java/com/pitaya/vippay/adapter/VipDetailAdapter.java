package com.pitaya.vippay.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.vippay.R;
import com.pitaya.vippay.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VipDetailAdapter extends BaseQuickAdapter<Coupon, VipDetailAdapter.BindItemViewHolder> {
    private static final String TAG = "VipDetailAdapter";
    private int mSelectedElePoiPosition = -1;
    private double mConsumption;

    private void clearSelectedStatus() {
        mSelectedElePoiPosition = -1;
    }

    public Coupon getSelectedElePoi() {
        Coupon coupon = null;
        try {
            coupon = getData().get(mSelectedElePoiPosition);
        } catch (Throwable e) {
        }
        return coupon;
    }


    public VipDetailAdapter(List<Coupon> shopIdList, double consumption) {
        super(R.layout.vippay_coupon_item, shopIdList);
        this.mConsumption = consumption;
    }

    @Override
    protected void convert(BindItemViewHolder helper, Coupon coupon) {
        if (coupon.rule > mConsumption) {
            helper.itemView.setEnabled(false);
        } else {
            helper.itemView.setEnabled(true);
        }

        helper.couponName.setText(coupon.name);
        helper.useRule.setText("消费满" + coupon.rule + "元可用");

        helper.itemView.setSelected(
                mSelectedElePoiPosition == (helper.getLayoutPosition() - getHeaderLayoutCount()));
    }

    public void updateViewStatus(int position) {
        clearSelectedStatus();
        mSelectedElePoiPosition = position;
        VipDetailAdapter.this.notifyDataSetChanged();
    }

    public static class BindItemViewHolder extends BaseViewHolder {
        @BindView(R2.id.coupon_name)
        TextView couponName;
        @BindView(R2.id.use_rule)
        TextView useRule;

        public BindItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
