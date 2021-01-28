package com.ym.game.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.InputStream;

public class ImageUtils {



    public static Bitmap rotateIm(Context context, int image){


        InputStream is = context.getResources().openRawResource(image);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap mBitmap = BitmapFactory.decodeStream(is, null, opts);


        Matrix matrix = new Matrix();
        // 设置旋转角度
        matrix.setRotate(180); //degree 旋转度数
    // 重新绘制Bitmap

        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

    }
}
