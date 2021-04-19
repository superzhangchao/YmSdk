package com.ym.game.sdk.constants;

public enum YmLanguageEnum {
    zh_CN("zh_CN", "简体中文"),
    zh_TW("zh_TW", "繁体中文"),
    en_US("en","英语"),
    ja_JP("ja","日语"),
    ko_KR("ko", "韩语"),
    vi_VN("vi", "越南语"),
    th_TH("th_TH","泰语");



    private String ymLanguageCode;
    private String languageName;

    private YmLanguageEnum(String ymLanguageCode, String languageName) {

        this.ymLanguageCode =  ymLanguageCode;
        this.languageName =  languageName;
    }


    public String getYmLanguageCode() {
        return this.ymLanguageCode;
    }

    public String getLanguageName() {
        return this.languageName;
    }
}
