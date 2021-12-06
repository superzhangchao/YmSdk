package com.ym.game.sdk.config;


public class Config {
    private static boolean isInit;
    private static String gameId;
    public final static String RELEASEURL = "https://ysfj-us-sdk.loologames.com";
    public final static String TESTBASEURL = "http://180.167.128.34:19090/";
    public final static String DEVBASEURL = "http://192.168.0.225/";
    private static String lang;

    public static String getGameId() {
        return gameId;
    }

    public static void setGameId(String gameId) {
        Config.gameId = gameId;
        Config.isInit = true;
    }

    public static void setLanguage(String lang){
        Config.lang = lang;

    }

    public static String getLanguage(){
        return lang;
    }

    public static boolean isInitPlatform(){
        return isInit;
    }


//    /**
//     * 渠道id
//     * @return
//     */
//    public static String getPromote(Context context){
//        String channel = "0";
//        try {
//            channel = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("gm_channel","0");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        if (channel == null){
//            channel = "0";
//        }
//        return channel;
//    }

}
