package com.ym.game.sdk.common.base.net;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * 用于获取配置好的retrofit对象
 * 需要先调用setBaseUrl，如果项目中BaseUrl不变，可以写死
 */
public class RetrofitFactory {

    private static Retrofit retrofit;
    private static String baseUrl;

    public static void setBaseUrl(String url) {
        baseUrl = url;
    }

    /**
     * 获取配置好的retrofit对象来生产Manager对象
     */
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            if (baseUrl == null || baseUrl.length() <= 0)
                throw new IllegalStateException("请在调用getFactory之前先调用setBaseUrl");

            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(baseUrl)
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 参考RxJava
                    .addConverterFactory(GsonConverterFactory.create()); // 参考与GSON的结合

            // 参考自定义Log输出
            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .addInterceptor(new Interceptor() {     //这个拦截器是操作请求头的
//                        @Override
//                        public Response intercept(Chain chain) throws IOException {
//                            Request request = chain.request().newBuilder()
//                                    .addHeader("version", "123411") //这里就是添加一个请求头
//                                    .build();
//
//                            return chain.proceed(request);
//                        }
//                    })
//                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))       //这个拦截器是用来打印日志的，不稳定
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
            builder.client(client);
            retrofit = builder.build();
        }
        return retrofit;
    }
}
