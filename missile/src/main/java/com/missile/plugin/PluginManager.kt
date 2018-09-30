package com.missile.plugin

import com.missile.BaseActionBarWebActivity
import com.missile.util.ConfigXmlParser
import com.missile.util.WebViewEngine
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
                val c = clazz.getConstructor(BaseActionBarWebActivity::class.java, WebViewEngine::class.java)
                val plugin = c.newInstance(activity, engine) as MissilePlugin
                plugins[key] = plugin
            }
            return plugins
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return HashMap()
    }
}
