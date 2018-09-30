package com.missile.plugin

import com.missile.BaseActionBarWebActivity
import com.missile.util.ConfigXmlParser
import com.missile.util.WebViewEngine
import java.lang.reflect.Method
import java.util.*

object PluginManager {
    private var pluginNames: HashMap<String, String>? = null

    @JvmStatic
    fun loadPlugins(activity: BaseActionBarWebActivity, engine: WebViewEngine): HashMap<String, MissilePlugin> {
        try {
            if (pluginNames == null)
                pluginNames = ConfigXmlParser.getPlugins(activity)

            val plugins = HashMap<String, MissilePlugin>()
            for ((key, value) in pluginNames!!) {
                val clazz = Class.forName(value)
                val func = clazz.getMethod("init", BaseActionBarWebActivity::class.java, WebViewEngine::class.java)
                val plugin = clazz.newInstance() as MissilePlugin
                func.invoke(plugin, activity, engine)
                plugins[key] = plugin
            }
            return plugins
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return HashMap()
    }
}
