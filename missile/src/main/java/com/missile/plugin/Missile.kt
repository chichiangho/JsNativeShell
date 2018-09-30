package com.missile.plugin

import android.webkit.JavascriptInterface

class Missile(var plugins: HashMap<String, MissilePlugin>) {

    @JavascriptInterface
    @JvmOverloads
    fun execute(plugin: String, func: String, args: String = "", success: String = "", error: String = "") {
        plugins[plugin]?.execute(func, args, MissilePlugin.MissileCallBack(plugins[plugin], success, error))
    }
}