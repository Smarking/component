package com.pitaya.checkout;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    @BindView(R.id.discountNumTv)
    TextView mDiscountNumTv;
    @BindView(R.id.residueNumTv)
    TextView mResidueNumTv;
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


    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity_checkout);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

    }
}
