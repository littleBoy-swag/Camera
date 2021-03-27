package com.car300.cameralib.api_new.work

import android.app.Activity
import android.content.Intent
import com.car300.cameralib.api_new.agent.IAcceptResultHandler
import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.params.BaseParams
import com.car300.cameralib.api_new.data.REQUEST_CODE_SINGLE
import com.car300.cameralib.api_new.params.SingleParams
import com.car300.cameralib.api_new.result_data.SingleResult
import com.car300.cameralib.pages.ContinuousShootActivity.Companion.RESULT_VALUE
import com.car300.cameralib.pages.SingleShootActivity
import com.car300.cameralib.utils.putExtras

/**
 * 单拍具体处理
 *
 * @author panfei.pf
 * @since 2021/3/16 10:53
 */
class SingleWorker(handler: IAcceptResultHandler, params: SingleParams) :
    BaseWorker<SingleParams, SingleResult>(handler,params) {

    override fun start(formerResult: Any?, callback: GetPhotoCallback<SingleResult>) {
        handler.attachedActivity()?.let {
            singleTake(it, callback)
        }
    }

    private fun singleTake(activity: Activity, callback: GetPhotoCallback<SingleResult>) {
        val intent = Intent(activity, SingleShootActivity::class.java)
        handleIntentParams(intent, mParams)
        try {
            handler.startActivityForResult(
                intent,
                REQUEST_CODE_SINGLE
            ) { _, resultCode, data ->
                handleResult(resultCode, data, callback)
            }
        } catch (e: Exception) {
            callback.onFail(e)
        }

    }

    private fun handleResult(
        resultCode: Int,
        data: Intent?,
        callback: GetPhotoCallback<SingleResult>
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            callback.onCancel()
            return
        }
        if (data != null && data.extras != null) {
            val list = data.extras?.getStringArrayList(RESULT_VALUE)
            val result = SingleResult()
            if (!list.isNullOrEmpty()) {
                result.path = list[0]
            }
            callback.onSuccess(result)
        } else {
            callback.onFail(IllegalArgumentException("No Data"))
        }
    }

    private fun handleIntentParams(intent: Intent, params: SingleParams) {
        intent.putExtras(
            "topTip" to params.topTip,
            "flashMode" to params.flashMode.getFlashModeValue(params.flashMode),
            "pictureQuality" to params.pictureQuality.getPicQualityValue(params.pictureQuality),
            "maskResUrl" to params.maskUrl,
            "maskResId" to params.maskId,
            "path" to params.path,
            "needHorizontal" to params.landScape
        )
    }

}