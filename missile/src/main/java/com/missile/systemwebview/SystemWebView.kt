package com.missile.systemwebview

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import android.webkit.*
import com.missile.BaseActionBarWebActivity
import com.missile.BuildConfig
import com.missile.plugin.Missile
import com.missile.plugin.MissilePlugin
import com.missile.plugin.PluginManager
import com.missile.util.WebViewEngine

class SystemWebView : WebView, WebViewEngine {

    override val view: View
        get() = this

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, privateBrowsing: Boolean) : super(context, attrs, defStyleAttr, privateBrowsing)

    override fun onDestroy() {
        destroy()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun evaluateJs(script: String, resultCallback: ValueCallback<String>?) {
        evaluateJavascript(script, resultCallback)
    }


    override fun captureBitmap(callback: WebViewEngine.GetBitmapCallback) {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        callback.onGetBitmap(bitmap)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init(activity: BaseActionBarWebActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && 0 != (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
        settings.javaScriptEnabled = true

        addJavascriptInterface(Missile(PluginManager.loadPlugins(activity, this)), "Missile")

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                activity.onEvent("onPageStart")
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                activity.onEvent("onPageFinished")
                super.onPageFinished(view, url)
            }
        }
    }
}
