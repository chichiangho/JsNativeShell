package com.missile.plugin

import com.google.gson.Gson
import com.missile.BaseActionBarWebActivity
import com.missile.util.WebViewEngine
import java.lang.ref.SoftReference

abstract class MissilePlugin {
    private lateinit var softActivity: SoftReference<BaseActionBarWebActivity>
    private lateinit var softEngine: SoftReference<WebViewEngine>

    protected val activity: BaseActionBarWebActivity?
        get() = softActivity.get()

    protected val engine: WebViewEngine?
        get() = softEngine.get()

    open fun init(activity: BaseActionBarWebActivity, engine: WebViewEngine) {
        softActivity = SoftReference(activity)
        softEngine = SoftReference(engine)
    }

    abstract fun execute(func: String, args: String, callback: MissileCallBack)

    @JvmOverloads
    fun execJs(method: String?, param: String? = null) {
        if (method?.isBlank() != false)
            return
        var callback: String = method
        if (param?.isBlank() != true) {
            var fixedCallback: String = method
            val index = method.indexOf("();")
            if (index >= method.length - 3)
                fixedCallback = fixedCallback.replace("();", "")
            callback = "$fixedCallback('$param');"
        }
        val jsFunc = "javascript:$callback"
        engine?.evaluateJs(jsFunc, null)
    }

    class MissileCallBack(private val plugin: MissilePlugin?, private val success: String, private val error: String) {
        @JvmOverloads
        fun success(info: String = "") {
            if (!success.isBlank())
                plugin?.execJs(success, info)
        }

        @JvmOverloads
        fun error(errorInfo: String = "") {
            if (!error.isBlank())
                plugin?.execJs(error, errorInfo)
        }
    }

    companion object {
        var gson = Gson()
    }
}
