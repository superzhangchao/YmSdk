package com.ym.game.sdk.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.ym.game.sdk.common.utils.ResourseIdUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShowXieyiFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private SubsamplingScaleImageView ymImLarge;
    private String xieyiImage;

    public void setXieyiImage(String xieyiImage){
        this.xieyiImage = xieyiImage;
    }
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
        ymImLarge.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), ResourseIdUtils.getMipmapId(xieyiImage), opts);
        int width = opts.outWidth;

        float scale = getResources().getDimension(ResourseIdUtils.getDimenId("ym_xieyicontext_width"))/width;
        ymImLarge.setMinScale(scale);

        ymImLarge.setImage(ImageSource.asset(xieyiImage+".png"));
        ymImLarge.setZoomEnabled(false);

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
