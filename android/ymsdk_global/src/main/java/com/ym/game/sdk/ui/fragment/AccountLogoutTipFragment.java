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

public class AccountLogoutTipFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private ImageView fengjiexianRight;
    private Button ymBtCancel;
    private Button ymBtConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_logouttip"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymBtCancel = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_cancel"));
        ymBtConfirm = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_confirm"));
        ymImBack.setOnClickListener(this);
        ymImClose.setOnClickListener(this);
        fengjiexianRight.setOnClickListener(this);
        ymBtCancel.setOnClickListener(this);
        ymBtConfirm.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));
        ymImClose.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ymImBack.getId()){
            back();
        }else if (v.getId() == ymImClose.getId()){
            finishActivity();
        }else if(v.getId() == ymBtCancel.getId()){
            finishActivity();
        }else if (v.getId() == ymBtConfirm.getId()){
            //TODO:
            UserPresenter.logout(baseActivity);
        }
    }

    /**
     * 返回
     */
    protected void back(){
        baseActivity.onBackPressed();
    }

    protected void finishActivity(){
        baseActivity.finish();
    }


    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }
}
