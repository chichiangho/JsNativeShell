package com.seeyon.cmp.common.utils.Logger;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.seeyon.cmp.common.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;

public class TextFormatStrategy implements FormatStrategy {
    private static final String SEPARATOR = ", ";
    public static final String M3LoggerFolder = "m3logger";

    private final Date date;
    private final SimpleDateFormat dateFormat;
    private final LogStrategy logStrategy;
    private final String tag;

    private TextFormatStrategy(Builder builder) {
        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, String onceOnlyTag, String message) {
        StringBuilder tag = new StringBuilder(formatTag(onceOnlyTag));

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
//            builder.append(Long.toString(date.getTime()));

        // human-readable date/time
//        builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // level
        builder.append(SEPARATOR);
        builder.append(logLevel(priority));

        // tag
        builder.append(tag);

        // message
        builder.append(SEPARATOR);
        for (int i = 0; i < 11 - tag.length(); i++) {
            builder.append(" ");
        }
        int length = builder.length();
        StringBuilder pre = new StringBuilder();
        for (int i = 0; i <= length; i++) {
            pre.append(" ");
        }

        if (message.contains("\n")) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll("\n", "\n" + pre.toString());
        }
        builder.append(message);

        // new line
        builder.append("\r\n");

        logStrategy.log(priority, tag.toString(), builder.toString());
    }

    private String formatTag(String tag) {
        if (!StringUtils.isEmpty(tag) && !this.tag.equals(tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 1024 * 1024;

        Date date;
        SimpleDateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder dateFormat(SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        public Builder logStrategy(LogStrategy val) {
            logStrategy = val;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public TextFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
            }
            if (logStrategy == null) {
                String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String folder = diskPath + File.separatorChar + M3LoggerFolder;

                HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                ht.start();
                Handler handler = new TextDiskLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
                logStrategy = new TextDiskLogStrategy(handler);
            }
            return new TextFormatStrategy(this);
        }
    }

    private String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "VERBOSE,";
            case DEBUG:
                return "DEBUG,  ";
            case INFO:
                return "INFO,   ";
            case WARN:
                return "WARN,   ";
            case ERROR:
                return "ERROR,  ";
            case ASSERT:
                return "ASSERT, ";
            default:
                return "UNKNOWN,";
        }
    }
}
