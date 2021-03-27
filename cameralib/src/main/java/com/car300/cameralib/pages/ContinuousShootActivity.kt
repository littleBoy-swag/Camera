package com.car300.cameralib.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.car300.cameralib.OrientationListener
import com.car300.cameralib.PictureQuality
import com.car300.cameralib.R
import com.car300.cameralib.utils.*
import com.car300.cameralib.widgets.scaleResolution
import com.car300.imgloader.ImgLoader
import io.fotoapparat.result.transformer.scaled
import kotlinx.android.synthetic.main.camera_lib_activity_camera.*
import java.io.File

/**
 * 多拍
 */
internal class ContinuousShootActivity : AppCompatActivity() {


    companion object {
        const val RESULT_VALUE = "result_value"
    }

    private var orientationListener: OrientationListener? = null // 陀螺仪监听

    private var landscape = false // 是否横屏，默认false
    private var maxCount = 1 // 最大拍摄照片数量 默认1
    private var flashMode = 1 // 闪光灯模式 默认自动模式
    private var pictureQuality: PictureQuality = PictureQuality.Normal // 图片质量 默认中等
    private var resultPathList = arrayListOf<String>() // 已经拍好的图片地址List
    private var givenPathList = arrayListOf<String>() // 由外部传递的图片地址List
    private var curPicClickIndex = -1 // 当前被点击的图片位置

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_lib_activity_camera)
         fullScreen()

        initIntentParams()
        initClicks()
        initRvPics()

    }

    private fun initRvPics() {
        with(rv_pics) {
            layoutManager = LinearLayoutManager(this@ContinuousShootActivity,
                LinearLayoutManager.HORIZONTAL, false)
            adapter = PicAdapter()
            this
        }.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val flag = resultPathList.isNotEmpty()
                if (flag && iv_preview.isVisible) {
                    loadImg(resultPathList[curPicClickIndex], iv_preview, centerCrop = false)
                }
                btn_confirm.visibleOrGone(flag)
                btn_confirm.text = "拍好了(${resultPathList.size})"
                if (resultPathList.isEmpty()) {
                    captureState()
                }
            }
        })
    }

    /**
     * 点击逻辑
     */
    private fun initClicks() {
        // 拍照
        val scaledSize = when (pictureQuality) {
            PictureQuality.High -> scaled(scaleFactor = 1f)
            PictureQuality.Normal -> scaleResolution(1080)
            PictureQuality.Low -> scaleResolution(720)
        }
        btn_capture.singleClick {
            if (tv_continue_hint.isVisible) {
                captureState()
                curPicClickIndex = -1
                rv_pics.adapter?.notifyDataSetChanged()
                return@singleClick
            }
            if (resultPathList.size >= maxCount) {
                showToast("最多添加${maxCount}张照片")
                return@singleClick
            }
            cameraLayout.takePicture()
                .toBitmap(scaledSize)
                .whenAvailable { bp ->
                    bp?.apply {
                        val path = getPicPath()
                        PicHelp.saveBitmapFile(bp.bitmap, File(path), bp.rotationDegrees, cameraLayout.isCameraBack()) { success ->
                            if (!success) {
                                return@saveBitmapFile
                            }
                            resultPathList.add(path)
                            rv_pics.adapter?.notifyDataSetChanged()
                            rv_pics.smoothScrollToPosition(resultPathList.size - 1)
                        }
                    }
                }
        }
        // 切换前后摄像头
        iv_switch.singleClick {
            cameraLayout.changeCamera()
            if (cameraLayout.isCameraBack()) {
                iv_flash.visible()
                updateCameraLayout() // 摄像头切换后置后需要更新闪光灯的设置
            } else { // 前置摄像头隐藏闪光灯按钮
                iv_flash.gone()
            }
        }
        // 闪光灯
        iv_flash.setOnClickListener {
            flashMode = (flashMode + 1) % 3
            updateCameraLayout()
        }
        // 取消-退出页面
        tv_finish.singleClick {
            onBackPressed()
        }
        // 拍好了--关闭页面并带入照片路径
        btn_confirm.singleClick {
            setResult(Activity.RESULT_OK, Intent()
                .putStringArrayListExtra(RESULT_VALUE, resultPathList))
            finish()
        }
    }

    /**
     * 获取一个文件保存地址
     * 外部传递的地址需要注意为合法地址，否则文件将保存失败
     * @sample
     */
    private fun getPicPath(): String {
        if (givenPathList.isEmpty()) { // 外部没有传递地址或外部传递地址已经用完了
            return PicHelp.generatePicPath(this@ContinuousShootActivity)
        }
        var resultPath = ""
        while (resultPath.isEmpty() && givenPathList.isNotEmpty()) {
            resultPath = givenPathList.removeAt(0)
        }
        if (resultPath.isNotEmpty()) {
            return resultPath
        }
        return PicHelp.generatePicPath(this)
    }

    /**
     * 初始化Intent传递的参数
     */
    private fun initIntentParams() {
        intent?.apply {
            landscape = getBooleanExtra("needHorizontal", false)
            orientationListener = OrientationListener(this@ContinuousShootActivity,
                    listOf(btn_capture, iv_switch, iv_flash), if (landscape) iv_landscape_hint else null)
            maxCount = getIntExtra("maxCount", 1)
            flashMode = getIntExtra("flashMode", 1)
            pictureQuality = when (getIntExtra("pictureQuality", 2)) {
                1 -> PictureQuality.High
                2 -> PictureQuality.Normal
                3 -> PictureQuality.Low
                else -> PictureQuality.Normal
            }
            // 将外部传递的图片地址保存起来
            getStringArrayListExtra("pathList")?.onEach {
                givenPathList.add(it ?: "")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        cameraLayout.start()
        updateCameraLayout()
    }

    override fun onResume() {
        super.onResume()
        orientationListener?.enable()
    }

    override fun onPause() {
        super.onPause()
        orientationListener?.disable()
    }

    override fun onStop() {
        super.onStop()
        cameraLayout.stop()
    }

    override fun onDestroy() {
        TaskThread.shutDown()
        super.onDestroy()
    }

    /**
     * 更新CameraLayout
     */
    private fun updateCameraLayout() {
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

    override fun onBackPressed() {
        if (resultPathList.isEmpty()) {
            super.onBackPressed()
            return
        }
        if (tv_continue_hint.isVisible) {
            captureState()
            curPicClickIndex = -1
            rv_pics.adapter?.notifyDataSetChanged()
            return
        }
        showQuitConfirmDialog()
    }

    /**
     * 退出确认框
     */
    private fun showQuitConfirmDialog() {
        val view = LayoutInflater.from(this@ContinuousShootActivity)
            .inflate(R.layout.camera_lib_layout_custom_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .show()
        view.findViewById<TextView>(R.id.tv_continue).singleClick {
            dialog.dismiss()
        }
        view.findViewById<TextView>(R.id.tv_quit).singleClick {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }
    }

    /**
     * RecyclerView的Adapter
     */
    private inner class PicAdapter : RecyclerView.Adapter<PicAdapter.PicViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder {
            return PicViewHolder(
                LayoutInflater.from(this@ContinuousShootActivity)
                    .inflate(R.layout.camera_lib_layout_item_pic, parent, false)
            )
        }

        override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
            val item = resultPathList[position]
            if (curPicClickIndex == position) {
                holder.vBg.background = ResourcesCompat.getDrawable(resources, R.drawable.camera_lib_bg_mask_item, null)
            } else {
                holder.vBg.background = null
            }
            loadImg(item, holder.ivPic, 4)
            holder.ivDel.singleClick {
                if (iv_preview.isVisible) {
                    handleCurrentClickIndex(position)
                }
                resultPathList.removeAt(position)
                notifyDataSetChanged()
            }
            holder.ivPic.singleClick {
                tv_continue_hint.visible()
                curPicClickIndex = position
                previewState()
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int = resultPathList.size

        private inner class PicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val vBg: View = itemView.findViewById<View>(R.id.v_bg)
            val ivPic: ImageView = itemView.findViewById(R.id.iv_pic)
            val ivDel: ImageView = itemView.findViewById(R.id.iv_delete)
        }

    }

    /**
     * 处理被移除的position与curClickIndex的关系
     */
    private fun handleCurrentClickIndex(removedPosition: Int) {
        if (removedPosition > curPicClickIndex) { // 移除的pos大于当前选中的pos
            return
        }
        if (removedPosition < curPicClickIndex) { // 移除的pos小于当前选中的pos
            curPicClickIndex -= 1
        }
        if (removedPosition == curPicClickIndex
            && removedPosition == resultPathList.size - 1) { // 移除的pos等于当前选中的pos
            // 在最右边的时候-1 在最左边or中间不做处理
            curPicClickIndex -= 1
        }
    }

    /**
     * 加载图片
     */
    private fun loadImg(path: String, iv: ImageView, radius: Int = -1, centerCrop: Boolean = true) {
        val loader = ImgLoader.with(this@ContinuousShootActivity)
            .centerCrop(centerCrop)
            .corner(radius)
            .url(path)
        if (iv.drawable != null) {
            loader.placeHolder(iv.drawable)
        }
        loader.into(iv)
    }

    /**
     * 预览状态
     */
    private fun previewState() {
        iv_preview.visible()
        iv_switch.gone()
        iv_flash.gone()
        tv_continue_hint.visible()
        btn_capture.setImageResource(R.mipmap.camera_lib_icon_shoot_preview)
    }

    /**
     * 拍摄状态
     */
    private fun captureState() {
        curPicClickIndex = -1
        iv_preview.setImageDrawable(null)
        iv_preview.gone()
        iv_switch.visible()
        iv_flash.visibleOrGone(cameraLayout.isCameraBack())
        tv_continue_hint.invisible()
        btn_capture.setImageResource(R.mipmap.camera_lib_icon_shoot)
    }

}