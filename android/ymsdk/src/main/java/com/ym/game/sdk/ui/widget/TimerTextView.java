package com.ym.game.sdk.ui.widget;

import android.content.Context;

import android.util.AttributeSet;




import com.ym.game.sdk.common.utils.ResourseIdUtils;




public class TimerTextView extends androidx.appcompat.widget.AppCompatTextView implements Runnable {

    // 当前计时器是否运行
    private boolean isRun = false;
    private int time;
    private int currentTime;

    private String contentFrontText;
    private String contentBackText;
    private String defaultText;

    public TimerTextView(Context context) {
        super(context);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 将倒计时时间毫秒数转换为自身变量
     *
     * @param time
     *            时间间隔秒数
     */
    public void setTimesandText(String defaultText,String contentFrontText,String contentBackText,int time) {
        this.defaultText = defaultText;
        this.contentFrontText=contentFrontText;
        this.contentBackText=contentBackText;
        this.time = time;
        this.currentTime =time;
    }

    /**
     * 显示当前时间
     *
     * @return
     */
    public String showTime() {
        return contentFrontText +
                currentTime +
                contentBackText;
    }

    /**
     * 实现倒计时
     */
    private void countdown() {
        if(currentTime==0){
            isRun = false;
        }else {
            currentTime--;

        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
//        NORMAL
        super.setText(text, type);
    }
//    public void setText(CharSequence text,int time){
//        super.setText();
//    }
    public boolean isRun() {
        return isRun;
    }

    /**
     * 开始计时
     */
    public void start() {
        isRun = true;
        run();
    }

    /**
     * 结束计时
     */
    public void stop() {
        isRun = false;
    }

    @Override
    public void run() {
        if (isRun) {
            this.setTextColor(getResources().getColor(ResourseIdUtils.getColorId("ym_edit_name")));
            this.setClickable(false);
            countdown();
            this.setText(showTime());
            postDelayed(this, 1000);
        } else {
            currentTime = time;
            this.setText(this.defaultText);
            this.setTextColor(getResources().getColor(ResourseIdUtils.getColorId("ym_phonecode")));
            this.setClickable(true);
            removeCallbacks(this);
        }
    }
}
