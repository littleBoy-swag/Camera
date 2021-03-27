package com.car300.cameralib.api_new.params

import androidx.annotation.DrawableRes
import com.car300.cameralib.FlashMode
import com.car300.cameralib.PictureQuality
import com.car300.cameralib.api_new.result_data.SingleResult
import com.car300.cameralib.api_new.work.FunctionManager
import com.car300.cameralib.api_new.work.IWorker
import com.car300.cameralib.api_new.work.SingleWorker

/**
 * 单拍api
 *
 * @author panfei.pf
 * @since 2021/3/17 14:02
 */
class SingleParams(manager: FunctionManager) :
    BaseParams<SingleParams,SingleResult>(manager) {

    /**
     * 是否横屏-默认不横屏
     */
    internal var landScape: Boolean = false

    /**
     * 闪光灯模式-默认关闭
     */
    internal var flashMode: FlashMode = FlashMode.FlashOff

    /**
     * 图片质量-默认中等
     */
    internal var pictureQuality: PictureQuality = PictureQuality.Normal

    /**
     * 蒙版资源id
     */
    internal var maskId: Int = -1

    /**
     * 蒙版网络图片地址
     */
    internal var maskUrl: String = ""

    /**
     * 顶部悬浮提示语
     */
    internal var topTip: String = ""

    /**
     * 外部传入的图片地址
     */
    internal var path: String = ""

    /**
     * 使用本地资源文件作为蒙版
     */
    fun mask(@DrawableRes id: Int): SingleParams {
        this.maskId = id
        return this
    }

    /**
     * 使用网络图片作为蒙版
     */
    fun mask(url: String): SingleParams {
        this.maskUrl = url
        return this
    }

    /**
     * 设置顶部悬浮提示语
     */
    fun topTip(tip: String): SingleParams {
        this.topTip = tip
        return this
    }

    /**
     * 设置闪光灯模式
     */
    fun flashMode(flashMode: FlashMode): SingleParams {
        this.flashMode = flashMode
        return this
    }

    /**
     * 设置图片质量
     */
    fun pictureQuality(quality: PictureQuality): SingleParams {
        this.pictureQuality = quality
        return this
    }

    /**
     * 设置是否横屏
     */
    fun landScape(): SingleParams {
        this.landScape = true
        return this
    }

    fun targetPath(path: String): SingleParams {
        this.path = path
        return this
    }

    override fun getParams(): SingleParams = this

    override fun getWorker(params: SingleParams): IWorker<SingleParams, SingleResult> {
        return SingleWorker(manager.handler, params)
    }

}