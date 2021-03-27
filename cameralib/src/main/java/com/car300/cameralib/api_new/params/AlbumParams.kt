package com.car300.cameralib.api_new.params

import androidx.annotation.IntRange
import com.car300.cameralib.api_new.result_data.AlbumResult
import com.car300.cameralib.api_new.work.AlbumWorker
import com.car300.cameralib.api_new.work.FunctionManager
import com.car300.cameralib.api_new.work.IWorker

/**
 * 从相册选取api
 *
 * @author panfei.pf
 * @since 2021/3/17 14:03
 */
class AlbumParams(manager: FunctionManager) : BaseParams<AlbumParams, AlbumResult>(manager) {
    /**
     * 最大数量 视频&图片共用
     */
    internal var maxCount = 1
/*
    *//**
     * 是否是选择视频
     *//*
    internal var videoMode = false

    *//**
     * 最大视频时长
     *//*
    internal var maxVideoDuration = Int.MAX_VALUE

    *//**
     * 最短视频时长
     *//*
    internal var minVideoDuration = 0*/

    fun maxCount(@IntRange(from = 1) count: Int): AlbumParams {
        this.maxCount = count
        return this
    }

    /*fun video(): AlbumParams {
        this.videoMode = true
        return this
    }

    *//**
     * 设置最大视频时长
     *//*
    fun videoMaxDuration(duration: Int): AlbumParams {
        this.maxVideoDuration = duration
        return this
    }

    *//**
     * 设置最短视频时长
     *//*
    fun videoMinDuration(duration: Int): AlbumParams {
        this.minVideoDuration = duration
        return this
    }*/

    override fun getParams(): AlbumParams = this

    override fun getWorker(params: AlbumParams): IWorker<AlbumParams, AlbumResult> {
        return AlbumWorker(manager.handler, params)
    }

}