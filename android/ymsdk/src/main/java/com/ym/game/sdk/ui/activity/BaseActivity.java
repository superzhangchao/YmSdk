package com.ym.game.sdk.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
        setFullScreen();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            setFullScreen();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ymRlLypg.getVisibility()==View.VISIBLE){
            dismissLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
