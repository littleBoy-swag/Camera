package com.car300.cameralib.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.car300.cameralib.OrientationListener
import com.car300.cameralib.PictureQuality
import com.car300.cameralib.R
import com.car300.cameralib.utils.*
import com.car300.cameralib.widgets.scaleResolution
import com.car300.imgloader.ImgLoader
import io.fotoapparat.result.transformer.scaled
import kotlinx.android.synthetic.main.camera_lib_activity_single_shoot.*
import java.io.File

/**
 * 单拍相机
 */
internal class SingleShootActivity : AppCompatActivity() {

    lateinit var orientationListener: OrientationListener

    private var flashMode = 1
    private var pictureQuality: PictureQuality = PictureQuality.Normal
    private var maskResId = -1
    private var maskResUrl: String = ""
    private var topTip: String = ""
    private var needHorizontal: Boolean = false
    private var givenPath = "" // 由外部传递的图片地址

    /**
     * 初始化Intent传递的参数
     */
    private fun initIntentParams() {
        intent?.let {
            maskResId = it.getIntExtra("maskResId", -1)
            flashMode = it.getIntExtra("flashMode", 2)
            maskResUrl = it.getStringExtra("maskResUrl").toString()
            topTip = it.getStringExtra("topTip").toString()
            needHorizontal = it.getBooleanExtra("needHorizontal", false)
            pictureQuality = when (it.getIntExtra("pictureQuality", 2)) {
                1 -> PictureQuality.High
                2 -> PictureQuality.Normal
                3 -> PictureQuality.Low
                else -> PictureQuality.Normal
            }
            val list = it.getStringArrayListExtra("pathList")
            val path = it.getStringExtra("path")
            list?.let { givenPath = list[0] }
            if (!path.isNullOrEmpty()) {
                givenPath = path
            }
            "topTip is $topTip maskResId is $maskResId,maskResUrl is $maskResUrl, pictureQuality is $pictureQuality，givenPath is $givenPath".log()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        setContentView(R.layout.camera_lib_activity_single_shoot)
        initIntentParams()
        orientationListener = OrientationListener(
            this,
            listOf(iv_flash, iv_camera_switch),
            if (needHorizontal) iv_tip else null
        )
        dealTopTip()
        setMask()
        clickEvents()
    }

    /**
     * 点击事件
     */
    fun clickEvents() {
        iv_flash.singleClick {
            if (cameraLayout.isCameraBack()) {
                flashMode = (flashMode + 1) % 3
            }
            changeFlashState()
        }

        iv_camera_switch.singleClick {
            cameraLayout.changeCamera()
            setMask()
            if (cameraLayout.isCameraBack()) {
                iv_flash.visible()
            } else {
                iv_flash.invisible()
            }
            changeFlashState()
        }

        // 拍照
        val scaledSize = when (pictureQuality) {
            PictureQuality.High -> scaled(scaleFactor = 1f)
            PictureQuality.Normal -> scaleResolution(1080)
            PictureQuality.Low -> scaleResolution(720)
        }
        iv_takepic.singleClick {
            cameraLayout.takePicture()
                .toBitmap(scaledSize)
                .whenAvailable { bp ->
                    bp?.apply {
                        givenPath = if (givenPath.isBlank())  PicHelp.generatePicPath(this@SingleShootActivity) else givenPath
                        PicHelp.saveBitmapFile(
                            bp.bitmap,
                            File(givenPath),
                            bp.rotationDegrees,
                            cameraLayout.isCameraBack()
                        ) { success ->
                            if (!success) {
                                return@saveBitmapFile
                            }
                            ImgLoader.with(this@SingleShootActivity)
                                .url(givenPath)
                                .into(iv_result)
                            setVisibility(
                                iv_flash,
                                rel_cameraLayout,
                                tv_tag_pic,
                                rel_start,
                                iv_tip,
                                visibility = View.GONE
                            )
                            setVisibility(iv_result, rel_end, visibility = View.VISIBLE)
                        }
                    }
                }
        }

        tv_exit.singleClick {
            finish()
        }

        tv_retry.singleClick {
            retry()
        }

        tv_sure.singleClick {
            "givenPath  =  $givenPath".log()
            arrayListOf(givenPath).forEach {
                it.log()
            }
            setResult(
                Activity.RESULT_OK, Intent()
                    .putExtra(ContinuousShootActivity.RESULT_VALUE, arrayListOf(givenPath))
            )
            finish()
        }
    }

    /**
     * 处理顶部提示
     */
    private fun dealTopTip() {
        if (topTip.isNullOrBlank()) {
            tv_tip.visibility = View.GONE
        } else {
            tv_tip.text = topTip
        }
    }

    /**
     * 批量处理显示隐藏
     */
    private fun setVisibility(vararg views: View, visibility: Int) {
        views.forEach {
            it.visibility = visibility
        }
    }

    /**
     * 更新闪光灯UI
     */
    private fun changeFlashState() {
        when (flashMode) {
            0 -> {
                cameraLayout.flashOn()
                iv_flash.setImageResource(R.mipmap.camera_lib_icon_flash_on)
            }
            1 -> {
                cameraLayout.flashOff()
                iv_flash.setImageResource(R.mipmap.camera_lib_icon_flash_off)
            }
            2 -> {
                cameraLayout.flashAuto()
                iv_flash.setImageResource(R.mipmap.camera_lib_icon_flash_auto)
            }
        }
    }

    /**
     * 蒙版设置
     */
    private fun setMask() {
        if (maskResUrl.startsWith("http") || maskResId != -1) {
            val maskView = ImageView(this)
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            if (maskResUrl.startsWith("http")) {
                ImgLoader.with(this@SingleShootActivity)
                    .url(maskResUrl)
                    .into(maskView)
            } else {
                maskView.setImageResource(maskResId)
            }
            rel_cameraLayout.addView(maskView, 2, params)
        }
    }

    /**
     * 点击重试
     */
    private fun retry() {
        givenPath = ""
        setVisibility(
            rel_cameraLayout,
            tv_tag_pic,
            rel_start,
            visibility = View.VISIBLE
        )
        setVisibility(iv_result, rel_end, visibility = View.GONE)
        if (cameraLayout.isCameraBack()) {
            iv_flash.visible()
        } else {
            iv_flash.invisible()
        }
    }

    override fun onStart() {
        super.onStart()
        cameraLayout.start()
        changeFlashState()
    }

    override fun onStop() {
        super.onStop()
        cameraLayout.stop()
    }

    override fun onResume() {
        super.onResume()
        orientationListener.enable()
    }

    override fun onPause() {
        super.onPause()
        orientationListener.disable()
    }

    override fun onBackPressed() {
        if (rel_start.isVisible) {
            finish()
        } else {
            retry()
        }
    }

    override fun onDestroy() {
        TaskThread.shutDown()
        super.onDestroy()
    }

}