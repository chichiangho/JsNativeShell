package com.seeyon.cmp.common.utils.network.entity;

/**
 * Created by wangxk on 2018-3-19.
 */

public class CMPNetinfo {
    // 设备网络状态
    public static final String C_sConnectionStatus_None = "none";
    /**
     * 流量模式
     */
    public static final String C_sConnectionStatus_Cellular = "cellular";
    /**
     * wifi 默认
     */
    public static final String C_sConnectionStatus_Wifi = "wifi";
    /**
     * 未知
     */
    public static final String C_sConnectionStatus_Unknown = "unknown";
    // 服务器状态
    /**
     * 服务器已链接
     */
    public static final String C_sServer_Connect = "connect";  // 服务器已链接
    /**
     * 服务器断开链接
     */
    public static final String C_sServer_Disconnect = "disconnect";  // 服务器断开链接

    private String networkType = C_sConnectionStatus_None;
    private String serverStatus = C_sServer_Connect;
    //默认的构造方法
    public CMPNetinfo() {
    }

    public CMPNetinfo(String fnetworkType, String fserverStatus) {
        this.networkType = fnetworkType;
        this.serverStatus = fserverStatus;

    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }
}
