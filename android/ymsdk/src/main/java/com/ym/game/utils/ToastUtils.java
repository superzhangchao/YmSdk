package com.ym.game.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by bzai on 2018/5/24.
 * <p>
 * Desc:
 *
 *  Toast 公共类
 */

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
