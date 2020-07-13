package com.ym.game.sdk.base.interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by bzai on 2020/03/19.
 * 生命周期接口
 */

public interface LifeCycleInterface {

    void onCreate(Context context, Bundle savedInstanceState);
    void onResume(Context context);
    void onStart(Context context);
    void onPuase(Context context);
    void onStop(Context context);
    void onRestart(Context context);
    void onDestroy(Context context);
    void onNewIntent(Context context, Intent intent);
    void onConfigurationChanged(Context context, Configuration newConfig);
    void onActivityResult(Context context, int requestCode, int resultCode, @Nullable Intent data);
    void onRequestPermissionsResult(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
