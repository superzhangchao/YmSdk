package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ym.game.sdk.R;
import com.ym.game.utils.ResourseIdUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountLoginFragment extends BaseFragment implements View.OnClickListener {

    private View content;
    private static final String TAG = "Ymsdk";
    private View ll_content;
    private View rl_title;
    private ImageView ymImBack;
    private ImageView ymImClose;
    private EditText ymEtPhone;
    private EditText ymEtPhonecode;
    private TextView ymTvPhonecode;
    private ImageView ymImUnCkXieyi;
    private ImageView ymImCkXieyi;
    private TextView ymTvXieyi;
    private ImageView fengjiexianRight;
    private Button ymBtLogin;
    private ImageView ymLoginWeixin;
    private ImageView ymLoginQq;
    private ImageView ymLoginGt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_login"), null, true);
//        ll_content = view.findViewById(ResourseIdUtils.getId("ll_content"));
//        rl_title = view.findViewById(ResourseIdUtils.getId("rl_title"));

        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymEtPhone = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phone"));
        ymEtPhonecode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_et_phonecode"));
        ymTvPhonecode = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_phonecode"));
        ymImUnCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_unck_xieyi"));
        ymImCkXieyi = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_ck_xieyi"));
        ymTvXieyi = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_xieyi"));
        ymBtLogin = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_login"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymLoginWeixin = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_weixin"));
        ymLoginQq = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_qq"));
        ymLoginGt = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_login_gt"));

        ymImBack.setOnClickListener(this);
        ymTvPhonecode.setOnClickListener(this);
        ymImUnCkXieyi.setOnClickListener(this);
        ymBtLogin.setOnClickListener(this);
        ymLoginWeixin.setOnClickListener(this);
        ymLoginQq.setOnClickListener(this);
        ymBtLogin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ymImBack.setVisibility(View.VISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        ymTvPhonecode.setText(ResourseIdUtils.getStringId("ym_tv_getphonecode"));
//        ymTvXieyi.setText("dfsdfsf");
//        ll_content.post(new Runnable(){
//
//            @Override
//            public void run() {
//                int height = ll_content.getMeasuredHeight();
//                int width = ll_content.getMeasuredWidth();
//
//                Log.i(TAG, "run: ll_content height:"+height+" width:"+width);
//            }
//
//
//        });
//
//        rl_title.post(new Runnable() {
//            @Override
//            public void run() {
//                int height = rl_title.getMeasuredHeight();
//                int width = rl_title.getMeasuredWidth();
//                Log.i(TAG, "run: rl_title height:"+height+" width:"+width);
//            }
//        });


    }

    @Override
    public void onClick(View view) {

    }
}
