package com.pitaya.vippay.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.vippay.R;
import com.pitaya.vippay.adapter.VipDetailAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Smarking on 17/12/10.
 */

public class VipDetailDialog extends DialogFragment {

    @BindView(R.id.vipInfoTv)
    TextView mVipInfoTv;
    @BindView(R.id.logoutBtn)
    Button mLogoutBtn;
    @BindView(R.id.couponListView)
    android.support.v7.widget.RecyclerView mCouponRecyclerView;
    @BindView(R.id.calculateResultTv)
    TextView calculateResultTv;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.progress_bar_tip)
    TextView progressBarTip;
    @BindView(R.id.progress_bar_container)
    RelativeLayout progressBarContainer;

    Unbinder unbinder;

    VipDetailAdapter mHeaderAndFooterAdapter;

    public static VipDetailDialog newInstance(String state) {
        VipDetailDialog f = new VipDetailDialog();
        f.setStyle(STYLE_NO_TITLE, 0);
        Bundle args = new Bundle();
        args.putString("mAuthState", state);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vippay_dialog_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List coupons = new ArrayList<Coupon>();
        coupons.add(new Coupon(2017121001, "满100元9折", 100, 0.9f));
        coupons.add(new Coupon(2017121002, "满100元8折", 100, 0.8f));
        coupons.add(new Coupon(2017121003, "满80元9折", 80, 0.9f));
        coupons.add(new Coupon(2017121004, "满80元6折", 80, 0.6f));
        coupons.add(new Coupon(2017121005, "满200元8折", 200, 0.8f));
        coupons.add(new Coupon(2017121007, "满100元9折", 100, 0.9f));
        coupons.add(new Coupon(2017121007, "满30元95折", 30, 0.95f));

        mHeaderAndFooterAdapter = new VipDetailAdapter(coupons, 90);
        mHeaderAndFooterAdapter.setHeaderViewAsFlow(true);
        mHeaderAndFooterAdapter.addHeaderView(getHeaderView());
        mHeaderAndFooterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ((VipDetailAdapter) adapter).updateViewStatus(position);
                //TODO
                //排序优惠 传递总金额
                //选择优惠，checkout重新计算金额
            }
        });

        mCouponRecyclerView.setHasFixedSize(true);
        mCouponRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCouponRecyclerView.setAdapter(mHeaderAndFooterAdapter);


    }


    private View getHeaderView() {
        return getActivity().getLayoutInflater().inflate(R.layout.vippay_coupon_item, (ViewGroup) mCouponRecyclerView.getParent(), false);
    }


    @Override
    public void onResume() {
        super.onResume();
        setDialogWidth();
    }

    private void setDialogWidth() {
        Point point = new Point();
        getActivity().getWindow().getDecorView().getDisplay().getRealSize(point);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = point.x * 4 / 5;
        window.setAttributes(attributes);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
