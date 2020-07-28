package com.ym.ysfj;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.sdk.YmSdkApi;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.utils.ToastUtils;
import com.ym.game.utils.YmSignUtils;
import com.ym.ysfj.R;


import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "Ymsdk";
    private Button btInit;
    private Button btLogin;
    private Button btLogout;
    private Button btPay;
    private Button btSendinfo;
    private IWXAPI api;
    private String text;
    private String key ="vJV8kCxV";
    private String encryptDES;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btInit = (Button) findViewById(R.id.bt_init);
        btLogin = (Button) findViewById(R.id.bt_login);
        btLogout = (Button) findViewById(R.id.bt_logout);
        btPay = (Button) findViewById(R.id.bt_pay);
        btSendinfo = (Button) findViewById(R.id.bt_sendinfo);
        tv = (TextView) findViewById(R.id.tv);
        btInit.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btLogout.setOnClickListener(this);
//        btLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                text = "fb-85321122112";
//                try {
//                    encryptDES = encryptDES(text, key);
//                    Log.i(TAG, "onClick:encryptDES "+encryptDES);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        btPay.setOnClickListener(this);
        btSendinfo.setOnClickListener(this);
        YmSdkApi.getInstance().initPlatform(this,"5012");
    }









    @Override
    protected void onResume() {
        super.onResume();
        YmSdkApi.getInstance().onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YmSdkApi.getInstance().onDestroy(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YmSdkApi.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==btInit.getId()){
            YmSdkApi.getInstance().setDebugMode(true);
        }else if(btLogin.getId()==v.getId()){
            YmSdkApi.getInstance().login(this, new LoginCallBack() {
                @Override
                public void onCancel() {
                    Log.i(TAG, "onCancle: ");
                    tv.setText("onCancle");
                }

                @Override
                public void onSuccess(Object o) {
                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) o;

                    Log.i(TAG, "onSuccess: "+resultAccoutBean.getData().toString());
                    tv.setText(resultAccoutBean.getData().toString());
                    ToastUtils.showToast(MainActivity.this,resultAccoutBean.getData().toString());
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.i(TAG, "onFailure: ");
                    tv.setText("onFailure");
                }
            });

        }else if (btSendinfo.getId()==v.getId()){
//            YmSdkApi.getInstance().resetFastLogin(false);
        }else if(btLogout.getId() ==v.getId()){
            YmSdkApi.getInstance().logout(this);
        }else if(btPay.getId()==v.getId()){

            YmSdkApi.getInstance().pay(this,getPurchaseBean(), new PayCallBack() {
                @Override
                public void onSuccess(Object o) {

                }

                @Override
                public void onFailure(int code, String msg) {

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    public PurchaseBean getPurchaseBean(){


        String orderId =  System.currentTimeMillis() +"";
        String gameSign = getGameSign("itemId_60","新手装备大礼包","1",orderId,
                "s1","147258","张三","1","gt-1258");
        PurchaseBean purchaseBean = new PurchaseBean.PurchaseBeanBuilder()
                .setProductDesc("新手装备大礼包")
                .setProductId("itemId_60")
                .setProductName("新手装备大礼包")
                .setProductPrice("1")
                .setOrderId(orderId)
                .setServerId("s1")
                .setRoleId("147258")
                .setRoleName("张三")
                .setRoleLevel("1")
                .setExt("sdfsdfsf")
                .setGameSign(gameSign)
                .build();
        return purchaseBean;
    }

    public String getGameSign(String productId, String productName, String productPrice, String orderId,
                               String serverId, String roleId, String roleName, String rolelevel, String userId) {
        Map<String,String> parasign = new HashMap<>();
        parasign.put("product_id",productId);
        parasign.put("product_name",productName);
        parasign.put("product_price",productPrice);
        parasign.put("game_order_no",orderId);
        return YmSignUtils.getYmSign(parasign,"CE40D7B08558ED0BBD1C653276C91E44");

    }

}
