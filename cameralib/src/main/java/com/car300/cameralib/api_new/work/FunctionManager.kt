package com.car300.cameralib.api_new.work

import android.app.Activity
import androidx.fragment.app.Fragment
import com.car300.cameralib.api_new.agent.AcceptResultHandlerFactory
import com.car300.cameralib.api_new.agent.IAcceptResultHandler
import com.car300.cameralib.api_new.params.*

/**
 * FunctionManager
 *
 * @author panfei.pf
 * @since 2021/3/15 17:28
 */
class FunctionManager(internal val handler: IAcceptResultHandler) {

    /**
     * workerList
     */
    internal val workerList = ArrayList<Any>()

    /**
     * 单拍
     */
    fun single(): SingleParams = SingleParams(this)

    /**
     * 多怕
     */
    fun multi(): MultiParams = MultiParams(this)

    /**
     * 相册
     */
    fun album(): AlbumParams = AlbumParams(this)

    /**
     * 编辑图片
     */
    fun edit(list: List<String> = arrayListOf()): EditParams = EditParams(this).pathList(list)

    companion object {
        fun create(activity: Activity) =
            FunctionManager(AcceptResultHandlerFactory.create(activity))

        @Throws(IllegalStateException::class)
        fun create(fragment: Fragment) = create(fragment.requireActivity())
        fun create(fragment: android.app.Fragment) = create(fragment.activity)
    }

}