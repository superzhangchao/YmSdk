package com.ym.game.sdk.ui.fragment;

import android.content.Context;

import com.ym.game.sdk.model.IUserView;

public class UserBaseFragment extends BaseFragment implements IUserView {

    @Override
    public Context getContext() {
        return baseActivity;
    }
}
