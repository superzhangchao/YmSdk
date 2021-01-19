package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.common.utils.IdentityUtils;
import com.ym.game.sdk.common.utils.ImageUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.presenter.UserPresenter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RealNameFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private ImageView fengjiexianRight;
    private EditText ymRealName;
    private EditText ymIdcardCode;
    private Button ymBtRealName;
    private AccountBean mAccountData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_realname"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymRealName = (EditText) view.findViewById(ResourseIdUtils.getId("ym_real_name"));
        ymIdcardCode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_idcard_code"));
        ymBtRealName = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_realName"));

        ymImBack.setOnClickListener(this);
        ymBtRealName.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAccountData = getAccountData();
        ymImBack.setVisibility(View.INVISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity,ResourseIdUtils.getMipmapId("ym_fenjiexian")));

    }

    @Override
    public void onClick(View view) {
        if(view.getId()== ymImBack.getId()){
            //TODO:关闭实名界面
            UserPresenter.cancelRealName(this);
        }else if(view.getId()== ymBtRealName.getId()){
            startRealName();
        }
    }

    private void startRealName(){
        String name = ymRealName.getText().toString();
        String idCard = ymIdcardCode.getText().toString();

        if(name.isEmpty()&&idCard.isEmpty()){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname1")));
            ymRealName.requestFocus();
        }else if(name.isEmpty()){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname2")));
            ymRealName.requestFocus();
        }else if(idCard.isEmpty()){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname3")));
            ymIdcardCode.requestFocus();
        }else if(!IdentityUtils.isChinese(name)){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname4")));
            ymRealName.requestFocus();
        }else if(!IdentityUtils.checkIDCard(idCard)){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname5")));
            ymIdcardCode.requestFocus();
        }else{
            AccountBean accountBean = mAccountData;
            accountBean.setName(name);
            accountBean.setIdCard(idCard);
            accountBean.setLoginType("phone");
            UserPresenter.startRealName(this,accountBean);
        }
    }

    @Override
    public AccountBean getAccountData() {
        AccountBean accountData = (AccountBean) getArguments().getSerializable("accountBean");
        return accountData;
    }

    @Override
    public boolean onBackPressed() {

        return true;
    }
}
