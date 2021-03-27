package com.car300.cameralib.api_new.result_data

import android.net.Uri

/**
 * 从相册选取的数据
 *
 * @author panfei.pf
 * @since 2021/3/17 14:11
 */
class AlbumResult : BaseResult() {
    var uriList: List<Uri> = ArrayList()
    var pathList: List<String> = ArrayList()
}