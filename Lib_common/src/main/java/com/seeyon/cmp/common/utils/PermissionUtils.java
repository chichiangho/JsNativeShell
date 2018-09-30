package com.seeyon.cmp.common.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.seeyon.cmp.common.base.BaseApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 跳转权限设置界面工具类
 * Created by vagrant on 2018/3/13.
 */

public class PermissionUtils {
    private static final String TOAST_HINT = "无法跳转至权限设置页面，请手动设置";

    /**
     * 检测悬浮窗权限
     *
     * @return
     */
    public static boolean checkFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(BaseApplication.getInstance());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //AppOpsManager添加于API 19
            return checkOps();
        } else {
            //4.4以下一般都可以直接添加悬浮窗
            return true;
        }
    }

    /**
     * 跳转到设置开启悬浮窗页面
     *
     * @param context
     */
    public static void tryJumpToPermissonPage(Activity context, String tip) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            switch (RomUtils.getRomName()) {
                case RomUtils.ROM_MIUI:
                    applyMiuiPermission(context, tip);
                    break;
                case RomUtils.ROM_EMUI:
                    applyHuaweiPermission(context, tip);
                    break;
                case RomUtils.ROM_VIVO:
                    applyVivoPermission(context, tip);
                    break;
                case RomUtils.ROM_OPPO:
                    applyOppoPermission(context, tip);
                    break;
                case RomUtils.ROM_QIKU:
                    apply360Permission(context, tip);
                    break;
                case RomUtils.ROM_SMARTISAN:
                    applySmartisanPermission(context, tip);
                    break;
                case RomUtils.ROM_COOLPAD:
                    applyCoolpadPermission(context, tip);
                    break;
                case RomUtils.ROM_ZTE:
                    applyZTEPermission(context, tip);
                    break;
                case RomUtils.ROM_LENOVO:
                    applyLenovoPermission(context, tip);
                    break;
                case RomUtils.ROM_LETV:
                    applyLetvPermission(context, tip);
                    break;
                case RomUtils.ROM_FLYME:
                    applyMeizuPermission(context, tip);
                    break;
                default:
                    Toast.makeText(context, TOAST_HINT, Toast.LENGTH_LONG).show();
            }
        } else {
            if (RomUtils.isMeizuRom()) {
                applyMeizuPermission(context, tip);
            } else {
                applyCommonPermission(context, tip);
            }
        }
    }

    /**
     * 应用设置界面
     *
     * @param context
     */
    public static void appSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean startActivitySafely(Intent intent, final Activity context, String tip) {
        if (isIntentAvailable(intent, context)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
                showAlertToast(context, tip);
                return true;
            } catch (Exception e) {//部分手机这里会crash，如魅族mx4申请悬浮窗权限
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        return intent != null && context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    private static void showAlertToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    private static void applyCommonPermission(Activity context, String tip) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            startActivitySafely(intent, context, tip);
        } catch (Exception e) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyCoolpadPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setClassName("com.yulong.android.seccenter", "com.yulong.android.seccenter.dataprotection.ui.AppListActivity");
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyLenovoPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setClassName("com.lenovo.safecenter", "com.lenovo.safecenter.MainTab.LeSafeMainActivity");
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyZTEPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setAction("com.zte.heartyservice.intent.action.startActivity.PERMISSION_SCANNER");
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyLetvPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setClassName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AppActivity");
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyVivoPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyOppoPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setAction("com.oppo.safe");
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (!startActivitySafely(intent, context, tip)) {
            intent.setAction("com.color.safecenter");
            intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
            if (!startActivitySafely(intent, context, tip)) {
                intent.setAction("com.coloros.safecenter");
                intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
                if (!startActivitySafely(intent, context, tip)) {
                    showAlertToast(context, TOAST_HINT);
                }
            }
        }
    }

    private static void apply360Permission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        if (!startActivitySafely(intent, context, tip)) {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (!startActivitySafely(intent, context, tip)) {
                showAlertToast(context, TOAST_HINT);
            }
        }
    }

    private static void applyMiuiPermission(Activity context, String tip) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applyMeizuPermission(Activity context, String tip) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", context.getPackageName());
        if (!startActivitySafely(intent, context, tip)) {
            showAlertToast(context, tip);
        }
    }

    private static void applyHuaweiPermission(Activity context, String tip) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            intent.setComponent(comp);
            if (!startActivitySafely(intent, context, tip)) {
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                intent.setComponent(comp);
                context.startActivity(intent);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            showAlertToast(context, TOAST_HINT);
        }
    }

    private static void applySmartisanPermission(Activity context, String tip) {
        Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW");
        intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
        intent.putExtra("index", 17); //有版本差异,不一定定位正确
        if (!startActivitySafely(intent, context, tip)) {
            intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS");
            intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("permission", new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});
            if (!startActivitySafely(intent, context, tip)) {
                showAlertToast(context, TOAST_HINT);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean checkOps() {
        try {
            Object object = BaseApplication.getInstance().getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = BaseApplication.getInstance().getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            //4.4至6.0之间的非国产手机，例如samsung，sony一般都可以直接添加悬浮窗
            return m == AppOpsManager.MODE_ALLOWED || !RomUtils.isDomesticSpecialRom();
        } catch (Exception ignore) {
        }
        return false;
    }
}
