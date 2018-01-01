package com.pitaya.checkout;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

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


    private Unbinder mUnbinder;

    private int mSumMoney = 0;
    private int mVipcardMoney = 0;
    private int mResidueMoney = 0;
    private int mAlreadyMoney = 0;

    private VipPayComProtocol mVipPayComProtocol = ComManager.getInstance().getProtocol(VipPayComProtocol.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity_checkout);
        mUnbinder = ButterKnife.bind(this);
        initView();
        initData();
    }

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

            }
        });


        mCashBtn.setOnClickListener(mOnClickListener);
        mVipcardBtn.setOnClickListener(mOnClickListener);
        mDebitcardBtn.setOnClickListener(mOnClickListener);
        mWeixinBtn.setOnClickListener(mOnClickListener);
    }


    private void updateView() {
        mResidueMoney = mSumMoney - (mVipcardMoney + mAlreadyMoney);

        mSumMoneyTv.setText(mSumMoney);
        mAlreadyMoneyTv.setText(mAlreadyMoney);
        mResidueMoneyTv.setText(mResidueMoney);
        mDiscountMoneyTv.setText(mResidueMoney);
    }


    private void initData() {
        mVipPayComProtocol.registerStatusReceiver(new VipPayComProtocol.LoginStatus() {

            @Override
            public void call(VipUserInfo param) {
                mVipcardInfo.setText(param.vipName + " 会员卡优惠");

            }
        });

        mVipPayComProtocol.registerStatusReceiver(new VipPayComProtocol.LogoutStatus() {

            @Override
            public String call(VipUserInfo param) {
                mVipcardInfo.setText("会员卡优惠");
                return null;
            }
        });
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
                mVipPayComProtocol.openVipCampaignDialog();
            }
        }
    };
}
