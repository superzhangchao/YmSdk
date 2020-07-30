package com.ym.game.sdk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.model.IUserModel;
import com.ym.game.sdk.model.UserModel;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.sdk.ui.fragment.AccountBindFragment;
import com.ym.game.sdk.ui.fragment.AccountLoginFragment;
import com.ym.game.sdk.ui.fragment.PurchaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ym.game.sdk.base.config.TypeConfig;
import com.ym.game.sdk.ui.fragment.RealNameFragment;

public class YmUserActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type",TypeConfig.LOGIN);
        if (type == TypeConfig.LOGIN) {
//            LoginFragment loginFragment = LoginFragment.getFragmentByName(this, LoginFragment.class);
            AccountLoginFragment accountLoginFragment = AccountLoginFragment.getFragmentByName(this,AccountLoginFragment.class);
            initFragment(accountLoginFragment);
        }else if (type == TypeConfig.AUTOLOGIN){
            UserPresenter.autoLogin(this);
        }else if (type == TypeConfig.BIND){
            AccountBean accountBean = (AccountBean) getIntent().getSerializableExtra("accountBean");
            AccountBindFragment accountBindFragment = AccountBindFragment.getFragmentByName(this, AccountBindFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("accountBean", accountBean);
            accountBindFragment.setArguments(bundle);
            initFragment(accountBindFragment);
        }else if (type == TypeConfig.REALNAME){
            AccountBean accountBean = (AccountBean) getIntent().getSerializableExtra("accountBean");
            RealNameFragment realNameFragment = RealNameFragment.getFragmentByName(this, RealNameFragment.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("accountBean", accountBean);
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
        UserPresenter.checkWxLogin();
    }
}
