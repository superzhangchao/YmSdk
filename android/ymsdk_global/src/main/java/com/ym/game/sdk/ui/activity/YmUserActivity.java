package com.ym.game.sdk.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.presenter.UserPresenter;

import com.ym.game.sdk.ui.fragment.AccountLoginFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ym.game.sdk.config.YmTypeConfig;
import com.ym.game.sdk.ui.fragment.AccountManagementFragment;

public class YmUserActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", YmTypeConfig.LOGIN);
        if (type == YmTypeConfig.LOGIN) {
            AccountLoginFragment accountLoginFragment = AccountLoginFragment.getFragmentByName(this,AccountLoginFragment.class);
            initFragment(accountLoginFragment);
        }else if (type == YmTypeConfig.AUTOLOGIN){
            UserPresenter.autoLogin(this);
        }else if (type == YmTypeConfig.BIND){
            AccountManagementFragment accountBindmanegenentFragment = AccountManagementFragment.getFragmentByName(this, AccountManagementFragment.class);
            initFragment(accountBindmanegenentFragment);
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
    }
}
