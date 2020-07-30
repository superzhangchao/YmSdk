package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.callback.listener.ChangeVcodeViewListener;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.widget.TimerTextView;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountBindFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private EditText ymEtPhone;
    private EditText ymEtPhonecode;
    private TimerTextView ymTvPhonecode;
    private Button ymBtUnbind;
    private Button ymBtBind;
    private AccountBean accountBean;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_bind"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymEtPhone = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phone"));
        ymEtPhonecode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phonecode"));
        ymTvPhonecode = (TimerTextView) view.findViewById(ResourseIdUtils.getId("ym_tv_phonecode"));
        ymBtUnbind = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_unbind"));
        ymBtBind = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_bind"));

        ymImBack.setOnClickListener(this);
        ymTvPhonecode.setOnClickListener(this);
        ymBtUnbind.setOnClickListener(this);
        ymBtBind.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountBean = getAccountData();
        ymImBack.setVisibility(View.INVISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        ymTvPhonecode.setText(ResourseIdUtils.getStringId("ym_tv_getphonecode"));
        ymTvPhonecode.setTimesandText(getString(ResourseIdUtils.getStringId("ym_tv_getphonecode")),"已发送（","s)",6);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==ymImBack.getId()){
            cancelBind();
        }else if(view.getId()==ymTvPhonecode.getId()){
            if (!TextUtils.isEmpty(getPhone())&&!ymTvPhonecode.isRun()){

                UserPresenter.sendVcode(this,getPhone(),new ChangeVcodeViewListener(){

                    @Override
                    public void onChangeVcodeView() {
                        ymTvPhonecode.start();
                    }
                });
            }
        }else if(view.getId()==ymBtBind.getId()){
            String phone = getPhone();
            String vcode = getVcode();
            accountBean.setNumber(phone);
            accountBean.setVcode(vcode);
            accountBean.setLoginType("phone");
            if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(vcode)){
                UserPresenter.startBind(this,accountBean);

            }
        }else if (view.getId()==ymBtUnbind.getId()){
            cancelBind();
        }
    }
    private void cancelBind(){
        ResultAccoutBean resultAccoutBean = new ResultAccoutBean();
        resultAccoutBean.setData(new ResultAccoutBean.DataBean());
        resultAccoutBean.getData().setUid(accountBean.getUid());
        resultAccoutBean.getData().setLoginToken(accountBean.getLoginToken());
        resultAccoutBean.getData().setNickName(accountBean.getNickName());
        resultAccoutBean.getData().setAuthStatus(accountBean.getAuthStatus());
        UserPresenter.cancelBind(this,resultAccoutBean);
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
    @Override
    public AccountBean getAccountData() {
        AccountBean accountData = (AccountBean) getArguments().getSerializable("accountBean");
        return accountData;
    }


    @Override
    public boolean onBackPressed() {
        cancelBind();
        return true;
    }
}
