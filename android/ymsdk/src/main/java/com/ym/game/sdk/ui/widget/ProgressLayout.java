package com.ym.game.sdk.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ym.game.sdk.common.utils.ResourseIdUtils;

import androidx.annotation.Nullable;

public class ProgressLayout extends LinearLayout {

    private Context mContext;
    private View mView;
    private ImageView bird;
    private ImageView grid;
    private ImageView pot;
    private AnimationDrawable animationDrawable;
    private AnimationDrawable potDrawable;
    private int mix;
    private int max;

    public ProgressLayout(Context context) {
        this(context,null);
    }

    public ProgressLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);


    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(ResourseIdUtils.getLayoutId("ym_layout_loading"), this, true);
        bird = (ImageView) mView.findViewById(ResourseIdUtils.getId("bg_im_bird"));
        grid = (ImageView) mView.findViewById(ResourseIdUtils.getId("bg_im_grid"));
        pot = (ImageView) mView.findViewById(ResourseIdUtils.getId("im_pot"));

        performAnim();

    }

    public ProgressLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    public void show(){
        mView.setVisibility(VISIBLE);

    }

    public void dismiss(){
        mView.setVisibility(GONE);
    }

    public void performAnim(){
        bird.setImageResource(ResourseIdUtils.getDrawableId("ym_bird_anim"));
        pot.setImageResource(ResourseIdUtils.getDrawableId("ym_pot_anim"));
        //41 -258

        animationDrawable = (AnimationDrawable) bird.getDrawable();
        animationDrawable.start();
        potDrawable = (AnimationDrawable) pot.getDrawable();
        potDrawable.start();

        final ViewGroup.LayoutParams layoutParams = grid.getLayoutParams();
        //属性动画对象
        ValueAnimator va ;
        mix = dip2px(mContext, 41);
        max = dip2px(mContext, 248);
//        Log.i(TAG, "performAnim: "+mix +"  "+max);


        va = ValueAnimator.ofInt(mix, max,mix);
        va.setDuration(1000);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setInterpolator(new AccelerateDecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int w = (int) valueAnimator.getAnimatedValue();
                layoutParams.width = w;
                grid.setLayoutParams(layoutParams);
                grid.requestLayout();

            }
        });
        va.start();



    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
