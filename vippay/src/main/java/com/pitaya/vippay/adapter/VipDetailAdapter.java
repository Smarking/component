package com.pitaya.vippay.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.elvishew.xlog.XLog;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.vippay.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VipDetailAdapter extends BaseQuickAdapter<Coupon, VipDetailAdapter.BindItemViewHolder> {
    private static final String TAG = "VipDetailAdapter";
    private int mSelectedElePoiPosition = 0;
    private double mConsumption;

    private void clearSelectedStatus() {
        mSelectedElePoiPosition = 0;
    }

    public Coupon getSelectedElePoi() {
        return getData().get(mSelectedElePoiPosition);
    }


    public VipDetailAdapter(List<Coupon> shopIdList, double consumption) {
        super(R.layout.vippay_coupon_item, shopIdList);
        this.mConsumption = consumption;
    }

    @Override
    protected void convert(BindItemViewHolder helper, Coupon coupon) {
        XLog.d(helper.getLayoutPosition() + " " + helper.getItemViewType() + " " + coupon.name);

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
        @BindView(R.id.coupon_name)
        TextView couponName;
        @BindView(R.id.use_rule)
        TextView useRule;

        public BindItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
