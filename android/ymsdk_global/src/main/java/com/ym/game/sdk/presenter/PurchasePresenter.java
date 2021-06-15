package com.ym.game.sdk.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import com.ym.game.plugin.google.dao.LocalPurchaseBean;
import com.ym.game.sdk.callback.listener.CreateOrderListener;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.PayStateListener;
import com.ym.game.sdk.config.YmErrorCode;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.PurchaseBean;

import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.model.IPurchaseView;
import com.ym.game.sdk.model.PurchaseModel;
import com.ym.game.sdk.model.UserModel;
import com.ym.game.sdk.ui.activity.YmPurchaseActivity;

import java.util.Map;


public class PurchasePresenter {
    private static Context mContext;

    /**
     * 展示支付渠道
     * @param activity
     */
//    public static void showPurchasePage(Activity activity, PurchaseBean purchaseBean){
//        purchaseActivity = activity;
//        if (UserPresenter.isLogin()){
//            Intent intent = new Intent(activity, YmPurchaseActivity.class);
//            intent.putExtra("purchaseBean", purchaseBean);
//            activity.startActivity(intent);
//        }else{
//            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_please_login")));
//            CallbackMananger.getPayCallBack().onFailure(YmErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_nologin")));
//        }
//
//    }

    public static void initPay(Activity activity){
        PurchaseModel.getInstance().initPay(activity);
    }

    public static void cancelPay(IPurchaseView purchaseView){
        purchaseView.closeActivity();
        CallbackMananger.getPayCallBack().onCancel();
    }

    public static void createOrder(Context context, final PurchaseBean purchaseDate) {
        mContext = context;
        PurchaseModel.getInstance().getVerifyData(mContext, new GetVerifyDataListener() {
            @Override
            public void onSuccess(String ts, String accessToken) {
                purchaseDate.setTs(ts);
                purchaseDate.setAccessToken(accessToken);
                createOrder(purchaseDate);

            }

            @Override
            public void onFail(int status, String message) {
                CallbackMananger.getPayCallBack().onFailure(status, message);
            }
        });
    }

    public static void checkPurchaseState(Context context){
        PurchaseModel.getInstance().checkPurchaseState(context);
    }

    public static void createOrder(final PurchaseBean purchaseDate){
        PurchaseModel.getInstance().createOrder(purchaseDate, new CreateOrderListener() {
            @Override
            public void onSuccess() {
                startPay(mContext,purchaseDate);
            }

            @Override
            public void onCancel() {
                CallbackMananger.getPayCallBack().onCancel();
            }

            @Override
            public void onFail(int status, String message) {
                CallbackMananger.getPayCallBack().onFailure(status,message);
            }
        });

    }

    public static void startPay(final Context context, PurchaseBean purchaseDate){
        PurchaseModel.getInstance().startPay(context, purchaseDate, new PayStateListener() {
            @Override
            public void onSuccess() {
                CallbackMananger.getPayCallBack().onSuccess("onSuccess");
            }

            @Override
            public void onCancel() {
                CallbackMananger.getPayCallBack().onCancel();
            }

            @Override
            public void onFail(int status, String message) {
                CallbackMananger.getPayCallBack().onFailure(status,message);
            }
        });
    }

}
