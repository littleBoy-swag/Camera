package com.car300.cameralib.api_new.result_data

/**
 * 多拍数据
 *
 * @author panfei.pf
 * @since 2021/3/17 14:11
 */
class MultiResult : BaseResult() {
    /**
     * 图片地址列表
     */
    var pathList: List<String> = ArrayList()
}