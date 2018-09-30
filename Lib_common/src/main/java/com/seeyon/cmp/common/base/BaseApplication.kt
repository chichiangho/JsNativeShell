package com.seeyon.cmp.common.base

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.forEach

open class BaseApplication : Application() {

    //    lateinit var refWatcher: RefWatcher
//        private set
    lateinit var sharedPreferences: SharedPreferences
        private set
    private val activityStack = ArrayList<Activity>()
    val lifeCycleListenerMap = HashMap<Activity, HashMap<String, ArrayList<() -> Unit>>>()

    fun getActivityStack(): ArrayList<Activity> {
        return activityStack
    }

    fun getTopActivity(): Activity? {
        for (i in activityStack.size - 1 downTo 0) {
            val top = activityStack[i]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!top.isDestroyed)
                    return top
            } else {
                return top
            }
        }
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        refWatcher = LeakCanary.install(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                isActivityVisible = false
                lifeCycleListenerMap[activity]?.get("onPaused")?.forEach { it() }
            }

            override fun onActivityResumed(activity: Activity) {
                isActivityVisible = true
                lifeCycleListenerMap[activity]?.get("onResumed")?.forEach { it() }
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityStack.remove(activity)
                lifeCycleListenerMap[activity]?.get("onDestroyed")?.forEach { it() }
                lifeCycleListenerMap.remove(activity)
//                refWatcher.watch(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                activityStack.add(activity)
            }
        })
    }

    companion object {
        @JvmStatic
        open lateinit var instance: BaseApplication
            private set
        @JvmStatic
        open var isActivityVisible: Boolean = false
    }
}
