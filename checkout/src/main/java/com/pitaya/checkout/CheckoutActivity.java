package com.pitaya.checkout;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pitaya.commanager.ComManager;
import com.pitaya.commanager.ProxyTools;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CheckoutActivity extends FragmentActivity {

    @BindView(R.id.sumMoneyEditText)
    EditText mSumMoneyEditText;
    @BindView(R.id.sumMoneyBtn)
    Button mSumMoneyBtn;
    @BindView(R.id.sumMoneyTv)
    TextView mSumMoneyTv;
    @BindView(R.id.discountMoneyTv)
    TextView mDiscountMoneyTv;
    @BindView(R.id.residueMoneyTv)
    TextView mResidueMoneyTv;
    @BindView(R.id.alreadyMoneyTv)
    TextView mAlreadyMoneyTv;
    @BindView(R.id.cashBtn)
    TextView mCashBtn;
    @BindView(R.id.vipcardBtn)
    TextView mVipcardBtn;
    @BindView(R.id.debitcardBtn)
    TextView mDebitcardBtn;
    @BindView(R.id.weixinBtn)
    TextView mWeixinBtn;
    @BindView(R.id.checkoutBtn)
    Button mCheckoutBtn;

    @BindView(R.id.vipcardInfo)
    TextView mVipcardInfo;

    @BindView(R.id.secondScreen)
    TextView mSecondScreen;

    private Unbinder mUnbinder;
    private Order mOrder;
    private float mSumMoney = 0;
    private float mVipcardMoney = 0;
    private float mResidueMoney = 0;
    private float mAlreadyMoney = 0;
    private Coupon mSelectedCoupon;

    private VipPayComProtocol mVipPayComProtocol = ComManager.getInstance().getProtocol(VipPayComProtocol.class);
    private PrinterComProtocol mPrinterComProtocol = ComManager.getInstance().getProtocol(PrinterComProtocol.class);


    private void initView() {
        mSumMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSumMoney = Integer.valueOf(mSumMoneyEditText.getText().toString());
                } catch (Throwable e) {

                }
                updateView();
            }
        });

        mCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVipcardMoney > 0 && mSelectedCoupon != null) {
                    //用了VipPay支付
                    mVipPayComProtocol.confirmCheckoutVip(mSelectedCoupon, new VipPayComProtocol.ConfirmCallback() {
                        @Override
                        public void onCheckout(String result) {
                            if (TextUtils.isEmpty(result)) {
                                Toast.makeText(getApplicationContext(), "会员支付失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            checkout();
                        }
                    });
                } else {
                    checkout();
                }
            }
        });

        mCashBtn.setOnClickListener(mOnClickListener);
        mVipcardBtn.setOnClickListener(mOnClickListener);
        mDebitcardBtn.setOnClickListener(mOnClickListener);
        mWeixinBtn.setOnClickListener(mOnClickListener);

        mVipcardInfo.setText(mVipPayComProtocol.getLoginInfo() == null ? "无会员优惠： " : mVipPayComProtocol.getLoginInfo().vipName + "会员卡优惠: ");
    }

    private void checkout() {
        if (mResidueMoney == 0) {
            Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
            mVipPayComProtocol.requestLogout();
            mPrinterComProtocol.print(String.format("支付成功，总金额%1$f，会员卡优惠%2$f，其他方式支付%3$f", mSumMoney, mVipcardMoney, mAlreadyMoney));

        } else {
            Toast.makeText(getApplicationContext(), "支付失败，钱不够", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity_checkout);
        mUnbinder = ButterKnife.bind(this);
        initView();
        initData();
    }

    private void updateView() {
        mResidueMoney = mSumMoney - (mVipcardMoney + mAlreadyMoney);
        mOrder.amount = mSumMoney;
        mOrder.receivable = mResidueMoney;

        mSumMoneyTv.setText(String.valueOf(mSumMoney));
        mAlreadyMoneyTv.setText(String.valueOf(mAlreadyMoney));
        mResidueMoneyTv.setText(String.valueOf(mResidueMoney));
        mDiscountMoneyTv.setText(String.valueOf(mVipcardMoney));
    }


    private void initData() {
        mOrder = new Order();
        mOrder.orderId = "12345";
        mVipPayComProtocol.registerStatusReceiver(new VipPayComProtocol.LoginStatus() {

            @Override
            public void call(VipUserInfo param) {
                mVipcardInfo.setText(param.vipName + " 会员卡优惠: ");
                updateSecondScreen("会员 " + param.vipName);
            }
        });

        mVipPayComProtocol.registerStatusReceiver(new VipPayComProtocol.LogoutStatus() {
            @Override
            public void call(VipUserInfo param) {
                mVipcardInfo.setText("无会员优惠: ");
                mVipcardMoney = 0;
                updateView();

                updateSecondScreen("无会员优惠");
            }
        });
    }

    private void updateSecondScreen(String msg) {
        //展示优惠优惠信息
        mSecondScreen.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.cashBtn
                    || viewId == R.id.debitcardBtn
                    || viewId == R.id.weixinBtn) {

                mAlreadyMoney = mSumMoney - mVipcardMoney;
                updateView();
            } else if (viewId == R.id.vipcardBtn) {
                mVipPayComProtocol.openVipCampaignDialog(CheckoutActivity.this, mOrder, ProxyTools.create(VipPayComProtocol.VipCampaignCallback.class, new VipPayComProtocol.VipCampaignCallback() {

                    @Override
                    public void onSortedCouponList(List<Coupon> sortedList) {
                        updateSecondScreen(sortedList.toString());
                    }

                    @Override
                    public void onSelectedCoupon(String calculateInfo) {
                        updateSecondScreen(calculateInfo);
                    }

                    @Override
                    public void onPresetPay(Coupon coupon) {
                        mSelectedCoupon = coupon;
                        updateSecondScreen("预结账优惠：" + coupon.toString());

                        mVipcardMoney = coupon.discount * mSumMoney;
                        updateView();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                }));

            }
        }
    };
}
