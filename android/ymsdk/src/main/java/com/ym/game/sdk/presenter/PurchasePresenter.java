package com.ym.game.sdk.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.ym.game.sdk.bean.PurchaseBean;

import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.model.IPurchaseView;
import com.ym.game.sdk.model.PurchaseModel;
import com.ym.game.sdk.ui.activity.YmPurchaseActivity;
import com.ym.game.sdk.ui.fragment.PurchaseFragment;

import java.io.Serializable;

public class PurchasePresenter {


    /**
     * 展示支付渠道
     * @param activity
     */
    public static void showPurchasePage(Activity activity, PurchaseBean purchaseBean){
//        if (UserPresenter.isLogin()){
            Intent intent = new Intent(activity, YmPurchaseActivity.class);
            intent.putExtra("purchaseBean", purchaseBean);
            activity.startActivity(intent);
//        }else{
//            Toast.makeText(activity,"请先登录",Toast.LENGTH_SHORT).show();
//            CallbackManager.getPurchaseCallback().onFail("未登录");
//        }

    }

    public static void initPay(Activity activity){
        PurchaseModel.getInstance().initPay(activity);
    }

    public static void cancelPay(IPurchaseView purchaseView){
        purchaseView.closeActivity();
        CallbackMananger.getPayCallBack().onCancel();
    }
    public static void startPay(final IPurchaseView purchaseView, String payType){
        PurchaseBean purchaseDate = purchaseView.getPurchaseDate();
        purchaseDate.setPayType(payType);
        PurchaseModel.getInstance().startPay(purchaseView.getContext(), purchaseDate, new PurchaseModel.PurchaseStatusListener() {


            @Override
            public void onSuccess(String platformOrderId) {
                purchaseView.dismissLoading();
                purchaseView.closeActivity();
                CallbackMananger.getPayCallBack().onSuccess(platformOrderId);
            }

            @Override
            public void onFail(int errorCode, String msg) {
                purchaseView.dismissLoading();
                purchaseView.closeActivity();
                CallbackMananger.getPayCallBack().onFailure(errorCode,msg);
            }

        });
    }


    public static void destroy(Activity activity) {
        PurchaseModel.getInstance().destroy(activity);
    }
}
