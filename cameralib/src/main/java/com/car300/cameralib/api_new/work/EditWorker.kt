package com.car300.cameralib.api_new.work

import android.app.Activity
import com.car300.cameralib.api_new.agent.IAcceptResultHandler
import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.params.EditParams
import com.car300.cameralib.api_new.result_data.AlbumResult
import com.car300.cameralib.api_new.result_data.EditResult
import com.car300.cameralib.api_new.result_data.MultiResult
import com.car300.cameralib.api_new.result_data.SingleResult

/**
 * 编辑工作类
 *
 * @author panfei.pf
 * @since 2021/3/17 15:18
 */
class EditWorker(handler: IAcceptResultHandler, params: EditParams) :
    BaseWorker<EditParams, EditResult>(handler, params) {

    override fun start(formerResult: Any?, callback: GetPhotoCallback<EditResult>) {
        handler.attachedActivity()?.let {
            editPictures(formerResult, it, callback)
        }
    }

    private fun editPictures(
        formerResult: Any?,
        activity: Activity,
        callback: GetPhotoCallback<EditResult>
    ) {
        val formerList = getFormerResult(formerResult)
        val editorOwnList = mParams.pathList // 外部传给它需要编辑的图片地址
        // TODO 跳转到图片编辑页面，这里暂时不处理直接返回
        val result = EditResult()
        formerList.addAll(editorOwnList)
        result.pathList = formerList
        callback.onSuccess(result)
    }

    private fun getFormerResult(formerResult: Any?): ArrayList<String> {
        val list = arrayListOf<String>()
        if (formerResult is SingleResult) { // 单拍过来的结果
            formerResult.path?.takeIf { it.isNotEmpty() }?.apply {
                list.add(this)
            }
        }
        if (formerResult is MultiResult) { // 多拍过来的结果
            formerResult.pathList.takeIf { it.isNotEmpty() }?.apply {
                list.addAll(this)
            }
        }
        if (formerResult is AlbumResult) { // 相册选择过来的结果
            // TODO 处理formerResult的uriList-uri转path
            formerResult.pathList.takeIf { it.isNotEmpty() }?.apply {
                list.addAll(this)
            }
        }
        return list
    }

}