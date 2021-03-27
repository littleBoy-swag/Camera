package com.car300.cameralib.utils

import android.view.View

/**
 * 单击
 *
 * @author PAN FEI
 * @since 2021/2/25 10:46
 */

internal abstract class SingleClickListener : View.OnClickListener {
    private val delay = 1000
    private var lastClickTime: Long = 0L
    protected abstract fun onSingleClick(v: View?)

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delay) {
            lastClickTime = currentTime
            onSingleClick(v)
        }
    }
}