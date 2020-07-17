package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ym.game.sdk.R;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.widget.TimerTextView;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountLoginFragment extends UserBaseFragment implements View.OnClickListener {

    private View content;
    private static final String TAG = "Ymsdk";
    private View ll_content;
    private View rl_title;
    private ImageView ymImBack;
    private ImageView ymImClose;
    private EditText ymEtPhone;
    private EditText ymEtPhonecode;
    private TimerTextView ymTvPhonecode;
    private ImageView ymImUnCkXieyi;
    private ImageView ymImCkXieyi;
    private TextView ymTvXieyiText;
    private ImageView fengjiexianRight;
    private Button ymBtLogin;
    private ImageView ymLoginWeixin;
    private ImageView ymLoginQq;
    private ImageView ymLoginGt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_login"), null, true);
//        ll_content = view.findViewById(ResourseIdUtils.getId("ll_content"));
//        rl_title = view.findViewById(ResourseIdUtils.getId("rl_title"));

        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymEtPhone = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phone"));
        ymEtPhonecode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phonecode"));
        ymTvPhonecode = (TimerTextView) view.findViewById(ResourseIdUtils.getId("ym_tv_phonecode"));
        ymImUnCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_unck_xieyi"));
        ymImCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_ck_xieyi"));
        ymTvXieyiText = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_xieyitext"));
        ymBtLogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_login"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymLoginWeixin = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_weixin"));
        ymLoginQq = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_qq"));
        ymLoginGt = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_gt"));

        ymImBack.setOnClickListener(this);
        ymTvPhonecode.setOnClickListener(this);
        ymImCkXieyi.setOnClickListener(this);
        ymTvXieyiText.setOnClickListener(this);
        ymBtLogin.setOnClickListener(this);
        ymLoginWeixin.setOnClickListener(this);
        ymLoginQq.setOnClickListener(this);
        ymBtLogin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ymImBack.setVisibility(View.VISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        ymTvPhonecode.setText(ResourseIdUtils.getStringId("ym_tv_getphonecode"));
        ymTvPhonecode.setTimesandText(getString(ResourseIdUtils.getStringId("ym_tv_getphonecode")),"已发送（","s)",6);

//        ymTvXieyi.setText("dfsdfsf");
//        ll_content.post(new Runnable(){
//
//            @Override
//            public void run() {
//                int height = ll_content.getMeasuredHeight();
//                int width = ll_content.getMeasuredWidth();
//
//                Log.i(TAG, "run: ll_content height:"+height+" width:"+width);
//            }
//
//
//        });
//
//        rl_title.post(new Runnable() {
//            @Override
//            public void run() {
//                int height = rl_title.getMeasuredHeight();
//                int width = rl_title.getMeasuredWidth();
//                Log.i(TAG, "run: rl_title height:"+height+" width:"+width);
//            }
//        });


    }

    @Override
    public void onClick(View view) {
        if(view.getId()==ymTvPhonecode.getId()){
            if (!TextUtils.isEmpty(getPhone())&&!ymTvPhonecode.isRun()){
                ymTvPhonecode.start();
                UserPresenter.sendVcode(this,getPhone());
            }else if (view.getId()==ymTvXieyiText.getId()){
                ShowXieyiFragment showXieyiFragment = ShowXieyiFragment.getFragmentByName(baseActivity,ShowXieyiFragment.class);
                redirectFragment(showXieyiFragment);
            }else if(view.getId()==ymImCkXieyi.getId()){
                int visibility = ymImCkXieyi.getVisibility();
//                ymImCkXieyi.setVisibility((visibility==View.VISIBLE)?View.INVISIBLE:View.VISIBLE);
                ymImCkXieyi.setVisibility(View.VISIBLE);
            }
        }
    }

//    验证手机号
    private String getPhone() {
        if (TextUtils.isEmpty(ymEtPhone.getText().toString().trim())) {
            ToastUtils.showToast(baseActivity, "请输入您的电话号码");
            ymEtPhone.requestFocus();
            return "";
        } else if (ymEtPhone.getText().toString().trim().length() != 11) {
            ToastUtils.showToast(baseActivity, "您的电话号码位数不正确");
            ymEtPhone.requestFocus();
            return "";
        } else {
            String phone_number = ymEtPhone.getText().toString().trim();
            String num = "[1][358]\\d{9}";
            if (phone_number.matches(num))
                return phone_number;
            else {
                ToastUtils.showToast(baseActivity, "请输入正确的手机号码");
                return "";
            }
        }
    }
}
