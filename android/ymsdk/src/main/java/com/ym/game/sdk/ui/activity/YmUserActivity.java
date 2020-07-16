package com.ym.game.sdk.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ym.game.sdk.ui.fragment.AccountLoginFragment;

import androidx.annotation.Nullable;

import static com.ym.game.sdk.base.config.TypeConfig.LOGIN;

public class YmUserActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type",LOGIN);
        if (type == LOGIN) {
//            LoginFragment loginFragment = LoginFragment.getFragmentByName(this, LoginFragment.class);
            AccountLoginFragment accountLoginFragment = AccountLoginFragment.getFragmentByName(this,AccountLoginFragment.class);
            initFragment(accountLoginFragment);
        }
    }
}
