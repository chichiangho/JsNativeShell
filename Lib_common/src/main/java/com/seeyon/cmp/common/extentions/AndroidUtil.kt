package com.seeyon.cmp.common.extentions

import android.app.Activity

/**
 * 专门用于无法使用扩展方法的java类调用扩展方法的中转
 */
object AndroidUtil {
    @JvmStatic
    fun doOnDestroy(activity: Activity, callback: EventCallback) {
        activity.doOnDestroyed {
            callback.onEvent()
        }
    }

    interface EventCallback {
        fun onEvent()
    }
}