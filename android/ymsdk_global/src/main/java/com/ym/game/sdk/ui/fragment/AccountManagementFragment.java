package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.utils.ImageUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountManagementFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private ImageView fengjiexianRight;
    private Button ymBtBind;
    private Button ymBtSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_management"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymBtBind = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_bind"));
        ymBtSwitch = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_switch"));
        ymImClose.setOnClickListener(this);
        ymBtBind.setOnClickListener(this);
        ymBtSwitch.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));
        ymImBack.setVisibility(View.INVISIBLE);
        ymImClose.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ymImClose.getId()){
            cancelBindAndFinish();
        }else if (v.getId() == ymBtBind.getId()){
            AccountBindFragment accountBindFragment = getFragmentByName(baseActivity, AccountBindFragment.class);
            redirectFragment(accountBindFragment);
        }else if (v.getId() == ymBtSwitch.getId()){
            AccountLogoutTipFragment accountLogoutTipFragment = getFragmentByName(baseActivity, AccountLogoutTipFragment.class);
            redirectFragment(accountLogoutTipFragment);
        }
    }

    @Override
    public boolean onBackPressed() {
        cancelBind();
        return BackHandlerHelper.handleBackPress(this);
    }

    protected void cancelBindAndFinish(){
        UserPresenter.cancelBind();
        baseActivity.finish();
    }

    protected void cancelBind(){
        UserPresenter.cancelBind();
    }

    protected void finishActivity(){
        baseActivity.finish();
    }

}
