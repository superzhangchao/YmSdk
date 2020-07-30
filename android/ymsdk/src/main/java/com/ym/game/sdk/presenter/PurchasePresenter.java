package com.ym.game.sdk.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.ym.game.sdk.R;
import com.ym.game.sdk.base.config.ErrorCode;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.PurchaseBean;

import com.ym.game.sdk.callback.CallbackMananger;
import com.ym.game.sdk.model.IPurchaseView;
import com.ym.game.sdk.model.PurchaseModel;
import com.ym.game.sdk.ui.activity.YmPurchaseActivity;
import com.ym.game.sdk.ui.fragment.PurchaseFragment;
import com.ym.game.utils.ResourseIdUtils;
import com.ym.game.utils.ToastUtils;

import java.io.Serializable;

public class PurchasePresenter {


    private static Activity purchaseActivity;

    /**
     * 展示支付渠道
     * @param activity
     */
    public static void showPurchasePage(Activity activity, PurchaseBean purchaseBean){
        purchaseActivity = activity;
        if (UserPresenter.isLogin()){
            Intent intent = new Intent(activity, YmPurchaseActivity.class);
            intent.putExtra("purchaseBean", purchaseBean);
            activity.startActivity(intent);
        }else{
            ToastUtils.showToast(activity,activity.getString(ResourseIdUtils.getStringId("ym_text_please_login")));
            CallbackMananger.getPayCallBack().onFailure(ErrorCode.PAY_FAIL,activity.getString(ResourseIdUtils.getStringId("ym_text_nologin")));
        }

    }

    public static void initPay(Activity activity){
        PurchaseModel.getInstance().initPay(activity);
    }

    public static void cancelPay(IPurchaseView purchaseView){
        purchaseView.closeActivity();
        CallbackMananger.getPayCallBack().onCancel();
    }
    public static void startPay(final IPurchaseView purchaseView, String payType){
        purchaseView.showLoading();
        AccountBean loginAccountInfo = UserPresenter.getLoginAccountInfo();
        PurchaseBean purchaseDate = purchaseView.getPurchaseData();
        purchaseDate.setPayType(payType);
        purchaseDate.setUserId(loginAccountInfo.getUid());
        PurchaseModel.getInstance().startPay(purchaseView.getContext(), purchaseDate, new PurchaseModel.PurchaseStatusListener() {

            @Override
            public void onSuccess(String platformOrderId) {
                purchaseView.dismissLoading();
                purchaseView.closeActivity();
                CallbackMananger.getPayCallBack().onSuccess(platformOrderId);
            }

            @Override
            public void onCancel() {
                purchaseView.dismissLoading();
                purchaseView.closeActivity();
                ToastUtils.showToast(purchaseActivity,purchaseActivity.getString(ResourseIdUtils.getStringId("ym_text_paycancel")));
            }

            @Override
            public void onFail(int errorCode, String msg) {
                purchaseView.dismissLoading();
                purchaseView.closeActivity();
                ToastUtils.showToast(purchaseActivity,msg);
                CallbackMananger.getPayCallBack().onFailure(errorCode,msg);
            }

        });
    }


    public static void destroy(Activity activity) {
        PurchaseModel.getInstance().destroy(activity);
    }

    public static void checkWxPay() {
        boolean wxPayStatus = PurchaseModel.getInstance().getWxPayStatus();
        if (wxPayStatus){
            PurchaseModel.getInstance().resetWxPay();
        }
    }
}
