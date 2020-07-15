package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.event.BaseEvent;
import com.ym.game.sdk.event.PayTypeEvent;
import com.ym.game.sdk.model.IPurchaseModel;
import com.ym.game.sdk.model.IPurchaseView;
import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.utils.ImageUtils;
import com.ym.game.utils.ResourseIdUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PurchaseFragment extends BaseFragment implements View.OnClickListener, IPurchaseView {

    private static final String TAG = "Ymsdk";
    private ImageView ymImBack;
    private ImageView ymImClose;
    private ImageView fengjiexianRight;
    private TextView ymProductName;
    private TextView ymPrice;
    private View ymRlPay;
    private ImageView ymImPay;
    private TextView ymTvPay;
    private ImageView ymImPaymore;
    private Button ymBtPayok;
    private String payType;
    private static final String PAYTYPEALI = "alipay";
    private static final String PAYTYPEWEIXIN = "wxpay";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_pay"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymProductName = (TextView) view.findViewById(ResourseIdUtils.getId("ym_product_name"));
        ymPrice = (TextView) view.findViewById(ResourseIdUtils.getId("ym_price"));
        ymRlPay = view.findViewById(ResourseIdUtils.getId("ym_rl_pay"));
        ymImPay = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_pay"));
        ymTvPay = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_pay"));

        ymBtPayok = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_payok"));

        ymImClose.setOnClickListener(this);
        ymRlPay.setOnClickListener(this);
        ymBtPayok.setOnClickListener(this);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PurchaseBean purchaseDate = getPurchaseDate();

        String productName = purchaseDate.getProductName();
        String productPrice = purchaseDate.getProductPrice();
//        String productName = "新手装备大礼包648元";
//        String productPrice = "648";
        payType = PAYTYPEALI;

        ymImBack.setVisibility(View.INVISIBLE);
        ymImClose.setVisibility(View.VISIBLE);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity,ResourseIdUtils.getMipmapId("ym_fenjiexian")));
        ymProductName.setText(productName);
        ymPrice.setText(productPrice);
        ymImPay.setImageResource(ResourseIdUtils.getMipmapId("ym_"+ payType));
        ymTvPay.setText(ResourseIdUtils.getStringId("ym_tv_"+ payType));
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==ymImClose.getId()){
            //关闭界面
            PurchasePresenter.cancelPay(this);
        }else if(view.getId()==ymRlPay.getId()){
            ChoosePayTypeFragment choosePayTypeFragment = ChoosePayTypeFragment.getFragmentByName(baseActivity,ChoosePayTypeFragment.class);
            redirectFragment(choosePayTypeFragment);

        }else if (view.getId()==ymBtPayok.getId()){

            //TODO:先验证订单再根据支付类型去发起支付
            if (TextUtils.equals(PAYTYPEALI,payType)){
                //TODO:支付宝支付

            }else if (TextUtils.equals(PAYTYPEWEIXIN,payType)){
                //TODO:微信支付
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBaseEvent(BaseEvent event){
        if (event instanceof PayTypeEvent){
            payType = ((PayTypeEvent) event).getPayType();
            ymImPay.setImageResource(ResourseIdUtils.getMipmapId("ym_pay_"+ payType));
            ymTvPay.setText(ResourseIdUtils.getStringId("ym_tv_pay"+ payType));
            Log.i(TAG, "onBaseEvent: "+payType);
        }
    }

    private void checkPayState(String payType){
        PurchasePresenter.checkPayState(this,payType);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void closeActivity() {
        baseActivity.finish();
    }

    @Override
    public PurchaseBean getPurchaseDate() {
        PurchaseBean purchaseBean = (PurchaseBean) getArguments().getSerializable("purchaseBean");
        return purchaseBean;
    }
}
