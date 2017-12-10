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
    private List<Coupon> mShopIdList;
    private Coupon mSelectedElePoi = null;
    private double mConsumption;

    private void clearSelectedStatus() {
        for (Coupon shop : mShopIdList) {
            shop.isSelected = false;
        }
    }

    public Coupon getSelectedElePoi() {
        return mSelectedElePoi;
    }


    public VipDetailAdapter(List<Coupon> shopIdList, double consumption) {
        super(R.layout.vippay_coupon_item, shopIdList);
        this.mShopIdList = shopIdList;
        this.mConsumption = consumption;
    }

    @Override
    protected void convert(BindItemViewHolder helper, Coupon item) {
        XLog.d(helper.getLayoutPosition() + " " + helper.getItemViewType() + " " + item.name);
        onBindViewHolder2(helper, helper.getLayoutPosition() - getHeaderLayoutCount());
    }

    private void onBindViewHolder2(BindItemViewHolder holder, final int position) {
        Coupon coupon = mShopIdList.get(position);

        if (coupon.rule > mConsumption) {
            holder.itemView.setEnabled(false);
        } else {
            holder.itemView.setEnabled(true);
        }

        holder.couponName.setText(coupon.name);
        holder.useRule.setText("消费满" + coupon.rule + "元可用");
        holder.itemView.setSelected(mShopIdList.get(position).isSelected);
    }

    public void updateViewStatus(int position) {
        clearSelectedStatus();
        mShopIdList.get(position).isSelected = true;
        mSelectedElePoi = mShopIdList.get(position);
        //TODO 修改为局部刷新
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
