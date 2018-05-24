package com.chichaingho.routor.jsnativeshell.entity;

import com.chichaingho.routor.jsnativeshell.JsNativeInterface;

public class PageInfo {
    public String url;
    public String onLoadStartedCallback;//页面开始加载的回调
    public String onLoadStoppedCallback;//页面停止加载的回调
    public String onResultCallback;//返回到此页面时的回调函数
    public String params;
    public TitleBarInfo titleBarInfo; //用来记录titlebar的所有设置信息，当页面跳转后重新设置

    public PageInfo(String url, String titleInfo, String params) {
        this.url = url;
        this.titleBarInfo = JsNativeInterface.gson.fromJson(titleInfo, TitleBarInfo.class);
        this.params = params;
    }
}
