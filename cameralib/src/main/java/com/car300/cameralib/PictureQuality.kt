package com.car300.cameralib

/**
 * 图片质量
 * 高 中 低
 *
 * @author PAN FEI
 * @since 2021/2/22 14:12
 */
sealed class PictureQuality {
    object High : PictureQuality() // 高
    object Normal : PictureQuality() // 中
    object Low : PictureQuality() // 低

    /**
     * 获取PictureQuality的值
     */
    fun getPicQualityValue(quality: PictureQuality): Int = when (quality) {
        High -> 1
        Normal -> 2
        Low -> 3
    }

}