package com.ym.game.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageUtils {



    public static Bitmap rotateIm(Context context, int image){
        Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), image);// 得到图片资源
        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();
        Matrix matrix = new Matrix();
// 设置旋转角度
        matrix.setRotate(180); //degree 旋转度数
    // 重新绘制Bitmap
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bw, bh, matrix, true);
        return newBitmap;
    }
}
