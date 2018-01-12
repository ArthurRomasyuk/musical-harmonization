package com.devs.musicalharmonization;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 *  3/28/2016.
 */
public class NoteBitmap {
    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmap(Drawable vectorDrawable) {

        Drawable drawable;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(vectorDrawable)).mutate();
        }
        else {
            drawable = vectorDrawable;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;

    }
    public static Bitmap qnh;
    public static Bitmap hnh;
}
