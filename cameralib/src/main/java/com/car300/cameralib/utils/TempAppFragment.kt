package com.car300.cameralib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * android.app包下的Fragment
 *
 * @author panfei.pf
 * @since 2021/3/14 13:01
 */
internal class TempAppFragment : android.app.Fragment() {

    private var requestCode = -1
    private var intent: Intent? = null
    private var callback: ((result: Intent?) -> Unit)? = null

    fun init(requestCode: Int, intent: Intent, callback: ((result: Intent?) -> Unit)) {
        this.requestCode = requestCode
        this.intent = intent
        this.callback = callback
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        intent?.let {
            startActivityForResult(it, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            val result = if (resultCode == Activity.RESULT_OK && data != null) data else null
            callback?.let { it(result) }
        }
    }

    override fun onDetach() {
        super.onDetach()
        intent = null
        callback = null
    }

}