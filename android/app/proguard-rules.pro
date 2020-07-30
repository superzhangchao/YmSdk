# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#eventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#eventBus end

#qq
-keep class * extends android.app.Dialog
#qq end

#微信
-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
    *;
}
#微信end

#支付宝

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
#支付宝 end

#gson
-keep class com.google.** {
    <fields>;
    <methods>;
}


-keepclassmembers class * extends java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Gson specific classes
-keep class sun.misc.Unsafe {
    <fields>;
    <methods>;
}

#gson end

# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# okhttp
-dontwarn okio.**

# Retrofit end

#自定义bean不能被混淆
-keep class com.ym.game.net.bean.**{*;}
-keep class com.ym.game.sdk.bean.**{*;}
#自定义bean不能被混淆

#游戏自定义框架
# 框架的 Native 方法
-keep class org.cocos2dx.lib.Cocos2dxBitmap { static <methods>; }
-keep class org.cocos2dx.lib.Cocos2dxAccelerometer {
    void onSensorChanged(float, float, float, long);
}
-keep class org.cocos2dx.lib.Cocos2dxETCLoader {
    void nativeSetTextureInfo( int, int,  byte[], int);
}
-keep class org.cocos2dx.lib.Cocos2dxHelper { static <methods>; }
-keep class org.cocos2dx.lib.Cocos2dxVideoHelper { static <methods>; }
-keep class org.cocos2dx.lib.Cocos2dxLuaJavaBridge { *; }
-keep class org.cocos2dx.lib.Cocos2dxRenderer { static <methods>; }
-keep class org.cocos2dx.lib.Cocos2dxGLSurfaceView { static <methods>; }
-keep class org.cocos2dx.lib.Cocos2dxLocalStorage { static <methods>; }

# 应用某些被 Native 反射调用的方法不能被混淆
-keep class org.cocos2dx.lua.AppActivity { static <methods>; }
-keep class com.extra.utils.CheckMD5 { static <methods>; }
-keep class com.extra.utils.JNIUtils { static <methods>; }

#游戏自定义框架 end