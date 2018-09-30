package com.seeyon.cmp.common.utils;

import android.content.Context;
import android.content.Intent;

import java.io.File;

public class IntentUtil {
    /**
     * android获取一个用于打开HTML文件的intent
     */
    public static Intent getHtmlFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        FileProviderUtil.setIntentDataAndType(context, intent, "text/html", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开图片文件的intent
     */
    public static Intent getImageFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "image/*", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开PDF文件的intent
     */
    public static Intent getPdfFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "application/pdf", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开文本文件的intent
     */
    public static Intent getTextFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "text/plain", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开音频文件的intent
     */
    public static Intent getAudioFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        FileProviderUtil.setIntentDataAndType(context, intent, "audio/*", new File(param), true);
        return intent;
    }


    /**
     * android获取一个用于打开视频文件的intent
     */
    public static Intent getVideoFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        FileProviderUtil.setIntentDataAndType(context, intent, "video/*", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开CHM文件的intent
     */
    public static Intent getChmFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "application/x-chm", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开Word文件的intent
     */
    public static Intent getWordFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "application/msword", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开Excel文件的intent
     */
    public static Intent getExcelFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "application/vnd.ms-excel", new File(param), true);
        return intent;
    }

    /**
     * android获取一个用于打开PPT文件的intent
     */
    public static Intent getPptFileIntent(Context context, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProviderUtil.setIntentDataAndType(context, intent, "application/vnd.ms-powerpoint", new File(param), true);
        return intent;
    }
}
