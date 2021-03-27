package com.car300.cameralib.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.Zoom
import io.fotoapparat.result.PhotoResult
import io.fotoapparat.result.transformer.ResolutionTransformer
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import io.fotoapparat.view.FocusView

class CameraLayout : FrameLayout {

    private val TAG = "CameraLayout"

    val cameraView: CameraView
    val focusView: FocusView

    var fotoapparat: Fotoapparat
        private set

    private var activeCamera: CameraConfig = CameraConfig.Back

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        cameraView = CameraView(context, attrs, defStyleAttr)
        addView(
            cameraView,
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )

        focusView = FocusView(context, attrs, defStyleAttr)
        cameraView.addView(
            focusView,
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )

        val configuration = CameraConfiguration(
            previewResolution = wideRatio(highestResolution()),
            pictureResolution = wideRatio(highestResolution()),
            previewFpsRange = highestFps(),          // (optional) we want to have the best frame rate
            focusMode = firstAvailable(              // (optional) use the first focus mode which is supported by device
                continuousFocusPicture(),
                autoFocus(),                     // if continuous focus is not available on device, auto focus will be used
                fixed()                          // if even auto focus is not available - fixed focus mode will be used
            ),
            flashMode = firstAvailable(
                off(),
                torch()
            ),
            antiBandingMode = auto(),
            sensorSensitivity = highestSensorSensitivity() // (optional) we want to have the lowest sensor sensitivity (ISO)
        )

        fotoapparat = Fotoapparat(
            context = context,
            focusView = focusView,               // set focus view impl interface o.fotoapparat.view.FocalPointSelector
            view = cameraView,                   // view which will draw the camera preview
            scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
            lensPosition = activeCamera.lensPosition,               // (optional) we want back camera
            cameraConfiguration = activeCamera.configuration, // (optional) define an advanced configuration
            cameraErrorCallback = { it.printStackTrace() }   // (optional) log fatal errors
        )

        scaleGestureDetector = ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    return setZoom(detector?.scaleFactor ?: 1f)
                }
            })
    }

    private sealed class CameraConfig(
        val lensPosition: LensPositionSelector,
        val configuration: CameraConfiguration
    ) {

        object Back : CameraConfig(
            lensPosition = back(),
            configuration = CameraConfiguration(
                previewResolution = firstAvailable(
                    wideRatio(highestResolution()),
                    standardRatio(highestResolution())
                ),
                previewFpsRange = highestFps(),
                flashMode = off(),
                focusMode = firstAvailable(
                    continuousFocusPicture(),
                    autoFocus()
                ),
                frameProcessor = {
                    // Do something with the preview frame
                }
            )
        )

        object Front : CameraConfig(
            lensPosition = front(),
            configuration = CameraConfiguration(
                previewResolution = firstAvailable(
                    wideRatio(highestResolution()),
                    standardRatio(highestResolution())
                ),
                previewFpsRange = highestFps(),
                flashMode = off(),
                focusMode = firstAvailable(
                    fixed(),
                    autoFocus()
                )
            )
        )
    }

    /** 缩放用的一些常量 */
    private var scaleGestureDetector: ScaleGestureDetector
    private var maxZoom = -1
    private var lastZoom = 0f

    private fun setZoom(scaleFactor: Float): Boolean {
        val level1 = 1f / (if (maxZoom <= 0) 60 else maxZoom)
        if (lastZoom == 0f && scaleFactor > 1f) {
            // 第一次缩放，设置上一次缩放值为第一个缩放等级
            lastZoom = level1
        }

        var zoom = lastZoom * scaleFactor
        zoom = Math.min(1f, Math.max(0f, zoom))
        if (zoom < level1) {// 已经小于了第一个放大等级了，直接用最小的等级
            zoom = 0f
        }
        if (zoom != lastZoom) {
            fotoapparat.setZoom(zoom)
            lastZoom = zoom
            return false
        }
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return scaleGestureDetector.onTouchEvent(ev) && super.dispatchTouchEvent(ev)
    }

    fun start() {
        fotoapparat.start()
        if (maxZoom <= 0) {
            fotoapparat.getCapabilities()
                .transform { it.zoom }
                .whenAvailable {
                    val zoom = it as? Zoom.VariableZoom? ?: return@whenAvailable
                    maxZoom = zoom.maxZoom
                }
        }
    }

    fun stop() {
        fotoapparat.stop()
    }

    /**
     * 闪光灯拍照时打开
     */
    fun flashOn() {
        fotoapparat.updateConfiguration(
            UpdateConfiguration.builder()
                .flash(on())
                .build()
        )
    }

    /**
     * 闪光灯长亮
     */
    fun flashTorch() {
        fotoapparat.updateConfiguration(
            UpdateConfiguration.builder()
                .flash(torch())
                .build()
        )
    }

    /**
     * 闪光灯自动
     */
    fun flashAuto() {
        fotoapparat.updateConfiguration(
            UpdateConfiguration.builder()
                .flash(autoFlash())
                .build()
        )
    }

    /**
     * 闪光灯关闭
     */
    fun flashOff() {
        fotoapparat.updateConfiguration(
            UpdateConfiguration.builder()
                .flash(off())
                .build()
        )
    }

    /**
     * 前置后置切换
     */
    fun changeCamera() {
        activeCamera = when (activeCamera) {
            CameraConfig.Front -> CameraConfig.Back
            CameraConfig.Back -> CameraConfig.Front
        }

        fotoapparat.switchTo(
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration
        )
    }

    /**
     * 判断当前摄像头朝向
     * true 后置
     * false 前置
     */
    fun isCameraBack() = when (activeCamera) {
        CameraConfig.Front -> false
        CameraConfig.Back -> true
    }


    /**
     * 拍摄
     */
    fun takePicture(): PhotoResult {
        return fotoapparat.takePicture()
    }

    /**
     * 更新Fotoapparat设置
     */
    fun updateConfiguration(newConfiguration: UpdateConfiguration) {
        fotoapparat.updateConfiguration(newConfiguration)
    }

}

/**
 * 计算Bitmap缩放比
 */
fun scaleResolution(height: Int): ResolutionTransformer = { input ->
    var scaleFactor = 1f
    if (input.height > height) {
        scaleFactor = height.toFloat() / input.height
    }
    Resolution(
        width = (input.width * scaleFactor).toInt(),
        height = (input.height * scaleFactor).toInt()
    )
}