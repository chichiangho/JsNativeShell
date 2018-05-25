package com.chichaingho.routor.jsnativeshell;

import android.content.Intent;

import com.chichaingho.routor.jsnativeshell.entity.ActionGoBack;
import com.chichaingho.routor.jsnativeshell.entity.ActionLoadUrl;
import com.chichaingho.routor.jsnativeshell.entity.ActionSetOnBackListener;
import com.chichaingho.routor.jsnativeshell.entity.ActionSetRightButtonByIndex;
import com.chichaingho.routor.jsnativeshell.entity.PageInfo;
import com.chichaingho.routor.jsnativeshell.entity.RightButtonIndex;
import com.chichaingho.routor.jsnativeshell.entity.TitleBarInfo;
import com.google.gson.Gson;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkActivityDelegate;

import java.lang.ref.SoftReference;

public class JsNativeInterface {
    public static Gson gson = new Gson();
    private SoftReference<BaseActionBarCordovaActivity> softActivity;

    public JsNativeInterface(BaseActionBarCordovaActivity activity) {
        softActivity = new SoftReference<>(activity);
    }

    public void execJs(String method) {
        execJs(method, null);
    }

    public void execJs(String method, String param) {
        if (method == null || method.equals(""))
            return;
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        if (!activity.isXWalkReady())
            return;
        String callback = method;
        if (param != null && !param.isEmpty()) {
            String fixedCallback = method;
            int index = method.indexOf("();");
            if (index >= method.length() - 3)
                fixedCallback = fixedCallback.replace("();", "");
            callback = fixedCallback + "('" + param + "');";
        }
        final String jsFunc = "javascript:" + callback;
        if (activity.isXWalkReady()) {
            activity.webView.evaluateJavascript(jsFunc, null);
        } else {
            activity.activityDelegate = new XWalkActivityDelegate(activity, null, new Runnable() {
                @Override
                public void run() {
                    activity.webView.evaluateJavascript(jsFunc, null);
                }
            });
            activity.activityDelegate.onResume();
        }
    }

    @JavascriptInterface
    public void loadUrl(String infoStr) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;

        final ActionLoadUrl info = gson.fromJson(infoStr, ActionLoadUrl.class);
        if (info == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (info.inCurPage) {
                    activity.loadUrl(new PageInfo(info.url, info.titleInfo, info.params));
                } else {
                    Intent intent = new Intent(activity, SwipeCloseActionBarCordovaActivity.class);
                    intent.putExtra("url", info.url);
                    intent.putExtra("titleBarInfo", JsNativeInterface.gson.toJson(info.titleInfo));
                    intent.putExtra("params", info.params);
                    activity.startActivityForResult(intent, 100);//以result传递返回参数
                }
            }
        });
    }

    @JavascriptInterface
    public void setTitleBar(String infoStr) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        final TitleBarInfo info = gson.fromJson(infoStr, TitleBarInfo.class);
        if (info == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setTitleBar(info);
            }
        });
    }

    @JavascriptInterface
    public void setSwipeAble(final boolean swipeCloseAble) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        if (activity instanceof SwipeCloseActionBarCordovaActivity)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SwipeCloseActionBarCordovaActivity) activity).setSwipeAble(swipeCloseAble);
                }
            });
    }

    @JavascriptInterface
    public void setShowProgress(final boolean showProgress) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setShowProgress(showProgress);
            }
        });
    }

    @JavascriptInterface
    public void backHistory(String infoStr) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        final ActionGoBack info = gson.fromJson(infoStr, ActionGoBack.class);
        if (info == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.goBack(info.backCount, info.backParams);
            }
        });
    }

    @JavascriptInterface
    public void setOnResultListener(final String info) {
        if (info == null)
            return;
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.pages.lastElement().onResultCallback = info;
            }
        });
    }

    @JavascriptInterface
    public void setOnLoadStartedListener(final String info) {
        if (info == null)
            return;
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.pages.lastElement().onLoadStartedCallback = info;
            }
        });
    }

    @JavascriptInterface
    public void setOnLoadStoppedListener(final String info) {
        if (info == null)
            return;
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.pages.lastElement().onLoadStoppedCallback = info;
            }
        });
    }

    @JavascriptInterface
    public void setRightButtonByIndex(String infoStr) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        final ActionSetRightButtonByIndex info = gson.fromJson(infoStr, ActionSetRightButtonByIndex.class);
        if (info == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setRightButtonByIndex(info);
            }
        });
    }

    @JavascriptInterface
    public void removeRightButtonByIndex(String infoStr) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        final RightButtonIndex info = gson.fromJson(infoStr, RightButtonIndex.class);
        if (info == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.removeRightButtonByIndex(info);
            }
        });
    }

    @JavascriptInterface
    public void addRightButton(String infoStr) {
        final BaseActionBarCordovaActivity activity = softActivity.get();
        if (activity == null)
            return;
        final TitleBarInfo.RightButtonInfo info = gson.fromJson(infoStr, TitleBarInfo.RightButtonInfo.class);
        if (info == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.addRightButton(info);
            }
        });
    }
}
