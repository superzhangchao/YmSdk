package com.ym.game.plugin.facebook.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.frame.logger.Logger;

import java.util.Map;

public class FBShare {
    private volatile static FBShare INSTANCE;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private FBShare(){}

    public static FBShare getInstance(){
        if (INSTANCE==null){
            synchronized (FBShare.class){
                if (INSTANCE==null){
                    INSTANCE = new FBShare();
                }
            }
        }
        return INSTANCE;
    }

    public void share(Context context, Map<String,Object> shareMap, CallBackListener callBackListener){
//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                .build();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog((Activity) context);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                callBackListener.onSuccess(result.getPostId());
                Logger.i("zhangchao fb share is onsuccess");
            }

            @Override
            public void onCancel() {
                callBackListener.onFailure(ErrorCode.CANCEL,"share onCancel");
                Logger.i("zhangchao fb share is onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                callBackListener.onFailure(ErrorCode.FAILURE,"share onError"+error.getMessage());
                Logger.i("zhangchao fb share is onError");
            }
        });

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .setQuote("Connect on a global scale.")
                .build();

        shareDialog.show(content);
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
