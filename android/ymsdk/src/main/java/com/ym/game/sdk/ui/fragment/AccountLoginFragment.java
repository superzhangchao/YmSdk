package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;

import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.ym.game.sdk.common.base.config.TypeConfig;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.ChangeVcodeViewListener;
import com.ym.game.sdk.common.base.parse.plugin.Plugin;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.utils.ImageUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.widget.TimerTextView;


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
    private TextView ymTvYinsiText;
    private ImageView fengjiexianRight;
    private Button ymBtLogin;
    private ImageView ymLoginWeixin;
    private ImageView ymLoginQq;
    private ImageView ymLoginGt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_login"), null, true);

        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymEtPhone = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phone"));
        ymEtPhonecode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phonecode"));
        ymTvPhonecode = (TimerTextView) view.findViewById(ResourseIdUtils.getId("ym_tv_phonecode"));
        ymImUnCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_unck_xieyi"));
        ymImCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_ck_xieyi"));
        ymTvXieyiText = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_xieyitext"));
        ymTvYinsiText = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_yinsitext"));
        ymBtLogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_login"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymLoginWeixin = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_weixin"));
        ymLoginQq = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_qq"));
        ymLoginGt = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_gt"));


        ymImBack.setOnClickListener(this);
        ymTvPhonecode.setOnClickListener(this);
        ymImUnCkXieyi.setOnClickListener(this);
        ymImCkXieyi.setOnClickListener(this);
        ymTvXieyiText.setOnClickListener(this);
        ymTvYinsiText.setOnClickListener(this);
        ymBtLogin.setOnClickListener(this);
        ymLoginWeixin.setOnClickListener(this);
        ymLoginQq.setOnClickListener(this);
        ymLoginGt.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ymImBack.setVisibility(View.INVISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        boolean xieyiStatus = UserPresenter.getXieyiStatus(this);
        if (xieyiStatus){
            ymImCkXieyi.setVisibility(View.VISIBLE);
        }
        if (PluginManager.getInstance().getPlugin("plugin_qq")==null){
            ymLoginQq.setVisibility(View.GONE);
        }
        ymTvPhonecode.setText(ResourseIdUtils.getStringId("ym_tv_getphonecode"));
        ymTvPhonecode.setTimesandText(getString(ResourseIdUtils.getStringId("ym_tv_getphonecode")),"已发送（","s)",60);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity,ResourseIdUtils.getMipmapId("ym_fenjiexian")));

    }

    @Override
    public void onClick(View view) {
        AccountBean accountBean = new AccountBean();
        if (view.getId()==ymImBack.getId()){
//            UserPresenter.cancelLogin(this);
        }else if(view.getId()==ymTvPhonecode.getId()){
            if (!TextUtils.isEmpty(getPhone())&&!ymTvPhonecode.isRun()){
                UserPresenter.sendVcode(this, TypeConfig.LOGIN, getPhone(), new ChangeVcodeViewListener() {
                    @Override
                    public void onChangeVcodeView() {
                        ymTvPhonecode.start();
                    }
                });
            }
        }else if (view.getId()==this.ymTvXieyiText.getId()) {
            ShowXieyiFragment showXieyiFragment = getFragmentByName(baseActivity, ShowXieyiFragment.class);
            showXieyiFragment.setXieyiImage("ym_xieyi");
            redirectFragment(showXieyiFragment);

        }else if(view.getId()==this.ymTvYinsiText.getId()){
            ShowXieyiFragment showXieyiFragment = getFragmentByName(baseActivity, ShowXieyiFragment.class);
            showXieyiFragment.setXieyiImage("ym_yinsi");
            redirectFragment(showXieyiFragment);
        }else if(view.getId()==ymImCkXieyi.getId()){
            ymImCkXieyi.setVisibility(View.INVISIBLE);
        }else if(view.getId()==ymImUnCkXieyi.getId()){
            ymImCkXieyi.setVisibility(View.VISIBLE);
            UserPresenter.saveXieyiStatud(this,true);
        }else if(view.getId()==ymBtLogin.getId()){
            if (!TextUtils.isEmpty(getPhone())&&!TextUtils.isEmpty(getVcode())&&isShowCk()){
                accountBean.setNumber(getPhone());
                accountBean.setVcode(getVcode());
                accountBean.setLoginType("phone");
                ymBtLogin.setClickable(false);
                UserPresenter.startLogin(this,accountBean);

            }
        }else if(view.getId()==ymLoginWeixin.getId()){
            accountBean.setLoginType("wx");
             if (ymImCkXieyi.getVisibility()==View.VISIBLE){
                 ymLoginWeixin.setClickable(false);
                 UserPresenter.startLogin(this,accountBean);
            }else{
                 ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_xieyi")));
             }

        }else if(view.getId()==ymLoginQq.getId()){
            accountBean.setLoginType("qq");
             if (ymImCkXieyi.getVisibility()==View.VISIBLE){
                 ymLoginQq.setClickable(false);
                 UserPresenter.startLogin(this,accountBean);
            }else{
                 ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_xieyi")));
             }
        }else if(view.getId()==ymLoginGt.getId()){
            accountBean.setLoginType("guest");
             if (ymImCkXieyi.getVisibility()==View.VISIBLE){
                 ymLoginGt.setClickable(false);
                UserPresenter.startLogin(this,accountBean);
            }else{
                 ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_xieyi")));
             }
        }

    }



    //    验证手机号
    private String getPhone() {
        if (TextUtils.isEmpty(ymEtPhone.getText().toString().trim())) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_input_phone")));
            ymEtPhone.requestFocus();
            return "";
        } else if (ymEtPhone.getText().toString().trim().length() != 11) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_phone_error1")));
            ymEtPhone.requestFocus();
            return "";
        } else {
            String phone_number = ymEtPhone.getText().toString().trim();
            String num = "\\d{11}";
            if (phone_number.matches(num))
                return phone_number;
            else {
                ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_phone_error2")));
                return "";
            }
        }
    }

    private String getVcode(){
        if (TextUtils.isEmpty(ymEtPhonecode.getText().toString().trim())) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_input_vcode")));
            ymEtPhonecode.requestFocus();
            return "";
        } else if (ymEtPhonecode.getText().toString().trim().length() != 6) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_vcode_error1")));
            ymEtPhonecode.requestFocus();
            return "";
        } else {
            String phoneCode = ymEtPhonecode.getText().toString().trim();
            String num = "\\d{6}";
            if (phoneCode.matches(num))
                return phoneCode;
            else {
                ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_vcode_error2")));
                return "";
            }
        }
    }
    private boolean isShowCk(){
        if(ymImCkXieyi.getVisibility()==View.INVISIBLE){
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_xieyi")));
            return false;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
//        UserPresenter.cancelLogin(this);
        return true;
    }

    @Override
    public void cancelLogin() {
        ymBtLogin.setClickable(true);
        ymLoginWeixin.setClickable(true);
        ymLoginQq.setClickable(true);
        ymLoginGt.setClickable(true);
    }


}
