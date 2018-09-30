package com.missile;

import android.os.Bundle;

import com.missile.entity.PageInfo;

public class TestSwipeActivity extends BaseActionBarWebActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //TODO 逻辑抽离，当前由于包依赖关系问题，采用接口回调的方式设置数据，正常应该整理包结构关系，在Util内部处理。url替换工具也应该只有一套，应当删除此util
//        UrlParserUtil.setDefaineder(new UrlParserUtil.UrlParserDefaineder() {
//            @Override
//            public String getLatestVersion(String appName, String appDomain) {
//                return MAppManager.getLatestVersion(appName, appDomain);
//            }
//
//            @Override
//            public String getVersionPath(String appName, String appDomain, String version) {
//                return MAppManager.getAppVersionPath(appName, appDomain, version);
//            }
//
//            @Override
//            public String getUrlBase() {
//                ServerInfo serverInfo = ServerInfoManager.getServerInfo();
//                return serverInfo == null ? "" : serverInfo.getServerurlForSeeyon();
//            }
//        });

//        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, BuildConfig.DEBUG);
        loadUrl(new PageInfo("file:///android_asset/www/index.html", null, null));
    }
}
