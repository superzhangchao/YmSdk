package com.ym.game.sdk.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.ym.game.sdk.R;
import com.ym.game.utils.ResourseIdUtils;

import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShowXieyiFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private SubsamplingScaleImageView ymImLarge;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_xieyi"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        ymImLarge = (SubsamplingScaleImageView) view.findViewById(ResourseIdUtils.getId("ym_im_large"));
        ymImBack.setOnClickListener(this);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ymImLarge.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);


        InputStream is = getResources().openRawResource(ResourseIdUtils.getMipmapId("ym_xieyi"));
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(is, null, opts);
        int width = opts.outWidth;
        float scale = getResources().getDimension(ResourseIdUtils.getDimenId("ym_xieyicontext_width"))/width;
        ymImLarge.setMinScale(scale);
        ymImLarge.setImage(ImageSource.resource(ResourseIdUtils.getMipmapId("ym_xieyi")));
        ymImLarge.setZoomEnabled(false);
        ymImLarge.setScaleAndCenter(scale,new PointF(0,0));
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
