package com.car300.cameralib.api_new.callback

/**
 * Base回调
 *
 * @author panfei.pf
 * @since 2021/3/16 10:23
 */
interface GetPhotoCallback<Result> {

    /**
     * 成功拿到回调
     */
    fun onSuccess(data: Result)

    /**
     * 异常
     */
    fun onFail(e: Exception) {}

    /**
     * 取消
     */
    fun onCancel() {}

}