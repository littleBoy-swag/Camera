package com.car300.cameralib.api_new.work

import android.app.Activity
import android.content.Intent
import com.car300.cameralib.api_new.agent.IAcceptResultHandler
import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.result_data.AlbumResult
import com.car300.cameralib.api_new.data.REQUEST_CODE_ALBUM
import com.car300.cameralib.api_new.params.AlbumParams
import com.car300.cameralib.utils.MatisseImgEngine
import com.che300.matisse.Matisse
import com.che300.matisse.MimeType

/**
 * 相册选取
 *
 * @author panfei.pf
 * @since 2021/3/16 16:26
 */
class AlbumWorker(handler: IAcceptResultHandler,params: AlbumParams) :
    BaseWorker<AlbumParams, AlbumResult>(handler,params) {

    override fun start(formerResult: Any?, callback: GetPhotoCallback<AlbumResult>) {
        handler.attachedActivity()?.let {
            albumTake(it, callback)
        }
    }

    private fun albumTake(activity: Activity, callback: GetPhotoCallback<AlbumResult>) {
        val intent = Matisse.from(activity)
            /*.choose(if (mParams.videoMode) MimeType.ofVideo() else MimeType.ofStaticImage())*/
            .choose(MimeType.ofStaticImage())
            .showSingleMediaType(true)
            /*.maxDuration(mParams.maxVideoDuration)
            .minDuration(mParams.minVideoDuration)*/
            .maxSelectable(mParams.maxCount).imageEngine(MatisseImgEngine())
            .thumbnailScale(0.85f).albumIntent
        if (intent.resolveActivity(activity.packageManager) == null) {
            callback.onFail(IllegalStateException("Activity Status Error"))
            return
        }
        try {
            handler.startActivityForResult(intent, REQUEST_CODE_ALBUM) { _, resultCode, data ->
                handleResult(resultCode, data, callback)
            }
        } catch (e: Exception) {
            callback.onFail(e)
        }
    }

    private fun handleResult(
        resultCode: Int,
        data: Intent?,
        callback: GetPhotoCallback<AlbumResult>
    ) {
        if (resultCode == Activity.RESULT_CANCELED) {
            callback.onCancel()
            return
        }
        if (data != null) {
            val pathList = Matisse.obtainPathResult(data)
            val uriList = Matisse.obtainResult(data)
            val result = AlbumResult()
            if (!pathList.isNullOrEmpty()) {
                result.pathList = pathList
            }
            if (!uriList.isNullOrEmpty()) {
                result.uriList = uriList
            }
            callback.onSuccess(result)
        } else {
            callback.onFail(IllegalArgumentException("No Data"))
        }
    }
}