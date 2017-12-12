package com.pitaya.vippay.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.pitaya.vippay.R;
import com.pitaya.vippay.R2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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


    private Unbinder mUnbinder;


    public static VerifyPhoneDialog newInstance(String state) {
        VerifyPhoneDialog f = new VerifyPhoneDialog();
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
                //TODO 网络请求

                //TODO 开启新的展示页面
                VipDetailDialog.newInstance("").show(getActivity().getSupportFragmentManager(), "VipDetailDialog");
                VerifyPhoneDialog.this.dismissAllowingStateLoss();
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

    public static boolean matchPhoneFormat(String inputNum) {
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

