package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;


import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.presenter.UserPresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountLoginFragment extends UserBaseFragment implements View.OnClickListener {


    private static final String TAG = "Ymsdk";
    private Button ymGplogin;
    private Button ymFblogin;
    private Button ymGtlogin;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_login"), null, true);
        ymGplogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_gplogin"));
        ymFblogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_fblogin"));
        ymGtlogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_gtlogin"));
        ymGplogin.setOnClickListener(this);
        ymFblogin.setOnClickListener(this);
//        ymGtlogin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()== ymFblogin.getId()){
            //TODO:fb登录
            UserPresenter.loginbyType(this, YmConstants.FBTYPE);
        }else if (view.getId()==ymGplogin.getId()){
            //TODO:gp登录
            UserPresenter.loginbyType(this, YmConstants.GOOGLETYPE);

        }else if(view.getId()==ymGtlogin.getId()){
            //TODO:gt游客登录
            UserPresenter.loginbyType(this, YmConstants.GUSETTYPE);
        }
    }




    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void cancelLogin() {

    }


}
