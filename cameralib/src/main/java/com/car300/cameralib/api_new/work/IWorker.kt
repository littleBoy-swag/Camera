package com.car300.cameralib.api_new.work

import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.params.BaseParams
import com.car300.cameralib.api_new.result_data.BaseResult

/**
 * IWorker
 *
 * @author panfei.pf
 * @since 2021/3/16 10:22
 */
interface IWorker<Params, Result> {
    fun start(formerResult: Any?, callback: GetPhotoCallback<Result>)
}