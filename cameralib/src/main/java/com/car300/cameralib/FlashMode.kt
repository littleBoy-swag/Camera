package com.car300.cameralib

/**
 * 闪光灯模式
 * 开启 关闭 自动
 *
 * @author PAN FEI
 * @since 2021/2/22 14:11
 */
sealed class FlashMode {
    object FlashOn : FlashMode() // 开启
    object FlashOff : FlashMode() // 关闭
    object FlashAuto : FlashMode() // 自动

    /**
     * 获取FlashMode的值
     */
    fun getFlashModeValue(mode: FlashMode): Int = when (mode) {
        FlashOn -> 0
        FlashOff -> 1
        FlashAuto -> 2
    }
}