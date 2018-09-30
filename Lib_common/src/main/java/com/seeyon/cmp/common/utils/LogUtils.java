package com.seeyon.cmp.common.utils;

import android.os.Environment;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.seeyon.cmp.common.BuildConfig;
import com.seeyon.cmp.common.base.BaseApplication;
import com.seeyon.cmp.common.utils.Logger.TextFormatStrategy;

import java.io.File;

import kotlin.jvm.Throws;

/**
 * Created by vagrant on 2016/12/9.
 */

public class LogUtils {

    private static int writeToFileLevel = Log.ERROR + 2;
    private static final boolean isPrettyLog = false;//是否使用美化的log

    private static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static void init() {
        try {
            //Logger第三方工具的日志目录
            File logger = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + TextFormatStrategy.M3LoggerFolder);
            long time = 1000 * 60 * 60 * 24 * 2;//2天前的文件删除
            for (File f : logger.listFiles()) {
                if (f.isFile() && System.currentTimeMillis() - f.lastModified() > time) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            w(e.toString());
        }
        writeToFileLevel = BaseApplication.getInstance().getSharedPreferences().getInt("writeToFileLevel", Log.ERROR + 2);
        if (isPrettyLog)
            Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder().tag("M3").build()));
        else
            Logger.addLogAdapter(new LogAdapter() {
                @Override
                public boolean isLoggable(int priority, String tag) {
                    return isDebug() || priority >= writeToFileLevel;
                }

                @Override
                public void log(int priority, String tag, String message) {
                    new LogcatLogStrategy().log(priority, tag, message);
                }
            });
        Logger.addLogAdapter(new MyDiskLogAdapter(TextFormatStrategy.newBuilder().tag("M3").build()));
        d("\n----------------M3 restart-----------------\n");
    }

    public static void init(String TAG) {
        Logger.t(TAG);
    }

    public static void i(String tag, String message) {
        init(tag);
        i(message);
    }

    public static void i(String message, Object... args) {
        if (isDebug() || writeToFileLevel <= Log.INFO) {
            Logger.i(message, args);
        }
    }

    public static void e(String tag, String message) {
        init(tag);
        e(message);
    }

    public static void e(String message, Object... args) {
        Logger.e(message, args);
    }

    public static void d(String tag, String message) {
        init(tag);
        d(message);
    }

    public static void d(String message, Object... args) {
        if (isDebug() || writeToFileLevel <= Log.DEBUG) {
            Logger.d(message, args);
        }
    }

    public static void v(String tag, String message) {
        init(tag);
        v(message);
    }

    public static void v(String message, Object... args) {
        if (isDebug() || writeToFileLevel <= Log.VERBOSE) {
            Logger.v(message, args);
        }
    }

    public static void w(String tag, String message) {
        init(tag);
        w(message);
    }

    public static void w(String message, Object... args) {
        if (isDebug() || writeToFileLevel <= Log.WARN) {
            Logger.w(message, args);
        }
    }

    public static void json(String tag, String message) {
        init(tag);
        json(message);
    }

    public static void json(String message, Object... args) {
        if (message.contains("\n")) {
            d(message);
        } else {
            StringBuilder jsonForMatStr = new StringBuilder();
            int level = 0;
            for (int index = 0; index < message.length(); index++) {//将字符串中的字符逐个按行输出
                //获取s中的每个字符
                char c = message.charAt(index);
                //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
                if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                    jsonForMatStr.append(getLevelStr(level));
                }
                //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
                switch (c) {
                    case '{':
                    case '[':
                        jsonForMatStr.append(c).append("\n");
                        level++;
                        break;
                    case ',':
                        jsonForMatStr.append(c).append("\n");
                        break;
                    case '}':
                    case ']':
                        jsonForMatStr.append("\n");
                        level--;
                        jsonForMatStr.append(getLevelStr(level));
                        jsonForMatStr.append(c);
                        break;
                    default:
                        jsonForMatStr.append(c);
                        break;
                }
            }
            d(jsonForMatStr.toString());
        }
    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("  ");
        }
        return levelStr.toString();
    }

    public static void setWriteToFileLevel(int level) {
        if (level >= Log.VERBOSE) {
            writeToFileLevel = level;
            BaseApplication.getInstance().getSharedPreferences().edit().putInt("writeToFileLevel", writeToFileLevel).apply();
            BaseApplication.getInstance().getSharedPreferences().edit().putLong("writeToFileStartTime", System.currentTimeMillis()).apply();
        }
    }

    public static long getLastLogToFileStartTime() {
        return BaseApplication.getInstance().getSharedPreferences().getLong("writeToFileStartTime", 0);
    }

    private static class MyDiskLogAdapter extends DiskLogAdapter {
        public MyDiskLogAdapter(FormatStrategy build) {
            super(build);
        }

        @Override
        public boolean isLoggable(int priority, String tag) {
            return priority >= writeToFileLevel;
        }
    }
}
