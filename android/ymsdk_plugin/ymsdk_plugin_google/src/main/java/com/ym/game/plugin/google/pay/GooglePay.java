package com.ym.game.plugin.google.pay;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.ym.game.plugin.google.Security;
import com.ym.game.plugin.google.dao.DaoUtils;
import com.ym.game.plugin.google.dao.LocalPurchaseBean;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class GooglePay implements PurchasesUpdatedListener, BillingClientStateListener{
    public String TAG ="zc GooglePay";
    public volatile static GooglePay INSTANCE;
    private BillingClient billingClient;
    private Context mContext;
    private String currentProductId;
    private CallBackListener mInitCallBackListener;
    private CallBackListener mPayCallBackListener;
    private CallBackListener mGetGPVerifyParamListener;
    private Map<String, Object> mPayMap;

    public GooglePay(){}

    public static GooglePay getInstance() {
        if (INSTANCE==null){
            synchronized (GooglePay.class){
                if (INSTANCE==null){
                    INSTANCE = new GooglePay();
                }
            }
        }
        return INSTANCE;
    }

    public void initPay(Context context,CallBackListener initCallBackListener){
        mInitCallBackListener = initCallBackListener;
        billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        startConnection();
    }

    public void pay(Context context, Map<String,Object> map, CallBackListener callBackListener,CallBackListener getGPVerifyParamListener){
        mContext = context;
        mPayMap = map;
        mPayCallBackListener = callBackListener;
        mGetGPVerifyParamListener = getGPVerifyParamListener;
        currentProductId = (String)map.get("productId");

        queryPurchasesHistory();
    }

    public void startConnection(){
        if (!isClientInit()){
            billingClient.startConnection(this);
        }
    }

    public void endConnection(){
        if (isClientInit()){
            billingClient.endConnection();
        }
    }

    //查询消费情况
    public void queryPurchasesHistory() {
        if (!isClientInit()) {
            return;
        }
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        Log.e(TAG, "queryPurchase code = " + purchasesResult.getResponseCode() + " getPurchasesList = " + purchasesResult.getPurchasesList());
        // 查询成功且列表不为空
        if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !purchasesResult.getPurchasesList().isEmpty()) {
            for (Purchase purchase :purchasesResult.getPurchasesList()) {
                if (currentProductId.equals(purchase.getSku())){
                    if (saveLocalPurchaseDB(purchase.getSku(),PurchaseState.NOCONSUME)){
                        delayedPaySuccessCallback();
                        handlePurchaseHistory(purchase);
                        return;
                    }
                }
            }
        }
        querySkuDetailsAsync();

    }

    private void delayedPaySuccessCallback() {
        if (mPayCallBackListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    mPayCallBackListener.onSuccess("ok");
                }
            }, 1000);
        }
    }

    private void handlePurchaseHistory(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
                        updateLocalPurchaseDB(purchase, PurchaseState.CONSUMESUCCESS);
                        //TODO:
                    } else {
                        // 消费失败,后面查询消费记录后再次消费，否则，就只能等待退款
                        updateLocalPurchaseDB(purchase,PurchaseState.CONSUMEFAIL);
                        //TODO:
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isSignatureValid(Purchase purchase){
        return Security.verifyPurchase(Security.BASE_64_ENCODED_PUBLIC_KEY, purchase.getOriginalJson(), purchase.getSignature());
    }

    private boolean isClientInit() {
        return billingClient.isReady();
    }

    public void querySkuDetailsAsync(){
        List<String> skuList = new ArrayList<>();
//        skuList.add("com.ym.ysfjtest01");
        skuList.add(currentProductId);
        skuList.add("xxx");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        int responseCode = billingResult.getResponseCode();
                        billingResult.getDebugMessage();
                        if (responseCode==BillingClient.BillingResponseCode.OK){
                            if (skuDetailsList == null || skuDetailsList.isEmpty()){
                                //订单错误
                                if (mPayCallBackListener!=null){
                                    mPayCallBackListener.onFailure(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_google_pay_ordererror")));
                                }
                                return;
                            }
                            SkuDetails skuDetails = null;
                            for (SkuDetails details: skuDetailsList) {
                                if (currentProductId.equals(details.getSku())){
                                    skuDetails = details;
                                }
                            }
                            if (skuDetails!=null){
                                saveLocalPurchaseDB(skuDetails.getSku(),PurchaseState.PURCHASING);
                                launchBillingFlow((Activity)mContext,skuDetails);
                            }else {
                                if (mPayCallBackListener!=null){
                                    mPayCallBackListener.onFailure(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_google_pay_ordererror")));
                                }
                            }

                        }else {
                            if (mPayCallBackListener!=null){
                                mPayCallBackListener.onFailure(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_google_pay_ordererror")));
                            }
                        }

                    }
                });
    }


    public void launchBillingFlow(Activity activity,SkuDetails skuDetails){
        //An activity reference from which the billing flow will be launched.
    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
        Log.i(TAG, "launchBillingFlow: "+responseCode);
    }


    //付款1 消费成功 0 未消费成功2
    public void handlePurchase(Purchase purchase){
        if (purchase.getPurchaseState()==Purchase.PurchaseState.PURCHASED){
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
                        updateLocalPurchaseDB(purchase,PurchaseState.CONSUMESUCCESS);
//                        purchase.getDeveloperPayload()


                    } else {
                        // 消费失败,后面查询消费记录后再次消费，否则，就只能等待退款
                        updateLocalPurchaseDB(purchase,PurchaseState.CONSUMEFAIL);
                    }
                }
            });
        }

    }

    private LocalPurchaseBean queryByproductId(String productId){
        DaoUtils daoUtils = new DaoUtils(mContext);
        List<LocalPurchaseBean> localPurchaseBeanList = daoUtils.queryAll();
        if (!localPurchaseBeanList.isEmpty()&&localPurchaseBeanList.size()>0){
            for (int i = 0; i < localPurchaseBeanList.size(); i++) {
                if (productId.equals(localPurchaseBeanList.get(i).getProductId())){
                    return localPurchaseBeanList.get(i);
                }
            }

        }
        return null;
    }

    private boolean saveLocalPurchaseDB(String sku, int payState) {

        DaoUtils daoUtils = new DaoUtils(mContext);
        LocalPurchaseBean purchaseHistory = new LocalPurchaseBean();
        purchaseHistory.uid = (String) mPayMap.get("userId");
        purchaseHistory.gameOrderId = (String) mPayMap.get("gameOrderId");
        purchaseHistory.productId = sku;
        purchaseHistory.purchaseState = payState;
        //保存数据库通知服务器订单信息
        LocalPurchaseBean queryLocalPurchaseBean = queryByproductId(sku);
        if (queryLocalPurchaseBean!=null){
            purchaseHistory.id = queryLocalPurchaseBean.getId();
            return daoUtils.updatePurchase(purchaseHistory);
        }
        return daoUtils.insertPurchase(purchaseHistory);
    }

    private void updateLocalPurchaseDB(Purchase purchase,int payState) {
//        purchase.getDeveloperPayload();
        LocalPurchaseBean queryLocalPurchaseBean = queryByproductId(purchase.getSku());
        if (queryLocalPurchaseBean==null){
            return;
        }

        DaoUtils daoUtils = new DaoUtils(mContext);
        queryLocalPurchaseBean.googleOrderId = purchase.getOrderId();
        queryLocalPurchaseBean.purchaseToken = purchase.getPurchaseToken();
        queryLocalPurchaseBean.googleAccountId = purchase.getAccountIdentifiers().getObfuscatedAccountId();
        queryLocalPurchaseBean.productId = purchase.getSku();
        queryLocalPurchaseBean.purchaseTime = purchase.getPurchaseTime();
        queryLocalPurchaseBean.packageName = purchase.getPackageName();
        queryLocalPurchaseBean.purchaseState = payState;
        //保存数据库通知服务器订单信息


//                            daoUtils.updatePurchase();
        boolean updateOk = daoUtils.updatePurchase(queryLocalPurchaseBean);
        if (updateOk){
            //上报数据
            mGetGPVerifyParamListener.onSuccess("ok");
        }
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
            mInitCallBackListener.onSuccess("ok");
        }else if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
            //Some apps may choose to make decisions based on this knowledge.
            Log.d(TAG, billingResult.getDebugMessage());
        }else {
            Log.d(TAG, billingResult.getDebugMessage());
        }
    }


    @Override
    public void onBillingServiceDisconnected() {
        startConnection();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        // To be implemented in a later section.
        //TODO:先获取检查支付状态
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {

            delayedPaySuccessCallback();

            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            queryPurchasesHistory();
        } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED){
//            connectToPlayBillingService();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            if (mPayCallBackListener!=null){
                mPayCallBackListener.onFailure(ErrorCode.PAY_CANCEL,mContext.getString(ResourseIdUtils.getStringId("ym_google_pay_ordererror")));
            }
        } else {
            // Handle any other error codes.
            if (mPayCallBackListener!=null){
                mPayCallBackListener.onFailure(ErrorCode.PAY_FAIL,mContext.getString(ResourseIdUtils.getStringId("ym_google_pay_fail")));
            }
        }
    }

}

