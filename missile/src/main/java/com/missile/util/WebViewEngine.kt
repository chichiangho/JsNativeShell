package com.missile.util

import android.graphics.Bitmap
import android.view.View
import android.webkit.ValueCallback
import com.missile.BaseActionBarWebActivity
import com.missile.plugin.MissilePlugin

interface WebViewEngine {

    val view: View

    fun evaluateJs(script: String, resultCallback: ValueCallback<String>?)

    fun loadUrl(url: String)

    fun init(activity: BaseActionBarWebActivity)

    fun onDestroy()

    fun captureBitmap(callback: GetBitmapCallback)

    interface GetBitmapCallback {
        fun onGetBitmap(b: Bitmap)
    }
}
