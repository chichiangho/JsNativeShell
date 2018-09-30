package com.seeyon.cmp.common.utils;

import com.seeyon.cmp.common.base.BaseApplication;

public class ResourcesUtile {

    /**
     * 根据资源ID获取字符串
     * @param id
     * @return
     */
    public static String getStringByResourcesId(int id){
        BaseApplication speechApp= BaseApplication.getInstance();
        if(speechApp!=null){
            return  BaseApplication.getInstance().getResources().getString(id);
        }else{
            return "";
        }
    }
}
