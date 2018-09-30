package com.missile.util

import android.content.Context
import com.missile.systemwebview.SystemWebView
import org.xmlpull.v1.XmlPullParser

class ConfigXmlParser {

    private val plugins = HashMap<String, String>()
    private var xWalkEngine: String? = null
    private var systemEngine: String? = null

    companion object {
        private var parser: ConfigXmlParser? = null

        @JvmStatic
        fun getPlugins(ctx: Context): HashMap<String, String> {
            if (parser == null) {
                parser = ConfigXmlParser()
                parser?.parse(ctx)
            }
            if (parser?.plugins?.get("Core") == null)
                parser?.plugins?.put("Core", "com.missile.plugin.CoreAndroid")
            return parser?.plugins ?: HashMap()
        }

        @JvmStatic
        fun getEngine(ctx: Context): WebViewEngine {
            if (parser == null) {
                parser = ConfigXmlParser()
                parser?.parse(ctx)
            }

            return try {
                val clazz = Class.forName(parser?.xWalkEngine ?: (parser?.systemEngine))
                val c = clazz.getConstructor(Context::class.java)
                c.newInstance(ctx) as WebViewEngine
            } catch (e: Exception) {
                SystemWebView(ctx)
            }
        }
    }

    internal fun parse(ctx: Context) {
        var id = ctx.resources.getIdentifier("config", "xml", ctx.javaClass.getPackage().name)
        if (id == 0) {
            id = ctx.resources.getIdentifier("config", "xml", ctx.packageName)
            if (id == 0) {
                return
            }
        }
        parse(ctx.resources.getXml(id))
    }

    private fun parse(xml: XmlPullParser) {
        var eventType = -1

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                handleStartTag(xml)
            }

            try {
                eventType = xml.next()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleStartTag(xml: XmlPullParser) {
        val strNode = xml.name
        if (strNode == "plugin") {
            val name = xml.getAttributeValue(null, "name")
            val clazz = xml.getAttributeValue(null, "value")
            plugins[name] = clazz
        }
        if (strNode == "webview") {
            val name = xml.getAttributeValue(null, "name")
            val clazzName = xml.getAttributeValue(null, "value")
            if (name == "XWalkWebView")
                xWalkEngine = clazzName
            else if (name == "SystemWebView")
                systemEngine = clazzName
        }
    }
}
