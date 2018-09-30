package com.seeyon.cmp.common.base

import com.seeyon.cmp.common.extentions.logE

/**
 * Created by chichiangho on 2017/4/18.
 */

class CrushHandler : Thread.UncaughtExceptionHandler {

    fun init() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会回调该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        val builder = StringBuilder("\n")
        builder.append(ex.toString() + "\n")
        builder.append(ex.localizedMessage + "\n")
        val stack = ex.stackTrace
        for (element in stack) {
            builder.append(element.toString() + "\n")
        }
        var throwable: Throwable? = ex.cause
        while (null != throwable) {
            val currentStack = throwable.stackTrace
            for (element in currentStack) {
                builder.append(element.toString() + "\n")
            }
            throwable = throwable.cause
        }
        logE("crush", builder.toString())

//        val log = builder.toString()

        //        RxHttpDataProvider.upLoadCrashLog(log, new RxHttpObserver<BaseResponse>() {
        //            @Override
        //            public void onStart() {
        //
        //            }
        //
        //            @Override
        //            public void onSuccess(BaseResponse response) {
        //
        //            }
        //
        //            @Override
        //            public void onFailed() {
        //
        //            }
        //
        //            @Override
        //            public void onComplete() {
        android.os.Process.killProcess(android.os.Process.myPid())
        //            }
        //        });
    }
}
