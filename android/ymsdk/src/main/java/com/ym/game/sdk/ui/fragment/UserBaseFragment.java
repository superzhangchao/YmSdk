package com.ym.game.sdk.ui.fragment;

import android.content.Context;

import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.model.IUserView;


public class UserBaseFragment extends BaseFragment implements IUserView {

    @Override
    public Context getContext() {
        return baseActivity;
    }

    @Override
    public void showLoading() {
        baseActivity.showLoading();
    }

    @Override
    public AccountBean getAccountData() {
        return null;
    }

    @Override
    public void dismissLoading() {
        baseActivity.dismissLoading();
    }


    @Override
    public void closeActivity() {
        baseActivity.finish();
    }

    @Override
    public void cancelLogin() {

    }
}
