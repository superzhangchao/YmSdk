package com.ym.game.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showToast(Context context,
                                 String content) {

        if (content == null){
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
