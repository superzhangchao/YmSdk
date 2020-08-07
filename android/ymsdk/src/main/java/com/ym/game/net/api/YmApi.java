package com.ym.game.net.api;

import android.text.TextUtils;

import com.ym.game.net.bean.ResultOrderBean;
import com.ym.game.net.bean.ResultVcodeBean;
import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.sdk.YmConstants;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YmApi {
    private static YmApi instance;
    private static String baseUrl;
    private YmApiService service;

    private YmApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = YmConstants.BASEURL;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(YmApiService.class);

    }

    public static void setBaseUrl(String url){
        baseUrl = url;
    }

    public static YmApi getInstance(){
        if(instance == null){
            instance = new YmApi();
        }
        return instance;
    }

    public Call<TokenBean> getTokenInfo(String appId,String from,String ts,String sign){
        return  service.getTokenInfo(appId,from,ts,sign);
    }
    public Call<String> getTime(){
        return service.getTime();
    }

    public Call<ResultVcodeBean> getVcode(Map<String, String> vcodeReq){
        return service.getVcode(vcodeReq);
    }
    public Call<ResultVcodeBean> checkBind(Map<String, String> vcodeReq){
        return service.checkBind(vcodeReq);
    }

    public Call<ResultAccoutBean>  getPhoneAccoutInfo(Map<String, String> accountReq){
        return service.getPhoneAccoutInfo(accountReq);
    }
    public Call<ResultAccoutBean>  getWeixinAccoutInfo(Map<String, String> accountReq){
        return service.getWeixinAccoutInfo(accountReq);
    }
    public Call<ResultAccoutBean>  getQQAccoutInfo(Map<String, String> accountReq){
        return service.getQQAccoutInfo(accountReq);
    }
    public Call<ResultAccoutBean>  getGuestAccoutInfo(Map<String, String> accountReq){
        return service.getGuestAccoutInfo(accountReq);
    }

    public Call<ResultAccoutBean> quickLogin(Map<String, String> accountReq){
        return service.quickLogin(accountReq);
    }

    public Call<ResultOrderBean> checkorder(Map<String, String> orderReq){
        return service.checkorder(orderReq);
    }
    public Call<ResultAccoutBean> bindAccoutInfo(Map<String,String> accountReq){
        return service.bindAccoutInfo(accountReq);
    }

    public Call<ResultAccoutBean> realName(Map<String, String> accountReq){
        return service.realName(accountReq);
    }

}
