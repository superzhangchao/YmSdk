package com.ym.ysfj;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.sdk.constants.YmConstants;
import com.ym.game.sdk.YmSdkApi;
import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.callback.LoginCallBack;
import com.ym.game.sdk.callback.PayCallBack;
import com.ym.game.sdk.common.frame.logger.Logger;
import com.ym.game.sdk.common.utils.RSAEncryptUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.common.utils.YmSignUtils;
import com.ym.ysfjen.R;


import java.util.HashMap;
import java.util.Map;

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

    private String text;
    private String key ="vJV8kCxV";
    private String encryptDES;
    private TextView tv;
    private Button btGetrealnameinfo;
    private Button btAuthStatus;
    private Button btGuestRealNane;
    private Button btGuestRealNane2;
    private Button btTestnet1;
    private Button btTestnet2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btInit = (Button) findViewById(R.id.bt_init);
        btLogin = (Button) findViewById(R.id.bt_login);
        btLogout = (Button) findViewById(R.id.bt_logout);
        btPay = (Button) findViewById(R.id.bt_pay);
        btAuthStatus = (Button) findViewById(R.id.bt_status);
        btSendinfo = (Button) findViewById(R.id.bt_sendinfo);
        btGuestRealNane = (Button) findViewById(R.id.bt_guestrealnane);
        btGuestRealNane2 = (Button) findViewById(R.id.bt_guestrealnane2);
        btTestnet1 = (Button) findViewById(R.id.bt_testnet1);
        btTestnet2 = (Button) findViewById(R.id.bt_testnet2);
        tv = (TextView) findViewById(R.id.tv);
        btInit.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        btAuthStatus.setOnClickListener(this);
        btGuestRealNane.setOnClickListener(this);
        btGuestRealNane2.setOnClickListener(this);
        btPay.setOnClickListener(this);
        btSendinfo.setOnClickListener(this);
        btTestnet1.setOnClickListener(this);
        btTestnet2.setOnClickListener(this);
        YmSdkApi.getInstance().onCreate(this,savedInstanceState);
    }









    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.this ==null){
            Logger.i("context 为空");
        }
        YmSdkApi.getInstance().onResume(MainActivity.this);
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
        YmSdkApi.getInstance().onActivityResult(this,requestCode,resultCode,data);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            setFullScreen();

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==btInit.getId()){
            YmSdkApi.getInstance().initEventReport(getApplication(),"toutiao",true);
            YmSdkApi.getInstance().setDebugMode(true);


            YmSdkApi.getInstance().registerEvent();
        }else if(btLogin.getId()==v.getId()){
            YmSdkApi.getInstance().login(this, new LoginCallBack() {
                @Override
                public void onCancel() {
                    tv.setText("onCancle");
                }

                @Override
                public void onSuccess(Object o) {
                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) o;

                    Log.i(TAG, "onSuccess: "+resultAccoutBean.getData().toString());
                    tv.setText(resultAccoutBean.getData().toString());
                    YmSdkApi.getInstance().loginEvent(resultAccoutBean.getData().getUid());
                    ToastUtils.showToast(MainActivity.this,resultAccoutBean.getData().toString());
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.i(TAG, "onFailure: ");
                    tv.setText("onFailure");
                }
            });

        }else if (btSendinfo.getId()==v.getId()){
            YmSdkApi.getInstance().trackEvent("event_1");
            try {
                RSAEncryptUtils.encrypt("a123456", YmConstants.publickey);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(btLogout.getId() ==v.getId()) {
            YmSdkApi.getInstance().logout(this);
        }else if(btGuestRealNane.getId() == v.getId()){
//            YmSdkApi.getInstance().showRealName(this, false, new RealNameCallBack() {
//                @Override
//                public void onSuccess(Object o) {
//                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) o;
//                    Log.i(TAG, "showRealName onSuccess: "+resultAccoutBean.getData().getAuthStatus());
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//
//                @Override
//                public void onFailure(int code, String msg) {
//
//                }
//            });
        }else if(btGuestRealNane2.getId() == v.getId()){
//            YmSdkApi.getInstance().showRealName(this, true, new RealNameCallBack() {
//                @Override
//                public void onSuccess(Object o) {
//                    ResultAccoutBean resultAccoutBean = (ResultAccoutBean) o;
//                    Log.i(TAG, "showRealName onSuccess: "+resultAccoutBean.getData().getAuthStatus());
//
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//
//                @Override
//                public void onFailure(int code, String msg) {
//
//                }
//            });
        }else if(btPay.getId()==v.getId()){

YmSdkApi.getInstance().pay(this,getPurchaseBean(), new PayCallBack() {
    @Override
    public void onSuccess(Object o) {
        Log.i(TAG, "onSuccess: ");
        PurchaseBean purchaseBean = (PurchaseBean) o;
        YmSdkApi.getInstance().paySuccessEvent(purchaseBean.getPlatformOrderId(),purchaseBean.getPayType(),"CNY",0.01f);
    }

    @Override
    public void onFailure(int code, String msg) {
        Log.i(TAG, "onFailure: ");
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "onCancel: ");
    }
});
        }else if(btAuthStatus.getId()==v.getId()){
//            int realNameStatus = YmSdkApi.getInstance().getRealNameStatus();
//            ToastUtils.showToast(this,"AuthStatus:"+realNameStatus);
        }else if (btTestnet1.getId()==v.getId()) {
            YmSdkApi.getInstance().testNet();
        }else if (btTestnet2.getId()==v.getId()){
            YmSdkApi.getInstance().testNet2();
        }
    }

    public PurchaseBean getPurchaseBean(){


        String orderId =  System.currentTimeMillis() +"";
        int productPrice = (int) (Double.parseDouble("0.01")*100);
        String gameSign = getGameSign("itemId_60","新手装备大礼包",productPrice+"",orderId,
                "s1","147258","张三","1","gt-1258");
        YmSdkApi.getInstance().createOrder(orderId,"CNY",0.01f);
        PurchaseBean purchaseBean = new PurchaseBean.PurchaseBeanBuilder()
                .setProductDesc("新手装备大礼包")
                .setProductId("itemId_60")
                .setProductName("新手装备大礼包")
                .setProductPrice(productPrice+"")
                .setGameOrderId(orderId)
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

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT > 18) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {

                        @SuppressLint("NewApi") @Override
                        public void onSystemUiVisibilityChange(int visibility) {

                            getWindow()
                                    .getDecorView()
                                    .setSystemUiVisibility(
                                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }

                    });
        }

    }

}
