package com.cundong.viewdraghelper.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class UiUtils {

    public static int getStatusHeight() {
        int statusBarHeight = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height",
                        "dimen", "android"));
        return statusBarHeight;
    }

    /**
     * 获得屏幕的宽高
     * @return
     */
    public static int[] getScreenSize(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wmManager = (WindowManager) context.getSystemService("window");
        wmManager.getDefaultDisplay().getMetrics(displayMetrics);

        int[] screens = new int[2];
        screens[0] = displayMetrics.widthPixels;
        screens[1] = displayMetrics.heightPixels;
        return screens;
    }


}