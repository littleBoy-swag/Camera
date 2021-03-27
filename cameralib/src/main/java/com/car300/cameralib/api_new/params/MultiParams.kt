package com.car300.cameralib.api_new.params

import androidx.annotation.IntRange
import com.car300.cameralib.FlashMode
import com.car300.cameralib.PictureQuality
import com.car300.cameralib.api_new.result_data.MultiResult
import com.car300.cameralib.api_new.work.FunctionManager
import com.car300.cameralib.api_new.work.IWorker
import com.car300.cameralib.api_new.work.MultiWorker

/**
 * 多拍api
 *
 * @author panfei.pf
 * @since 2021/3/17 14:03
 */
class MultiParams(manager: FunctionManager) : BaseParams<MultiParams, MultiResult>(manager) {
    /**
     * 多拍最大拍摄数量
     */
    internal var maxCount = 9

    /**
     * 闪光灯模式-默认关闭
     */
    internal var flashMode: FlashMode = FlashMode.FlashOff

    /**
     * 图片质量-默认中等
     */
    internal var pictureQuality: PictureQuality = PictureQuality.Normal

    /**
     * 是否横屏-默认不横屏
     */
    internal var landScape: Boolean = false

    /**
     * 图片列表
     */
    internal var pathList = ArrayList<String>()

    /**
     * 设置最大拍摄数量
     */
    fun maxCount(@IntRange(from = 1) count: Int): MultiParams {
        this.maxCount = count
        return this
    }

    /**
     * 设置闪光灯模式
     */
    fun flashMode(flashMode: FlashMode): MultiParams {
        this.flashMode = flashMode
        return this
    }

    /**
     * 设置图片质量
     */
    fun pictureQuality(quality: PictureQuality): MultiParams {
        this.pictureQuality = quality
        return this
    }

    /**
     * 设置是否横屏
     */
    fun landScape(): MultiParams {
        this.landScape = true
        return this
    }

    /**
     * 图片地址列表
     */
    fun pathList(list: List<String>): MultiParams {
        pathList.clear()
        pathList.addAll(list)
        return this
    }

    override fun getParams(): MultiParams = this

    override fun getWorker(params: MultiParams): IWorker<MultiParams, MultiResult> {
        return MultiWorker(manager.handler, params)
    }

}