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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ym.game.sdk.YmConstants;

import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.HistoryAccountBean;
import com.ym.game.sdk.common.utils.RSAEncryptUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.event.BaseEvent;
import com.ym.game.sdk.event.UserEvent;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.Adapter.HistoryPopAdapter;
import com.ym.game.sdk.ui.widget.AutoPopupWindow;
import com.ym.game.sdk.ui.widget.PasswordTextWatcher;
import com.ym.game.utils.CommonUtils;
import com.ym.game.utils.ImageUtils;
import com.ym.game.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountPasswordLoignFragment extends UserBaseFragment implements View.OnClickListener, AutoPopupWindow.IAutoPopSelectedListener {


    private ImageView ymImBack;
    private ImageView ymImClose;
    private EditText ymEtPhone;
    private ImageView ymImArrow;
    private RelativeLayout ymRlPhone;
    private EditText ymEtPwd;
    private ImageView ymImSeePwd;
    private TextView ymTvRegister;
    private TextView ymTvSetpwd;
    private Button ymBtLogin;
    private boolean showHistoryArrow = false;
    private boolean hasHistoryAccount = false;

    private AutoPopupWindow historyPopView;
    private HistoryPopAdapter popAdapter;
    private List<HistoryAccountBean> mSources = new ArrayList<>();

    private static final int HIDEPWDSTATE = 0;
    private static final int SHOWPWDSTATE = 1;
    private static final int CLEANPWDSTATE = 2;
    private int currentPwdState = HIDEPWDSTATE;
    private AccountBean mAccountBean = new AccountBean();
    private ImageView fengjiexianRight;
    private TextView ymTvLoginOther;
    private int pwdPageType;
    private RelativeLayout ymRlAccountset;
    private String lastPhone ;
    private String lastpwd ;
    private String newPhone;
    private String newPwd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_pwd"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymEtPhone = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phone"));
        ymImArrow = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_arrow"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));

        ymRlPhone = (RelativeLayout) view.findViewById(ResourseIdUtils.getId("ym_rl_phone"));
        ymEtPwd = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_pwd"));
        ymImSeePwd = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_seepwd"));
        ymRlAccountset = (RelativeLayout) view.findViewById(ResourseIdUtils.getId("ym_rl_accountset"));
        ymTvRegister = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_register"));
        ymTvSetpwd = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_setpwd"));
        ymBtLogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_login"));
        ymTvLoginOther = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_loginother"));

        ymImBack.setOnClickListener(this);
        ymImClose.setOnClickListener(this);
        ymImArrow.setOnClickListener(this);
        ymImSeePwd.setOnClickListener(this);
        ymTvRegister.setOnClickListener(this);
        ymTvSetpwd.setOnClickListener(this);
        ymBtLogin.setOnClickListener(this);
        ymTvLoginOther.setOnClickListener(this);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pwdPageType = getPwdPageType();
        initSources();
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));
        ymImArrow.setImageResource(ResourseIdUtils.getMipmapId("ym_arr_down"));
        ymEtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ymEtPwd.setSelection(ymEtPwd.getText().length());

        ymEtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus&&TextUtils.equals(ymEtPwd.getText().toString().trim(),"******")){
                    ymEtPwd.setText("");
                }else if (hasFocus){
                    ymEtPwd.setSelection(ymEtPwd.getText().length());
                }
            }
        });

        ymEtPwd.addTextChangedListener(new PasswordTextWatcher(ymEtPwd) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if(TextUtils.equals(ymEtPwd.getText().toString().trim(),"******")){
                    ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdclean"));
                    currentPwdState = CLEANPWDSTATE;
                }else if (currentPwdState == CLEANPWDSTATE){
                    ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdhide"));
                    currentPwdState = HIDEPWDSTATE;
                }
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
        if (pwdPageType == YmTypeConfig.COMMONPWDPAGE){
            ymImBack.setVisibility(View.VISIBLE);
            ymImClose.setVisibility(View.INVISIBLE);
            ymRlAccountset.setVisibility(View.VISIBLE);
            ymTvLoginOther.setVisibility(View.GONE);
        }else if (pwdPageType ==YmTypeConfig.QUICKPWDPAGE){
            ymImBack.setVisibility(View.INVISIBLE);
            ymImClose.setVisibility(View.VISIBLE);
            ymRlAccountset.setVisibility(View.GONE);
            ymTvLoginOther.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ymImBack.getId()) {
            back();
        } else if (v.getId() == ymImArrow.getId()) {
            if(CommonUtils.isFastDoubleClick(500)) {
                return;
            }

            if (!showHistoryArrow) {
                showHistoryView();
            } else {
                hideHistoryView();
            }
        } else if (v.getId() == ymImSeePwd.getId()) {
            switch (currentPwdState){
                case HIDEPWDSTATE:
                    // 显示密码
                    ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdshow"));
                    ymEtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ymEtPwd.setSelection(ymEtPwd.getText().length());
                    currentPwdState = SHOWPWDSTATE;
                    break;
                case SHOWPWDSTATE:
                    // 隐藏密码
                    ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdhide"));
                    ymEtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ymEtPwd.setSelection(ymEtPwd.getText().length());
                    currentPwdState = HIDEPWDSTATE;
                    break;
                case CLEANPWDSTATE:
                    ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdhide"));
                    ymEtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    currentPwdState = HIDEPWDSTATE;
                    ymEtPwd.setText("");
                    ymEtPwd.requestFocus();
                    break;
            }

        } else if (v.getId() == ymTvRegister.getId()) {
            ymEtPwd.setText("");
            AccountSetFragment accountSetFragment = getFragmentByName(baseActivity, AccountSetFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("setType", YmTypeConfig.REGISTER);
            accountSetFragment.setArguments(bundle);
            redirectFragment(accountSetFragment);
        } else if (v.getId() == ymTvSetpwd.getId()) {
            ymEtPwd.setText("");
            AccountSetFragment accountSetFragment = getFragmentByName(baseActivity, AccountSetFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("setType", YmTypeConfig.SETPASSWORD);
            accountSetFragment.setArguments(bundle);
            redirectFragment(accountSetFragment);
        }else if (v.getId()==ymImClose.getId()){
            AccountLoginFragment accountLoginFragment = getFragmentByName(baseActivity, AccountLoginFragment.class);
            redirectFragment(accountLoginFragment);
        }else if(v.getId()==ymTvLoginOther.getId()){
            AccountLoginFragment accountLoginFragment = getFragmentByName(baseActivity, AccountLoginFragment.class);
            redirectFragment(accountLoginFragment);
        }else if (v.getId() == ymBtLogin.getId()) {
            if (CommonUtils.isFastDoubleClick(500)){
                return;
            }
            if (!TextUtils.isEmpty(getPhone())&&!TextUtils.isEmpty(getPwd())){
                String phone = getPhone();
                String pwd = getPwd();
                if (!TextUtils.isEmpty(lastpwd)&&TextUtils.equals(lastpwd,pwd)&&!TextUtils.isEmpty(lastPhone)&&TextUtils.equals(lastPhone,phone)){
                    ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_pwderror")));
                    return;
                }
                lastPhone = phone;
                lastpwd =pwd;
                mAccountBean.setNumber(phone);
                try {
                    if (!TextUtils.isEmpty(newPwd)){
                        pwd = newPwd;
                        newPhone = null;
                        newPwd = null;
                    }
                    else if (hasHistoryAccount&&TextUtils.equals("******",pwd)){
                        pwd = mAccountBean.getPassword();
                    }else {
                        pwd = RSAEncryptUtils.encrypt(pwd, YmConstants.publickey);
                    }


                    mAccountBean.setPassword(pwd);
                    mAccountBean.setLoginType(YmConstants.PHONELOGIN);
                    mAccountBean.setHasPassword(true);
                ymBtLogin.setClickable(false);
                    UserPresenter.startLogin(this,mAccountBean);
                } catch (Exception e) {
                    //加密错误
                    e.printStackTrace();
                }


            }
        }

    }

    private void hideHistoryView() {
        historyPopView.dismiss();
    }

    private void showHistoryView() {
        ymImArrow.setImageResource(ResourseIdUtils.getMipmapId("ym_arr_up"));
        showHistoryArrow = !showHistoryArrow;

        historyPopView = new AutoPopupWindow(baseActivity, this);
        popAdapter = new HistoryPopAdapter(baseActivity);
        historyPopView.setBaseAdapter(popAdapter);
        popAdapter.setListener(this);
        historyPopView.initData();
        historyPopView.setOutsideTouchable(true);
        setWidth();
        setHeight(mSources);
        historyPopView.setFocusable(true);

        historyPopView.setOutsideTouchable(true);
        popAdapter.notifyDataSetChanged(mSources);
        historyPopView.showAsAnchorBelow(ymRlPhone);
        historyPopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ymImArrow.setImageResource(ResourseIdUtils.getMipmapId("ym_arr_down"));
                showHistoryArrow = !showHistoryArrow;
            }
        });

    }
    private void setHeight(List<HistoryAccountBean> args) {
        int height;
        if (args.size()==2){
            height= (int) ( 2 * ymRlPhone.getMeasuredHeight()*1.3);
        }else {
            height= (int) ( 3 * ymRlPhone.getMeasuredHeight()*1.3);
        }
        historyPopView.setHeight(height);
    }

    private void setWidth() {
        historyPopView.setWidth(ymRlPhone.getMeasuredWidth());
    }
    private void initSources() {
        List<HistoryAccountBean> historyAccountBeanList = SharedPreferencesUtils.getHistoryAccountBean(getContext(), "histroyAccount", "histroyAccount");
        if (historyAccountBeanList==null||historyAccountBeanList.size()==0){
            ymImArrow.setVisibility(View.INVISIBLE);
            // 隐藏密码
            ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdhide"));
            AccountBean lastNormalLoginInfo = UserPresenter.getLastNormalLoginInfo(baseActivity);
            String loginType = lastNormalLoginInfo.getLoginType();
            if (TextUtils.equals(loginType, YmConstants.PHONELOGIN)&&!TextUtils.isEmpty(lastNormalLoginInfo.getNumber().trim())){
                ymEtPhone.setText(lastNormalLoginInfo.getNumber().trim());
            }
            currentPwdState = HIDEPWDSTATE;
        }else {
            for (int i = 0; i < historyAccountBeanList.size(); i++) {
                if (i==historyAccountBeanList.size()-1){
                    historyAccountBeanList.get(i).setCheck(true);
                    mAccountBean.setNumber(historyAccountBeanList.get(i).getPhone());
                    mAccountBean.setPassword(historyAccountBeanList.get(i).getPassword());
                    ymEtPhone.setText(historyAccountBeanList.get(i).getPhone());
                    ymEtPwd.setText("******");
                    ymEtPhone.requestFocus();
                    hasHistoryAccount = true;
                    ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdclean"));
                    currentPwdState = CLEANPWDSTATE;
                }else {
                    historyAccountBeanList.get(i).setCheck(false);
                }
            }
            if (historyAccountBeanList.size()==1){
                ymImArrow.setVisibility(View.INVISIBLE);
            }
            mSources = historyAccountBeanList;
        }

    }
    //    验证手机号
    private String getPhone() {
        if (TextUtils.isEmpty(ymEtPhone.getText().toString().trim())) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_input_phone")));
            ymEtPhone.requestFocus();
        } else if (ymEtPhone.getText().toString().trim().length() != 11) {
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_phone_error1")));
            ymEtPhone.requestFocus();
            ymEtPhone.setSelection(ymEtPhone.getText().length());
        } else {
            String phone_number = ymEtPhone.getText().toString().trim();
            String num = "1[3456789]\\d{9}";
            if (phone_number.matches(num))
                return phone_number;
            else {
                ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_phone_error2")));
                ymEtPhone.requestFocus();
                ymEtPhone.setSelection(ymEtPhone.getText().length());

            }
        }
        return "";
    }

    private String getPwd(){
        if (TextUtils.isEmpty(ymEtPhone.getText().toString().trim())){
            ymEtPhone.requestFocus();
        }else if (hasHistoryAccount){
            return ymEtPwd.getText().toString().trim();
        }else if (TextUtils.isEmpty(ymEtPwd.getText().toString())){
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_input_pwd")));
            ymEtPwd.requestFocus();
            ymEtPwd.setSelection(ymEtPwd.getText().length());
        }else if (ymEtPwd.getText().toString().length() < 6|| ymEtPwd.getText().toString().length()>20){
            ToastUtils.showToast(baseActivity, getString(ResourseIdUtils.getStringId("ym_tip_pwd_error")));
            ymEtPwd.requestFocus();
            ymEtPwd.setSelection(ymEtPwd.getText().length());
        }else {
            return ymEtPwd.getText().toString();
        }
        return "";
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }

    @Override
    public void onAutoPopSelected(HistoryAccountBean historyAccountBean, int position) {
        for (int i = 0; i < mSources.size(); i++) {
            if (i==position){
                mSources.get(mSources.size()-i-1).setCheck(true);
            }else{
                mSources.get(mSources.size()-i-1).setCheck(false);
            }
        }
        popAdapter.notifyDataSetChanged(mSources);
        hideHistoryView();
        ymEtPhone.setText(historyAccountBean.getPhone());
        ymEtPwd.setText("******");
        ymEtPhone.requestFocus();
        ymEtPhone.setSelection(ymEtPhone.getText().length());
        mAccountBean.setNumber(historyAccountBean.getPhone());
        mAccountBean.setPassword(historyAccountBean.getPassword());
        hasHistoryAccount = true;
        ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdclean"));
        currentPwdState = CLEANPWDSTATE;

    }

    @Override
    public void onItemDelet(int position,List<HistoryAccountBean> data) {
        if (data != null && data.size() > 0) {
            data.remove(data.size()-position-1);

            SharedPreferencesUtils.putHistoryAccountBean(getContext(), "histroyAccount",data, "histroyAccount");
            if (data.size()==1){
                historyPopView.dismiss();
                ymImArrow.setVisibility(View.INVISIBLE);
                ymEtPhone.setText(data.get(0).getPhone());
                ymEtPwd.setText("******");
                ymEtPhone.requestFocus();
                ymEtPhone.setSelection(ymEtPhone.getText().length());
                mAccountBean.setNumber(data.get(0).getPhone());
                mAccountBean.setPassword(data.get(0).getPassword());
                hasHistoryAccount = true;
                ymImSeePwd.setImageResource(ResourseIdUtils.getMipmapId("ym_pwdclean"));
                currentPwdState = CLEANPWDSTATE;
            }
        }

    }
    private int getPwdPageType(){
        int pwdPageType = (int) getArguments().getSerializable("pwdPageType");
        return pwdPageType;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBaseEvent(BaseEvent event){
        if (event instanceof UserEvent){
            newPhone = ((UserEvent) event).getPhone();
            newPwd = ((UserEvent) event).getPwd();
            ymEtPhone.setText(newPhone);
            ymEtPwd.setText("******");
            ymEtPhone.requestFocus();
            ymEtPhone.setSelection(ymEtPhone.getText().length());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void cancelLogin() {
        ymBtLogin.setClickable(true);

    }
}
