package com.ym.game.net.api;

import com.ym.game.net.bean.TokenBean;
import com.ym.game.net.bean.ResultAccoutBean;
import com.ym.game.net.bean.ResultOrderBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface YmApiService {

    @GET("Init")
    Call<String> getInitInfo(@QueryMap Map<String,String> initReq);

    @GET("token")
    Call<TokenBean>  getAccessToken(@QueryMap Map<String, String> tokenReq);
    @GET("time")
    Call<String> getTime();


    @GET("user/login/facebook")
    Call<ResultAccoutBean> getFBAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/google")
    Call<ResultAccoutBean> getGoogleAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/guest")
    Call<ResultAccoutBean> getGuestAccoutInfo(@QueryMap Map<String, String> accountReq);

    @GET("user/login/quick")
    Call<ResultAccoutBean> quickLogin(@QueryMap Map<String, String> accountReq);

    @GET("user/bind/facebook")
    Call<ResultAccoutBean> bindFbAccount(@QueryMap Map<String, String> accountReq);

    @GET("user/bind/google")
    Call<ResultAccoutBean> bindGoolgeAccount(@QueryMap Map<String, String> accountReq);

    @GET("pay")
    Call<ResultOrderBean> createOrder(@QueryMap Map<String, String> orderReq);

    @POST("pay/notify/google")
    Call<ResultOrderBean> verifyGoogleOrder(@QueryMap Map<String, String> orderReq);
}
