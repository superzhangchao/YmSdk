package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ym.game.sdk.YmConstants;

import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.ChangeVcodeViewListener;
import com.ym.game.sdk.common.utils.RSAEncryptUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.widget.PasswordTextWatcher;
import com.ym.game.sdk.ui.widget.TimerTextView;
import com.ym.game.utils.CommonUtils;
import com.ym.game.utils.ImageUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountSetFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private TextView ymSetTitle;
    private EditText ymEtPhone;
    private EditText ymEtPhonecode;
    private TimerTextView ymTvPhonecode;
    private EditText ymEtPwd;
    private ImageView ymImSeePwd;
    private RelativeLayout ymLlXieyi;
    private Button ymBtSet;
    private LinearLayout ymLlSet;
    private TextView ymTvTip;
    private int setType;
    private ImageView fengjiexianRight;
    private boolean showPwd = true;
    private ImageView ymImUnCkXieyi;
    private ImageView ymImCkXieyi;
    private TextView ymTvXieyiText;
    private TextView ymTvYinsiText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_setpwd"), null, true);
        ymSetTitle = (TextView) view.findViewById(ResourseIdUtils.getId("ym_set_title"));
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymEtPhone = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phone"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));

        ymEtPhonecode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phonecode"));
        ymEtPwd = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_pwd"));
        ymImSeePwd = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_seepwd"));
        ymLlSet = (LinearLayout) view.findViewById(ResourseIdUtils.getId("ym_ll_set"));
        ymTvTip = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_tip"));
        ymTvPhonecode = (TimerTextView) view.findViewById(ResourseIdUtils.getId("ym_tv_phonecode"));
        ymLlXieyi = (RelativeLayout) view.findViewById(ResourseIdUtils.getId("ym_ll_xieyi"));
        ymImUnCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_unck_xieyi"));
        ymImCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_ck_xieyi"));
        ymTvXieyiText = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_xieyitext"));
        ymTvYinsiText = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_yinsitext"));
        ymBtSet = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_set"));


        ymImBack.setOnClickListener(this);
        ymTvPhonecode.setOnClickListener(this);
        ymImSeePwd.setOnClickListener(this);
        ymImUnCkXieyi.setOnClickListener(this);
        ymImCkXieyi.setOnClickListener(this);
        ymTvXieyiText.setOnClickListener(this);
        ymTvYinsiText.setOnClickListener(this);
        ymBtSet.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));

        setType = getSetType();
        ymImBack.setVisibility(View.VISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        ymLlSet.setVisibility(View.VISIBLE);
        ymEtPwd.addTextChangedListener(new PasswordTextWatcher(ymEtPwd) {

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                super.onTextChanged(s, start, before, count);
            }
        });
        ymTvPhonecode.setText(ResourseIdUtils.getStringId("ym_tv_getphonecode"));
        ymTvPhonecode.setTimesandText(getString(ResourseIdUtils.getStringId("ym_tv_getphonecode")),"已发送（","s)",60);
        if(setType== YmTypeConfig.REGISTER){
            ymLlXieyi.setVisibility(View.VISIBLE);
            ymImCkXieyi.setVisibility(View.VISIBLE);
            ymSetTitle.setText(ResourseIdUtils.getStringId("ym_tv_registertitle"));
            ymBtSet.setText(ResourseIdUtils.getStringId("ym_bt_register"));
        }else if (setType==YmTypeConfig.SETPASSWORD){
            ymTvTip.setVisibility(View.GONE);
            ymLlXieyi.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin50 = (int)getResources().getDimension(ResourseIdUtils.getDimenId("ym_edit_horizontal_margin"));
            int margin20 = (int)getResources().getDimension(ResourseIdUtils.getDimenId("ym_margin20"));
            layoutParams.setMargins(margin50,margin20,margin50,0);
            ymLlSet.setLayoutParams(layoutParams);
            ymSetTitle.setText(ResourseIdUtils.getStringId("ym_tv_setpwdtitle"));
            ymBtSet.setText(ResourseIdUtils.getStringId("ym_bt_resetpwd"));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==ymImBack.getId()){
            back();
        }else if(view.getId()==ymTvPhonecode.getId()){
            if (!TextUtils.isEmpty(getPhone())&&!ymTvPhonecode.isRun()){
                if(CommonUtils.isFastDoubleClick()){
                    return;
                }
                //TODO:修改为检测
                UserPresenter.sendVcode(this, setType, getPhone(), new ChangeVcodeViewListener(){

                    @Override
                    public void onChangeVcodeView() {
                        ymTvPhonecode.start();
                    }
                });
            }
        }else if(view.getId()==ymImSeePwd.getId()){
            if (showPwd){
                // 显示密码
                ymEtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ymEtPwd.setSelection(ymEtPwd.getText().length());
                ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdshow"));

            }else {
                // 隐藏密码
                ymEtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ymEtPwd.setSelection(ymEtPwd.getText().length());
                ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdhide"));
            }
            showPwd =!showPwd;
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
        }else if(view.getId()==ymBtSet.getId()){
            if (setType==YmTypeConfig.REGISTER && !isShowCk()){
                return;
            }
            String phone = getPhone();
            String vcode = getVcode();
            String pwd = getPwd();
            AccountBean accountBean = new AccountBean();

            if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(vcode)&&!TextUtils.isEmpty(pwd)){
                //TODO:开始注册账号
                accountBean.setNumber(phone);
                accountBean.setVcode(vcode);
                //TODO：pwd加密
                try {
                    String encrypt = RSAEncryptUtils.encrypt(pwd, YmConstants.publickey);
                    accountBean.setPassword(encrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (setType == YmTypeConfig.REGISTER){
                    UserPresenter.registerAccount(this,accountBean);
                }else if (setType ==YmTypeConfig.SETPASSWORD){
                    UserPresenter.setPwd(this,accountBean);
                }
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
            ymEtPhone.setSelection(ymEtPhone.getText().length());
            return "";
        } else {
            String phone_number = ymEtPhone.getText().toString().trim();
            String num = "1[3456789]\\d{9}";
            if (phone_number.matches(num))
                return phone_number;
            else {
                ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_phone_error2")));
                ymEtPhone.requestFocus();
                ymEtPhone.setSelection(ymEtPhone.getText().length());
                return "";
            }
        }
    }

    private String getVcode(){
        if (TextUtils.isEmpty(ymEtPhone.getText().toString().trim())){
            return "";
        }else if (TextUtils.isEmpty(ymEtPhonecode.getText().toString().trim())) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_input_vcode")));
            ymEtPhonecode.requestFocus();
            return "";
        } else if (ymEtPhonecode.getText().toString().trim().length() != 6) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_vcode_error1")));
            ymEtPhonecode.requestFocus();
            ymEtPhonecode.setSelection(ymEtPhonecode.getText().length());
            return "";
        } else {
            String phoneCode = ymEtPhonecode.getText().toString().trim();
            String num = "\\d{6}";
            if (phoneCode.matches(num))
                return phoneCode;
            else {
                ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_vcode_error2")));
                ymEtPhonecode.requestFocus();
                ymEtPhonecode.setSelection(ymEtPhonecode.getText().length());
                return "";
            }
        }
    }

    private String getPwd(){
        if (TextUtils.isEmpty(ymEtPhone.getText().toString().trim())||TextUtils.isEmpty(ymEtPhonecode.getText().toString().trim())){
            return "";
        }else if (TextUtils.isEmpty(ymEtPwd.getText().toString())){
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_input_pwd")));
            ymEtPwd.requestFocus();
        }else if (ymEtPwd.getText().toString().length() < 6 || ymEtPwd.getText().toString().length()>20){
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_pwd_error")));
            ymEtPwd.setText("");
            ymEtPwd.requestFocus();
        }else {
            return ymEtPwd.getText().toString();
        }
        return "";
    }

    private boolean isShowCk(){
        if(ymImCkXieyi.getVisibility()==View.INVISIBLE){
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_xieyi")));
            return false;
        }
        return true;
    }

    public int getSetType() {
        int setType = (int) getArguments().getSerializable("setType");
        return setType;
    }


    @Override
    public void closeCurrnetFragment() {
        back();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}
