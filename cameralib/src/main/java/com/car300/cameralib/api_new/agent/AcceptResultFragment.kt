package com.car300.cameralib.api_new.agent

import android.app.Activity
import android.app.Fragment
import android.content.Intent


/**
 * 临时android.app.Fragment
 *
 * @author panfei.pf
 * @since 2021/3/15 17:34
 */
class AcceptResultFragment : Fragment(), IAcceptResultHandler {

    private var requestCode = 0
    private var callback: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null

    override fun attachedActivity(): Activity? = activity

    override fun startActivityForResult(
        intent: Intent,
        requestCode: Int,
        callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit
    ) {
        this.requestCode = requestCode
        this.callback = callback
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (callback == null || this.requestCode != requestCode) {
            return
        }
        if (isActivityUnavailable()) {
            return
        }
        callback?.invoke(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    private fun isActivityUnavailable(): Boolean {
        return activity == null || activity?.isFinishing == true || activity?.isDestroyed == true
    }
}