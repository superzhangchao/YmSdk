package com.ym.game.sdk.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.ym.game.utils.ResourseIdUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShowXieyiFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private WebView ymWbXieyi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_xieyi"), null, true);
//        ym_wb_xieyi
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymWbXieyi = (WebView) view.findViewById(ResourseIdUtils.getId("ym_wb_xieyi"));
        ymImBack.setOnClickListener(this);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = "file:///android_res/mipmap/ym_xieyi2.png";
        WebSettings settings = ymWbXieyi.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        ymWbXieyi.loadUrl(url);


    }

    @Override
    public void onClick(View view) {
        back();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}
