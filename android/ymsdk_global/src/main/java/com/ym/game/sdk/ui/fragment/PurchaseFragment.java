package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.common.base.parse.plugin.PluginManager;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.event.BaseEvent;
import com.ym.game.sdk.event.PayTypeEvent;
import com.ym.game.sdk.model.IPurchaseView;
import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

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
        ymImPaymore = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_paymore"));
        ymTvPay = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_pay"));

        ymBtPayok = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_payok"));

        ymImBack.setOnClickListener(this);

        ymBtPayok.setOnClickListener(this);
        EventBus.getDefault().register(this);
        PurchasePresenter.initPay(baseActivity);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PurchaseBean purchaseDate = getPurchaseData();

        String productName = purchaseDate.getProductName();
        String productPrice = purchaseDate.getProductPrice();

        Double price = Double.parseDouble(productPrice)/100;
        DecimalFormat df = new DecimalFormat("0.00");//格式化
        String formatPrice = df.format(price);
        ymImPaymore.setVisibility(View.INVISIBLE);
        if (PluginManager.getInstance().getPlugin("plugin_alipay")!=null&&PluginManager.getInstance().getPlugin("plugin_wechat")!=null){
            payType = PAYTYPEALI;
            ymRlPay.setOnClickListener(this);
            ymImPaymore.setVisibility(View.VISIBLE);
        }else if (PluginManager.getInstance().getPlugin("plugin_alipay")!=null){
            payType = PAYTYPEALI;
        }else if (PluginManager.getInstance().getPlugin("plugin_wechat")!=null){
            payType = PAYTYPEWEIXIN;
        }
        ymImBack.setVisibility(View.VISIBLE);
        ymImClose.setVisibility(View.INVISIBLE);
        ymImBack.setImageResource(ResourseIdUtils.getMipmapId("ym_close"));

        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));
        ymProductName.setText(productName);
        ymPrice.setText(formatPrice);
        ymImPay.setImageResource(ResourseIdUtils.getMipmapId("ym_"+ payType));
        ymTvPay.setText(ResourseIdUtils.getStringId("ym_tv_"+ payType));
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==ymImBack.getId()){
            //关闭界面
            PurchasePresenter.cancelPay(this);
        }else if(view.getId()==ymRlPay.getId()){
            ChoosePayTypeFragment choosePayTypeFragment = ChoosePayTypeFragment.getFragmentByName(baseActivity,ChoosePayTypeFragment.class);
            redirectFragment(choosePayTypeFragment);

        }else if (view.getId()==ymBtPayok.getId()){
            ymBtPayok.setClickable(false);
//            PurchasePresenter.startPay(this,payType);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBaseEvent(BaseEvent event){
        if (event instanceof PayTypeEvent){
            payType = ((PayTypeEvent) event).getPayType();
            ymImPay.setImageResource(ResourseIdUtils.getMipmapId("ym_"+ payType));
            ymTvPay.setText(ResourseIdUtils.getStringId("ym_tv_"+ payType));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        PurchasePresenter.destroy(baseActivity);
    }

    @Override
    public void showLoading() {
        baseActivity.showLoading();
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
    public void cancelPay() {
        ymBtPayok.setClickable(true);
    }

    @Override
    public PurchaseBean getPurchaseData() {
        PurchaseBean purchaseBean = (PurchaseBean) getArguments().getSerializable("purchaseBean");
        return purchaseBean;
    }

    @Override
    public boolean onBackPressed() {
        PurchasePresenter.cancelPay(this);
        return true;
    }
}
