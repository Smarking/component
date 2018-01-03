package com.pitaya.vippay.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.elvishew.xlog.XLog;
import com.pitaya.baselib.network.ApiFactory;
import com.pitaya.baselib.network.ApiResponse;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;
import com.pitaya.vippay.R;
import com.pitaya.vippay.R2;
import com.pitaya.vippay.network.VipPayService;
import com.pitaya.vippay.utils.VipPayUserCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Smarking on 17/12/10.
 */
public class VerifyPhoneDialog extends DialogFragment {

    @BindView(R2.id.inputPhoneEt)
    EditText mEditText;

    @BindView(R2.id.cancel)
    TextView mCancel;

    @BindView(R2.id.confirm)
    TextView mConfirm;

    @BindView(R2.id.errorTipTv)
    TextView mErrorTipTv;

    private VipPayService mVipPayService = ApiFactory.getApi(VipPayService.class);
    private Unbinder mUnbinder;

    private static final String ARG_ORDER = "order";

    public static VerifyPhoneDialog newInstance(FragmentManager fragmentManager, Order order) {
        Fragment fragment = fragmentManager.findFragmentByTag(VerifyPhoneDialog.class.getName());
        if (fragment != null) {
            fragment.getArguments().putParcelable(ARG_ORDER, order);
            return (VerifyPhoneDialog) fragment;
        }
        VerifyPhoneDialog f = new VerifyPhoneDialog();
        f.setStyle(STYLE_NO_TITLE, 0);
        Bundle args = new Bundle();
        args.putParcelable(ARG_ORDER, order);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (VipPayUserCenter.getInstance().getVipUserInfo() != null) {
            jumpVipDetailDialog();
            dismissAllowingStateLoss();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.vippay_dialog_verifyphone, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(VerifyPhoneDialog.this, view);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mEditText.getText().toString();
                mVipPayService.loginVipPay(phone)
                        .onErrorReturnItem(new ApiResponse<VipUserInfo>(200, new VipUserInfo("杨明", "vip001", 1001), null, true))
                        .flatMap(new Function<ApiResponse<VipUserInfo>, ObservableSource<ApiResponse<List<Coupon>>>>() {
                            @Override
                            public ObservableSource<ApiResponse<List<Coupon>>> apply(ApiResponse<VipUserInfo> vipUserInfoApiResponse) throws Exception {

                                VipPayUserCenter.getInstance().setVipUserInfo(vipUserInfoApiResponse.getData());

                                //TODO mock
                                List<Coupon> coupons = new ArrayList();
                                coupons.add(new Coupon(2017121001, "满100元9折", 100, 0.9f));
                                coupons.add(new Coupon(2017121002, "满100元8折", 100, 0.8f));
                                coupons.add(new Coupon(2017121003, "满80元9折", 80, 0.9f));
                                coupons.add(new Coupon(2017121004, "满80元6折", 80, 0.6f));
                                coupons.add(new Coupon(2017121005, "满200元8折", 200, 0.8f));
                                coupons.add(new Coupon(2017121007, "满100元9折", 100, 0.9f));
                                coupons.add(new Coupon(2017121007, "满30元95折", 30, 0.95f));

                                return mVipPayService.getCouponList(vipUserInfoApiResponse.getData().vipId)
                                        .onErrorReturnItem(new ApiResponse<List<Coupon>>(200, coupons, null, true));
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ApiResponse<List<Coupon>>>() {
                            @Override
                            public void accept(ApiResponse<List<Coupon>> listApiResponse) throws Exception {
                                VipPayUserCenter.getInstance().setCouponList(listApiResponse.getData());
                                ComManager.getInstance().getReceiver(VipPayComProtocol.LoginStatus.class).call(VipPayUserCenter.getInstance().getVipUserInfo());

                                jumpVipDetailDialog();

                                dismissAllowingStateLoss();
                            }
                        });
            }

        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                XLog.d("onTextChanged " + s + "  count" + count + " start" + start + " before" + before);
            }

            @Override
            public void afterTextChanged(Editable s) {
                XLog.d("afterTextChanged " + s.toString());
                if (matchPhoneFormat(s.toString())) {
                    mErrorTipTv.setVisibility(View.GONE);
                } else {
                    mErrorTipTv.setVisibility(View.VISIBLE);
                    mErrorTipTv.setText("亲，请输入的手机号");
                }
            }
        });
    }

    private void jumpVipDetailDialog() {
        VipDetailDialog.newInstance(getFragmentManager(), (Order) getArguments().getParcelable(ARG_ORDER))
                .show(getFragmentManager(), VipDetailDialog.class.getName());
    }

    private static boolean matchPhoneFormat(String inputNum) {
        // 规则
        String regEx = "^1[3|4|5|8][0-9]\\d{8}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(inputNum);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
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
        mUnbinder.unbind();
    }
}

