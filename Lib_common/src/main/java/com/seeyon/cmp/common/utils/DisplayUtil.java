package com.seeyon.cmp.common.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.seeyon.cmp.common.base.BaseApplication;

public class DisplayUtil {
    private static int statusBarHeight = 0;
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            int resourceId = BaseApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = BaseApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    public static int dip2px(float dpValue) {
        final float scale = BaseApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getScreenWidth(Activity activity) {
        if (screenWidth == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        }
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return screenWidth;
        else
            return screenHeight;
    }

    public static int getScreenHeight(Activity activity) {
        if (screenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        }
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)//兼容横竖屏
            return screenHeight;
        else
            return screenWidth;
    }
}
