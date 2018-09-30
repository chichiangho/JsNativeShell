package com.missile.plugin

import android.content.Intent
import com.missile.BaseActionBarWebActivity
import com.missile.SwipeCloseActionBarWebActivity
import com.missile.entity.*
import com.missile.util.WebViewEngine

class CoreAndroid internal constructor(activity: BaseActionBarWebActivity, engine: WebViewEngine) : MissilePlugin(activity, engine) {

    override fun execute(func: String, args: String, callback: MissilePlugin.MissileCallBack) {
        when (func) {
            "loadUrl" -> loadUrl(args, callback)
            "setTitleBar" -> setTitleBar(args, callback)
            "setSwipeAble" -> setSwipeAble(args == "true", callback)
            "setShowProgress" -> setShowProgress(args == "true", callback)
            "backHistory" -> backHistory(args, callback)
            "setOnResultListener" -> setOnResultListener(args, callback)
            "setOnLoadStartedListener" -> setOnLoadStartedListener(args, callback)
            "setOnLoadStoppedListener" -> setOnLoadStoppedListener(args, callback)
            "setRightButtonByIndex" -> setRightButtonByIndex(args, callback)
            "removeRightButtonByIndex" -> removeRightButtonByIndex(args, callback)
            "addRightButton" -> addRightButton(args, callback)
        }
    }

    private fun loadUrl(infoStr: String, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }

        val info = gson.fromJson(infoStr, ActionLoadUrl::class.java)
                ?: let {
                    callback.error()
                    return
                }
        activity.runOnUiThread {
            if (info.inCurPage) {
                activity.loadUrl(PageInfo(info.url, info.titleInfo, info.params))
            } else {
                val intent = Intent(activity, SwipeCloseActionBarWebActivity::class.java)
                intent.putExtra("url", info.url)
                intent.putExtra("titleBarInfo", gson.toJson(info.titleInfo))
                intent.putExtra("params", info.params)
                activity.startActivityForResult(intent, 100)//以result传递返回参数
            }
            callback.success()
        }
    }

    private fun setTitleBar(infoStr: String, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        val info = gson.fromJson(infoStr, TitleBarInfo::class.java)
                ?: let {
                    callback.error()
                    return
                }
        activity.runOnUiThread { activity.setTitleBar(info) }
    }

    private fun setSwipeAble(swipeCloseAble: Boolean, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        (activity as? SwipeCloseActionBarWebActivity)?.runOnUiThread {
            activity.setSwipeAble(swipeCloseAble)
            callback.success()
        }
    }

    private fun setShowProgress(showProgress: Boolean, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        activity.runOnUiThread {
            activity.setShowProgress(showProgress)
            callback.success()
        }
    }

    private fun backHistory(infoStr: String, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        val info = gson.fromJson(infoStr, ActionGoBack::class.java)
                ?: let {
                    callback.error()
                    return
                }
        activity.runOnUiThread {
            activity.goBack(info.backCount, info.backParams)
            callback.success()
        }
    }

    private fun setOnResultListener(info: String?, callback: MissilePlugin.MissileCallBack) {
        info ?: let {
            callback.error()
            return
        }
        val activity = activity ?: let {
            callback.error()
            return
        }
        activity.runOnUiThread {
            activity.pages.lastElement().onResultCallback = info
            callback.success()
        }
    }

    private fun setOnLoadStartedListener(info: String?, callback: MissilePlugin.MissileCallBack) {
        info ?: let {
            callback.error()
            return
        }
        val activity = activity ?: let {
            callback.error()
            return
        }
        activity.runOnUiThread {
            activity.pages.lastElement().onLoadStartedCallback = info
            callback.success()
        }
    }

    private fun setOnLoadStoppedListener(info: String?, callback: MissilePlugin.MissileCallBack) {
        info ?: let {
            callback.error()
            return
        }
        val activity = activity ?: let {
            callback.error()
            return
        }
        activity.runOnUiThread {
            activity.pages.lastElement().onLoadStoppedCallback = info
            callback.success()
        }
    }

    private fun setRightButtonByIndex(infoStr: String, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        val info = gson.fromJson(infoStr, ActionSetRightButtonByIndex::class.java)
                ?: let {
                    callback.error()
                    return
                }
        activity.runOnUiThread {
            activity.setRightButtonByIndex(info)
            callback.success()
        }
    }

    private fun removeRightButtonByIndex(infoStr: String, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        val info = gson.fromJson(infoStr, RightButtonIndex::class.java)
                ?: let {
                    callback.error()
                    return
                }
        activity.runOnUiThread {
            activity.removeRightButtonByIndex(info)
            callback.success()
        }
    }

    private fun addRightButton(infoStr: String, callback: MissilePlugin.MissileCallBack) {
        val activity = activity ?: let {
            callback.error()
            return
        }
        val info = gson.fromJson(infoStr, TitleBarInfo.RightButtonInfo::class.java)
                ?: let {
                    callback.error()
                    return
                }
        activity.runOnUiThread {
            activity.addRightButton(info)
            callback.success()
        }
    }
}
