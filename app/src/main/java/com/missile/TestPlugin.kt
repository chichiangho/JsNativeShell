package com.missile

import com.chichiangho.common.extentions.commit
import com.chichiangho.common.extentions.rxDoInBackground
import com.chichiangho.common.extentions.rxRunOnUiThread
import com.chichiangho.common.extentions.toast
import com.missile.plugin.MissilePlugin

class TestPlugin : MissilePlugin() {

    override fun execute(func: String, args: String, callback: MissilePlugin.MissileCallBack) {
        rxRunOnUiThread { toast("$func--$args") }
                .rxDoInBackground {

                }
                .commit()
    }
}
