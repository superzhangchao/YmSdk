package com.ym.game.sdk.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ym.game.net.api.YmApi;
import com.ym.game.net.bean.ResultOrderBean;
import com.ym.game.net.bean.ResultVcodeBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.sdk.YmConstants;
import com.ym.game.sdk.callback.listener.GetVerifyDataListener;
import com.ym.game.sdk.callback.listener.SendVcodeListener;
import com.ym.game.utils.YmSignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserModel implements IUserModel{
    private static UserModel instance;
    private boolean needBind = false;
    private GetVerifyDataListener mGetVerifyDataListener;

    private static final int SENDTIME = 1;
    private static final int SENDTIMEERROR = 2;
    private static final int SENDTIMENETERROR = 3;
    private static final int GETTOKENSUCCESS = 4;
    private static final int GETTOKENFAIL = 5;
    private static final int GETTOKENNETFAIL = 6;
    private static final int SENDVCODESUCCESS = 7;
    private static final int SENDVCODEFAIL = 8;

    private String currentTs;
    private SendVcodeListener mSendVcodeListener;

    public static UserModel getInstance(){
        if (instance == null){
            instance = new UserModel();
        }
        return instance;
    }

    private UserModel(){

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENDTIME:
                    JSONObject timeInfo = (JSONObject) msg.obj;
                    currentTs = timeInfo.optString("ts");
                    getToken();
                    break;
                case GETTOKENSUCCESS:
                    //TODO:
                    String accessToken = (String) msg.obj;
                    mGetVerifyDataListener.onSuccess(currentTs,accessToken);
                    break;
                case SENDVCODESUCCESS:
                    mSendVcodeListener.onSuccess();
                    break;
                case SENDTIMEERROR:
                    //服务器验证失败
                    //TODO:请求ts失败
                    mGetVerifyDataListener.onFail("获取ts失败");
                    break;
                case SENDTIMENETERROR:
                    //TODO:请求ts网络失败
                    mGetVerifyDataListener.onFail("获取ts网络失败");
                    break;
                case GETTOKENFAIL:
                    //TODO:请求token失败
                    mGetVerifyDataListener.onFail("获取token失败");
                    break;
                case GETTOKENNETFAIL:
                    //TODO:网络请求token失败
                    mGetVerifyDataListener.onFail("获取token网络失败");
                    break;
                case SENDVCODEFAIL:
                    mSendVcodeListener.onFail("发送短信验证吗失败");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void saveToken(Context context, String token) {

    }

    @Override
    public String getToken(Context context) {
        return null;
    }

    @Override
    public void getVerifyData(GetVerifyDataListener getVerifyDataListener) {
        mGetVerifyDataListener = getVerifyDataListener;
        getTime();

    }

    @Override
    public void sendVcode(Context context, String phone,String ts,String accessToken, SendVcodeListener sendVcodeListener) {
        mSendVcodeListener = sendVcodeListener;

        final Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, ts);
        param.put(YmConstants.NUMBER, phone);
        param.put(YmConstants.ACCESSTOKEN, accessToken);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        param.put("sign", sign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResultVcodeBean> token = YmApi.getInstance().getVcode(param);
                token.enqueue(new Callback<ResultVcodeBean>() {
                    @Override
                    public void onResponse(Call<ResultVcodeBean> call, Response<ResultVcodeBean> response) {
                        ResultVcodeBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE){
                            message.what = SENDVCODESUCCESS;
                        }else {
                            message.what = SENDVCODEFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<ResultVcodeBean> call, Throwable t) {
                        Message message = new Message();
                        message.what = SENDVCODEFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private  void getTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<String> token = YmApi.getInstance().getTime();
                token.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String body = response.body();
                        JSONObject timeInfo = new JSONObject();
                        Message message = new Message();
                        try {
                            timeInfo.put("ts",body);
                            message.obj= timeInfo;
                            message.what = SENDTIME;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            message.what = SENDTIMEERROR;
                        }

                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Message message = new Message();
                        message.what = SENDTIMENETERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    private void getToken() {

        Map<String, String> param = new HashMap<>();
        //app_id=**&from=client'
        param.put(YmConstants.APPIDKEY, YmConstants.APPID);
        param.put(YmConstants.TS, currentTs);
        param.put(YmConstants.FROMKEY, YmConstants.FROM);
        final String sign = YmSignUtils.getYmSign(param, YmConstants.CLIENTSECRET);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<TokenBean> token = YmApi.getInstance().getTokenInfo(YmConstants.APPID, YmConstants.FROM, currentTs, sign);
                token.enqueue(new Callback<TokenBean>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenBean> call, @NonNull Response<TokenBean> response) {
                        TokenBean body = response.body();
                        int errorCode = body.getCode();
                        Message message = new Message();
                        if (errorCode == YmConstants.SUCCESSCODE) {
                            message.obj = body.getData().getAccessToken();
                            message.what = GETTOKENSUCCESS;

                        }else {
                            message.what = GETTOKENFAIL;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenBean> call, @NonNull Throwable t) {
                        Message message = new Message();

                        message.what = GETTOKENNETFAIL;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }

//



//    public boolean userNeedBind() {
//        return needBind;
//    }
//
//
//
//    public boolean isLogin(){
////        if (userBean == null){
////            return false;
////        }
//
////        if (TextUtils.isEmpty(userBean.getSid())){
////            return false;
////        }
////
////        if (TextUtils.isEmpty(userBean.getUid())){
////            return false;
////        }
//
//        return true;
//    }
//
//    public void logout(){
////        userBean = null;
//    }
//
//    @Override
//    public void saveToken(Context context, String token) {
//
//    }
//
//    @Override
//    public String getToken(Context context) {
//        return null;
//    }
//    /**
//     * 获取短信验证码
//     * @param context
//     * @param phone
//     * @param userListener
//     */
//    @Override
//    public void sendVcode(Context context, String phone, final User.UserListener userListener) {
//
//
//    }

//    /**
//     * 修改帐号昵称
//     * @param context
//     * @param nickName
//     * @param userListener
//     */
//    @Override
//    public void updateNickName(final Context context, String nickName, final User.UserListener userListener) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("action","user.change_name");
//        params.put("sid",userBean.getSid());
//        params.put("nickname",nickName);
//        params.put("device_no",Config.getDeviceNo(context));
//
//    }

//    @Override
//    public void logout(Context context, User.UserListener userListener) {
//
//    }


//    /**
//     * 一键登录
//     * @param context
//     * @param userListener
//     */
//    @Override
//    public void fastLogin(final Context context, final User.UserListener userListener) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("action","user.fastlogin");
//        params.put("device_no",Config.getDeviceNo(context));
//        params.put("promote",Config.getPromote(context));
//        params.put("game_id",Config.getGameId());
////        Log.i("UserModel","login url : " + HttpProxy.getUrlWithQueryString(Config.HOST,params, Request.DEFAULT_PARAMS_ENCODING));
//
//    }

//    /**
//     * 绑定帐号
//     * @param context
//     * @param phone
//     * @param pwd
//     * @param vcode
//     * @param userListener
//     */
//    @Override
//    public void bindPhone(final Context context, String phone, String pwd, String vcode, final User.UserListener userListener) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("action","user.bind");
//        params.put("sid",userBean.getSid());
//        params.put("device_no",Config.getDeviceNo(context));
//        params.put("phone_mob",phone);
//        params.put("password",pwd);
//        params.put("verifycode",vcode);
//
//
//    }

//    @Override
//    public void getThirdUserInfo(final Context context, String url, final User.UserListener userListener) {
////        Log.i("UserModel","getThirdUserInfo : " + url);
//
//    }
//
//    @Override
//    public void parseThirdUserInfo(final Context context, JSONObject response, User.UserListener userListener){
//
//    }
//
//    @Override
//    public void autoLogin(final Context context, final User.UserListener userListener){
//        String token = getToken(context);
//        if (TextUtils.isEmpty(token)){
//            userListener.onFail("token 无效");
//            return;
//        }
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("action","user.login_by_token");
//        params.put("token",token);
//        params.put("device_no",Config.getDeviceNo(context));
//
//
//    }

//    /**
//     * 保存用户登录的手机号
//     * @param context
//     * @param phone
//     */
//    @Override
//    public void saveUserPhone(Context context, String phone){
//        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
//        Set<String> phones = sharedPreferences.getStringSet("phone",new LinkedHashSet<String>());
//        if (phones.contains(phone)){
//            return;
//        }
//
//        phones.add(phone);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putStringSet("phone",phones);
//        editor.commit();
//    }

//    /**
//     * 读取保存的用户手机号
//     * @param context
//     * @return
//     */
//    @Override
//    public Set<String> getAllPhone(Context context){
//        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
//        return sharedPreferences.getStringSet("phone",new LinkedHashSet<String>());
//    }
//
//    @Override
//    public void deleteUserPhone(Context context, String phone) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
//        Set<String> phones = sharedPreferences.getStringSet("phone",new LinkedHashSet<String>());
//        if (phones.contains(phone)){
//            phones.remove(phone);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putStringSet("phone",phones);
//            editor.commit();
//        }
//    }


}
