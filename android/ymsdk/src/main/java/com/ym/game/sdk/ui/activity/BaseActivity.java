package com.ym.game.sdk.ui.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



import com.ym.game.sdk.ui.fragment.BackHandlerHelper;

import com.ym.game.utils.ResourseIdUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private ImageView loadingView;
    private TextView loadingMsgView;

    private View ymRlLypg;
    private AnimationDrawable animationDrawable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourseIdUtils.getLayoutId("activity_fragment"));
        ymRlLypg = findViewById(ResourseIdUtils.getId("ym_rl_lypg"));
        loadingView = (ImageView)findViewById(ResourseIdUtils.getId("bg_im_bird"));
        loadingView.setImageResource(ResourseIdUtils.getDrawableId("ym_bird_anim"));

        animationDrawable = (AnimationDrawable) loadingView.getDrawable();
        animationDrawable.start();
        fragmentManager = getSupportFragmentManager();


    }

    protected void initFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(ResourseIdUtils.getId("content"),fragment);
        transaction.commitAllowingStateLoss();



    }



    public void showLoading() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ymRlLypg.setVisibility(View.VISIBLE);


            }
        });



    }

    public void dismissLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ymRlLypg.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

}
