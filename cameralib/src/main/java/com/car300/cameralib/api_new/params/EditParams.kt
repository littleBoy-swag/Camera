package com.car300.cameralib.api_new.params

import com.car300.cameralib.api_new.result_data.EditResult
import com.car300.cameralib.api_new.work.EditWorker
import com.car300.cameralib.api_new.work.FunctionManager
import com.car300.cameralib.api_new.work.IWorker

/**
 * 相册编辑的api
 *
 * @author panfei.pf
 * @since 2021/3/17 15:18
 */
class EditParams(manager: FunctionManager) : BaseParams<EditParams, EditResult>(manager) {

    /**
     * 需要处理的图片地址集合
     */
    internal var pathList = ArrayList<String>()

    fun pathList(list: List<String>): EditParams {
        pathList.addAll(list.filter { it.isNotEmpty() })
        return this
    }

    override fun getParams(): EditParams = this

    override fun getWorker(params: EditParams): IWorker<EditParams, EditResult> {
        return EditWorker(manager.handler, params)
    }
}