package com.ym.game.sdk.event;

public class WeChatLoginEvent extends BaseEvent {
    private int mErrorCode;
    private String mCode;

    public WeChatLoginEvent(int errorCode,String code) {
        mErrorCode = errorCode;
        mCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }
}
