package com.car300.cameralib.api_new.work

import android.app.Activity
import android.content.Intent
import com.car300.cameralib.api_new.agent.IAcceptResultHandler
import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.params.BaseParams
import com.car300.cameralib.api_new.result_data.MultiResult
import com.car300.cameralib.api_new.data.REQUEST_CODE_MULTI
import com.car300.cameralib.api_new.params.MultiParams
import com.car300.cameralib.pages.ContinuousShootActivity
import com.car300.cameralib.utils.putExtras

/**
 * 多拍具体处理
 *
 * @author panfei.pf
 * @since 2021/3/16 12:58
 */
class MultiWorker(handler: IAcceptResultHandler,params: MultiParams) :
    BaseWorker<MultiParams, MultiResult>(handler,params) {

    override fun start(formerResult: Any?, callback: GetPhotoCallback<MultiResult>) {
        handler.attachedActivity()?.let {
            multiTake(it, callback)
        }
    }

    private fun multiTake(activity: Activity, callback: GetPhotoCallback<MultiResult>) {
        val intent = Intent(activity, ContinuousShootActivity::class.java)
        handleIntentParams(intent, mParams)
        try {
            handler.startActivityForResult(intent, REQUEST_CODE_MULTI) { _, resultCode, data ->
                handleResult(resultCode, data, callback)
            }
        } catch (e: Exception) {
            callback.onFail(e)
        }
    }

    private fun handleResult(
        resultCode: Int,
        data: Intent?,
        callback: GetPhotoCallback<MultiResult>
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            callback.onCancel()
            return
        }
        if (data != null && data.extras != null) {
            val list = data.extras?.getStringArrayList(ContinuousShootActivity.RESULT_VALUE)
            val result = MultiResult()
            if (!list.isNullOrEmpty()) {
                result.pathList = list
            }
            callback.onSuccess(result)
        } else {
            callback.onFail(IllegalArgumentException("No Data"))
        }
    }

    private fun handleIntentParams(intent: Intent, params: MultiParams) {
        intent.putExtras(
            "maxCount" to params.maxCount,
            "flashMode" to params.flashMode.getFlashModeValue(params.flashMode),
            "pictureQuality" to params.pictureQuality.getPicQualityValue(params.pictureQuality),
            "needHorizontal" to params.landScape,
            "pathList" to params.pathList
        )
    }

}