package com.ym.game.sdk.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import com.ym.game.utils.ResourseIdUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private View loadingView;
    private TextView loadingMsgView;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourseIdUtils.getLayoutId("activity_fragment"));
        fragmentManager = getSupportFragmentManager();

        loadingView = findViewById(ResourseIdUtils.getId("loaing"));
        loadingMsgView = (TextView) findViewById(ResourseIdUtils.getId("loading_msg"));

    }

    protected void initFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(ResourseIdUtils.getId("content"),fragment);
//        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();



    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 0){
            finish();
        }else{
            fragmentManager.popBackStack();
        }
    }

    public void showLoading(final String msg) {
        if(dialog == null){
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        dialog.setTitle(msg);
        if (!dialog.isShowing()){
            dialog.show();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.VISIBLE);
                loadingMsgView.setText(msg);
            }
        });

    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.GONE);
            }
        });

    }
}
