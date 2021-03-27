package com.car300.cameralib.api_new.params

import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.work.FunctionManager
import com.car300.cameralib.api_new.work.IWorker

/**
 * BaseParams
 *
 * @author panfei.pf
 * @since 2021/3/15 17:59
 */
abstract class BaseParams<Params, Result>(internal val manager: FunctionManager) {

    fun then(): FunctionManager {
        manager.workerList.add(getWorker(getParams()))
        return manager
    }

    fun start(callback: GetPhotoCallback<Result>) {
        manager.workerList.add(getWorker(getParams()))
        val iterator = manager.workerList.iterator()
        startApply(null, iterator, callback)
    }

    private fun startApply(
        formerResult: Any?,
        iterator: MutableIterator<Any>,
        callback: GetPhotoCallback<Result>
    ) {
        val worker = iterator.next() as? IWorker<Params, Result>
        worker?.start(formerResult, object : GetPhotoCallback<Result> {
            override fun onSuccess(data: Result) {
                if (iterator.hasNext()) {
                    iterator.remove()
                    startApply(data, iterator, callback)
                } else {
                    callback.onSuccess(data)
                }
            }

            override fun onFail(e: Exception) {
                super.onFail(e)
                callback.onFail(e)
            }

            override fun onCancel() {
                super.onCancel()
                callback.onCancel()
            }
        })
    }

    internal abstract fun getParams(): Params

    internal abstract fun getWorker(params: Params): IWorker<Params, Result>

}