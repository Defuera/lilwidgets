package ru.justd.lilwidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;

/**
 * Created by defuera on 09/02/2017.
 */

class Utils {

    @ColorInt
    static int loadColorFromStyle(Context context, int styleRes, int attrTes) {
        int[] attrs = new int[] { attrTes };
        TypedArray ta = context.obtainStyledAttributes(styleRes, attrs);
        int color = ta.getColor(0, -1);
        ta.recycle();
        return color;
    }

    @LayoutRes
    static int loadLayoutFromStyle(Context context, int styleRes, int attrTes) {
        int[] attrs = new int[] { attrTes };
        TypedArray ta = context.obtainStyledAttributes(styleRes, attrs);
        int layout = ta.getResourceId(0, -1);
        ta.recycle();
        return layout;
    }
}
