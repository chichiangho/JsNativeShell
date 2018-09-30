package com.seeyon.cmp.common.utils;

/**
 * Created by vagrant on 2018/2/7.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.seeyon.cmp.common.base.BaseApplication;

import java.io.File;

/**
 * Android N 适配工具类
 */
public class FileProviderUtil {

    private static String authority = "";

    public static void setAuthority(String applicationId) {
        FileProviderUtil.authority = applicationId + ".FileProvider";
    }

    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            if (authority.equals("")) {
                Log.e(FileProviderUtil.class.getName(), "请先设置Authority");
                return null;
            }
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    private static Uri getUriForFile24(Context context, File file) {
        //部分华为手机需要使用此方法适配，否则无法打开第三方软件
        return ContentUriProvider.getUriForFile(context, authority, file);
    }

    public static void setIntentDataAndType(Context context, Intent intent, String type, File file, boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
