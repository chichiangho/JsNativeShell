package com.seeyon.cmp.common.extentions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Looper
import android.preference.PreferenceManager
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import android.util.TypedValue
import android.widget.Toast
import com.seeyon.cmp.common.base.BaseApplication
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

val appCtx: Context
    get() = BaseApplication.instance.applicationContext

val statusBarHeight
    get() = parseDimension(appCtx.resources.getIdentifier("status_bar_height", "dimen", "android"))

fun getPrivateSharedPreferences(name: String? = null): SharedPreferences =
        name?.let { appCtx.getSharedPreferences(name, Context.MODE_PRIVATE) }
                ?: PreferenceManager.getDefaultSharedPreferences(appCtx)

fun getTopActivity(): Activity? = BaseApplication.instance.getTopActivity()

fun toast(@StringRes id: Int, type: Int = Toast.LENGTH_SHORT) {
    val string = appCtx.getString(id)
    toast(string, type)
}

fun toast(string: String?, type: Int = Toast.LENGTH_SHORT) {
    if (string == null) return
    if (Looper.getMainLooper() === Looper.myLooper())
        Toast.makeText(appCtx, string, type).show()
    else
        getTopActivity()?.runOnUiThread {
            Toast.makeText(appCtx, string, type).show()
        }
}

fun parseDimension(@DimenRes id: Int) = appCtx.resources.getDimension(id).toInt()

fun parseColor(@ColorRes id: Int) = appCtx.resources.getColor(id)

fun parseColor(rgb: String) = Color.parseColor(rgb)

fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), appCtx.resources.displayMetrics).toInt()

private fun Activity.doOnEvent(event: String, action: () -> Unit) {
    BaseApplication.instance.lifeCycleListenerMap[this] ?: let {
        BaseApplication.instance.lifeCycleListenerMap[this] = HashMap()
    }
    val listeners = BaseApplication.instance.lifeCycleListenerMap[this]
    listeners?.get(event) ?: let {
        BaseApplication.instance.lifeCycleListenerMap[this]?.put(event, ArrayList())
    }
    listeners?.get(event)?.add(action)
}


fun Activity.doOnResumed(action: () -> Unit) {
    this.doOnEvent("onResumed", action)
}

fun Activity.doOnPaused(action: () -> Unit) {
    this.doOnEvent("onPaused", action)
}

fun Activity.doOnDestroyed(action: () -> Unit) {
    this.doOnEvent("onDestroyed", action)
}

fun <T> ArrayList<T>.copy(): ArrayList<T> {
    val newArray = ArrayList<T>()
    newArray.addAll(this)
    return newArray
}
