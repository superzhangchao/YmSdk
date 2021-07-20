package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.presenter.UserPresenter;

import com.ym.game.utils.CommonUtils;
import com.ym.game.utils.ImageUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountBindFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private ImageView fengjiexianRight;
    private ImageView imGoogleBind;
    private ImageView imFbBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_bind"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        imGoogleBind = (ImageView) view.findViewById(ResourseIdUtils.getId("im_google_bind"));
        imFbBind = (ImageView) view.findViewById(ResourseIdUtils.getId("im_fb_bind"));

        ymImBack.setOnClickListener(this);
        ymImClose.setOnClickListener(this);
        imGoogleBind.setOnClickListener(this);
        imFbBind.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));
        ymImBack.setVisibility(View.VISIBLE);
        ymImClose.setVisibility(View.VISIBLE);



    }

    @Override
    public void onClick(View view) {
        if (view.getId()==ymImBack.getId()){
            back();
        }else if (view.getId()== ymImClose.getId()){
            cancelBindAndFinish();
        }else if(view.getId()==imGoogleBind.getId()){
            //TODO:绑定gp
            if (CommonUtils.isFastDoubleClick()){
                return;
            }
            UserPresenter.bindByType(this, YmConstants.GOOGLETYPE);
        }else if (view.getId()==imFbBind.getId()){
            //TODO:绑定fb
            if (CommonUtils.isFastDoubleClick()){
                return;
            }
            UserPresenter.bindByType(this, YmConstants.FBTYPE);
        }

    }




    @Override
    public AccountBean getAccountData() {
        AccountBean accountData = (AccountBean) getArguments().getSerializable("accountBean");
        return accountData;
    }


    protected void cancelBindAndFinish(){
        UserPresenter.cancelBind();
        baseActivity.finish();
    }

    protected void cancelBind(){
        UserPresenter.cancelBind();
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
