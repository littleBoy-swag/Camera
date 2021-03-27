package com.car300.cameralib.api_new.work

import com.car300.cameralib.api_new.agent.IAcceptResultHandler
import com.car300.cameralib.api_new.params.BaseParams
import com.car300.cameralib.api_new.result_data.BaseResult

/**
 * BaseWorker
 *
 * @author panfei.pf
 * @since 2021/3/16 10:54
 */
abstract class BaseWorker<Params, Result>(val handler: IAcceptResultHandler, val mParams: Params) :
    IWorker<Params, Result>