package com.car300.cameralib.api_new.agent

import android.app.Activity
import android.content.Intent

/**
 * 抽象调用者
 *
 * @author panfei.pf
 * @since 2021/3/15 17:28
 */
interface IAcceptResultHandler {

    /**
     * 依附的activity
     */
    fun attachedActivity(): Activity?

    /**
     * startActivityForResult
     */
    fun startActivityForResult(
        intent: Intent, requestCode: Int,
        callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit
    )

}