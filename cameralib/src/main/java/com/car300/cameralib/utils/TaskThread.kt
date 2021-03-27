package com.car300.cameralib.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 线程池
 *
 * @author panfei.pf
 * @since 2021/3/17 11:58
 */
object TaskThread {

    private var executor: ExecutorService? = null
    private var handler = Handler(Looper.getMainLooper())

    fun addTask(r: Runnable) {
        if (executor == null || executor?.isShutdown == true) {
            executor = Executors.newSingleThreadExecutor()
        }
        executor?.submit(r)
    }

    fun shutDown() {
        executor?.shutdownNow()
        executor = null
        handler.removeCallbacksAndMessages(null)
    }

    fun mainPost(f: () -> Unit) {
        handler.post { f.invoke() }
    }

}