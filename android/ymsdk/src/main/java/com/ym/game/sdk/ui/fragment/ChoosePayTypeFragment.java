package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ym.game.sdk.common.utils.ImageUtils;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.event.PayTypeEvent;


import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChoosePayTypeFragment extends BaseFragment implements View.OnClickListener {

    private ImageView fengjiexianRight;
    private ImageView ymImBack;
    private ImageView ymImClose;
    private View ymRlAli;
    private View ymRlWeixin;
    private static final String PAYTYPEALI = "alipay";
    private static final String PAYTYPEWX = "wxpay";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_paytype"), null, true);

        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymRlAli = view.findViewById(ResourseIdUtils.getId("ym_rl_alipay"));
        ymRlWeixin = view.findViewById(ResourseIdUtils.getId("ym_rl_wxpay"));

        ymImBack.setOnClickListener(this);
        ymRlAli.setOnClickListener(this);
        ymRlWeixin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ymImBack.setVisibility(View.VISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity,ResourseIdUtils.getMipmapId("ym_fenjiexian")));

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==ymImBack.getId()){
            back();
        }else if (view.getId()==ymRlAli.getId()){
            PayTypeEvent payTypeEvent = new PayTypeEvent();
            payTypeEvent.setPayType(PAYTYPEALI);
            EventBus.getDefault().post(payTypeEvent);
            back();
        }else if (view.getId()==ymRlWeixin.getId()){
            PayTypeEvent payTypeEvent = new PayTypeEvent();
            payTypeEvent.setPayType(PAYTYPEWX);
            EventBus.getDefault().post(payTypeEvent);
            back();
        }
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }
}
