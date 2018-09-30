package com.seeyon.cmp.common.utils;

import okhttp3.HttpUrl;

public class ServerCheckUtil {
    private static String reg6 = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:";
    private static String reg4 = "^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";

    public static boolean isIpV4(String ip) {
        if (ip == null)
            return false;
        if (ip.startsWith("http://"))
            ip = ip.substring(7, ip.length());
        else if (ip.startsWith("https://"))
            ip = ip.substring(8, ip.length());
        if (ip.matches(reg4)) {
            return true;
        } else {
            try {
                HttpUrl parsed = HttpUrl.parse("http://" + ip);
                parsed.uri();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static boolean isIpV6(String ip) {
        if (ip == null)
            return false;
        if (ip.startsWith("http://["))
            ip = ip.substring(7, ip.length());
        else if (ip.startsWith("https://["))
            ip = ip.substring(8, ip.length());
        if (ip.startsWith("["))
            ip = ip.substring(1, ip.length() - 1);
        return ip.matches(reg6);
    }

    public static boolean isIp(String ip) {
        return isIpV4(ip) || isIpV6(ip);
    }
}
