package com.ym.game.sdk.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.sdk.bean.AccountBean;

import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.invoke.plugin.WechatPluginApi;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.fragment.AccountBindFragment;
import com.ym.game.sdk.ui.fragment.AccountLoginFragment;
import com.ym.game.sdk.ui.fragment.AccountPasswordLoignFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.ym.game.sdk.ui.fragment.RealNameFragment;

public class YmUserActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", YmTypeConfig.LOGIN);
        if (type ==YmTypeConfig.QUICKPWDPAGE){
            AccountPasswordLoignFragment accountPasswordLoignFragment = AccountPasswordLoignFragment.getFragmentByName(this,AccountPasswordLoignFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("pwdPageType", YmTypeConfig.QUICKPWDPAGE);
            accountPasswordLoignFragment.setArguments(bundle);
            initFragment(accountPasswordLoignFragment);
        }else if (type == YmTypeConfig.LOGIN) {
//            LoginFragment loginFragment = LoginFragment.getFragmentByName(this, LoginFragment.class);
            AccountLoginFragment accountLoginFragment = AccountLoginFragment.getFragmentByName(this,AccountLoginFragment.class);
            initFragment(accountLoginFragment);
        }else if (type == YmTypeConfig.AUTOLOGIN){
            UserPresenter.autoLogin(this);
        }else if (type == YmTypeConfig.BIND){
            AccountBean accountBean = (AccountBean) getIntent().getSerializableExtra("accountBean");
            AccountBindFragment accountBindFragment = AccountBindFragment.getFragmentByName(this, AccountBindFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("accountBean", accountBean);
            accountBindFragment.setArguments(bundle);
            initFragment(accountBindFragment);
        }else if (type == YmTypeConfig.REALNAME){
            AccountBean accountBean = (AccountBean) getIntent().getSerializableExtra("accountBean");
            int realNameType =  getIntent().getIntExtra("realNameType",1);
            RealNameFragment realNameFragment = RealNameFragment.getFragmentByName(this, RealNameFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("accountBean", accountBean);
            bundle.putInt("realNameType",realNameType);
            realNameFragment.setArguments(bundle);
            initFragment(realNameFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserPresenter.onActivityResult(YmUserActivity.this,requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UserPresenter.onRequestPermissionsResult(YmUserActivity.this,requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PluginManager.getInstance().getPlugin("plugin_wechat")!=null){
            WechatPluginApi.getInstance().onResume(YmUserActivity.this);
        }
    }
}
