package com.pitaya.vippay.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pitaya.baselib.network.ApiFactory;
import com.pitaya.baselib.network.ApiResponse;
import com.pitaya.comcallback.Callback1;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.vippay.R;
import com.pitaya.vippay.R2;
import com.pitaya.vippay.adapter.VipDetailAdapter;
import com.pitaya.vippay.network.VipPayService;
import com.pitaya.vippay.utils.VipPayUserCenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Smarking on 17/12/10.
 */

public class VipDetailDialog extends DialogFragment {

    @BindView(R2.id.vipInfoTv)
    TextView mVipInfoTv;
    @BindView(R2.id.logoutBtn)
    Button mLogoutBtn;
    @BindView(R2.id.couponListView)
    android.support.v7.widget.RecyclerView mCouponRecyclerView;
    @BindView(R2.id.calculateResultTv)
    TextView calculateResultTv;
    @BindView(R2.id.cancel)
    TextView cancel;
    @BindView(R2.id.confirm)
    TextView confirm;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.progress_bar_tip)
    TextView progressBarTip;
    @BindView(R2.id.progress_bar_container)
    RelativeLayout progressBarContainer;

    Unbinder unbinder;

    private VipDetailAdapter mHeaderAndFooterAdapter;

    private VipPayService mVipPayService = ApiFactory.getApi(VipPayService.class);

    private Order mOrder;

    private static final String ARG_ORDER = "order";

    public static VipDetailDialog newInstance(FragmentManager fragmentManager, Order order) {
        Fragment fragment = fragmentManager.findFragmentByTag(VipDetailDialog.class.getName());
        if (fragment != null) {
            fragment.getArguments().putParcelable(ARG_ORDER, order);
            return (VipDetailDialog) fragment;
        }
        VipDetailDialog f = new VipDetailDialog();
        f.setStyle(STYLE_NO_TITLE, 0);
        Bundle args = new Bundle();
        args.putParcelable(ARG_ORDER, order);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrder = getArguments().getParcelable(ARG_ORDER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vippay_dialog_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVipInfoTv.setText(VipPayUserCenter.getInstance().getVipUserInfo().toString());

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComManager.getInstance().getReceiver(VipPayComProtocol.LogoutStatus.class)
                        .call(VipPayUserCenter.getInstance().getVipUserInfo());

                mVipPayService.logoutVipPay(VipPayUserCenter.getInstance().getVipUserInfo().vipId)
                        .onErrorReturnItem(new ApiResponse<String>(200, "succeed", null, true))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ApiResponse<String>>() {
                            @Override
                            public void accept(ApiResponse<String> stringApiResponse) throws Exception {
                                Toast.makeText(getContext().getApplicationContext(), "会员卡退出成功", Toast.LENGTH_SHORT).show();
                                VipPayUserCenter.getInstance().setCouponList(null);
                                VipPayUserCenter.getInstance().setVipUserInfo(null);
                                dismissAllowingStateLoss();
                            }
                        });
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mHeaderAndFooterAdapter.getSelectedElePoi() == null) {
                    Toast.makeText(getContext().getApplicationContext(), "未选择优惠或者无可用优惠", Toast.LENGTH_SHORT).show();
                    return;
                }

                mVipPayService.presetPay(mHeaderAndFooterAdapter.getSelectedElePoi())
                        .onErrorReturnItem(new ApiResponse<String>(200, "succeed", null, true))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ApiResponse<String>>() {
                            @Override
                            public void accept(ApiResponse<String> stringApiResponse) throws Exception {
                                if (stringApiResponse.isSuccess()) {
                                    ComManager.getInstance().getReceiver(VipPayComProtocol.VipCampaignCallback.class).onPresetPay(mHeaderAndFooterAdapter.getSelectedElePoi());
                                    dismissAllowingStateLoss();
                                } else {
                                    Toast.makeText(getContext().getApplicationContext(), "非法使用", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        mHeaderAndFooterAdapter = new VipDetailAdapter(null, mOrder.amount);
        mHeaderAndFooterAdapter.setHeaderViewAsFlow(true);
        mHeaderAndFooterAdapter.addHeaderView(getHeaderView());
        mHeaderAndFooterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ((VipDetailAdapter) adapter).updateViewStatus(position);

                ComManager.getInstance().getProtocol(CheckoutComProtocol.class).calculateDiscount(
                        mOrder,
                        mHeaderAndFooterAdapter.getData().get(position),
                        new Callback1<Float>() {
                            @Override
                            public void call(final Float param) {
                                String info = "总金额:" + mOrder.amount + " 打" + (int) (mHeaderAndFooterAdapter.getSelectedElePoi().discount * 10) + "折，可优惠:" + param;
                                calculateResultTv.setText(info);
                                ComManager.getInstance().getReceiver(VipPayComProtocol.VipCampaignCallback.class).onSelectedCoupon(info);
                            }
                        });
            }
        });

        mCouponRecyclerView.setHasFixedSize(true);
        mCouponRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCouponRecyclerView.setAdapter(mHeaderAndFooterAdapter);

        ComManager.getInstance().getProtocol(CheckoutComProtocol.class).sortVipPayCoupons(
                VipPayUserCenter.getInstance().getCouponList(),
                new Callback1<List<Coupon>>() {
                    @Override
                    public void call(final List<Coupon> param) {
                        mHeaderAndFooterAdapter.setNewData(param);
                        ComManager.getInstance().getReceiver(VipPayComProtocol.VipCampaignCallback.class).onSortedCouponList(param);
                    }
                });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ComManager.getInstance().getReceiver(VipPayComProtocol.VipCampaignCallback.class).unbind();
    }
}
