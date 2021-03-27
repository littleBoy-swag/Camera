package com.car300.cameralib

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.OrientationEventListener
import android.view.View
import com.car300.cameralib.utils.log
import java.util.logging.LogRecord


/**
 * 方向监听
 * 传入上下文以及需要旋转的view集合
 * 第三个可空参数：仅 当前需要横屏拍摄 时传入，当竖屏时展示，横屏是隐藏
 */
class OrientationListener(context: Context?, views: List<View>, view: View?) :
    OrientationEventListener(context) {

    val animViews = views
    val tipView = view

    companion object {
        private const val TAG = "OrientationListener"
    }

    var lastRotation = 0

    var lastOrientation = 361

    private var mTime = 3
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var hadShowTip = false
    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mTime == 0) {
                tipView?.visibility = View.GONE
            }
            mTime--
            mHandler.postDelayed(this, 1_000L)
        }
    }

    override fun onOrientationChanged(orientation: Int) {
        if (!canDetectOrientation()) return
        //过滤抖动  当在45那里抖动的话会来回的进行旋转动画
        if (lastOrientation != 361 && Math.abs(lastOrientation- orientation) < 10) return
        val closestRightAngle = (orientation / 90 + if (orientation % 90 > 45) 1 else 0) * 90 % 360
        val rotation = (360 - closestRightAngle) % 360

        tipView?.apply {
            if (rotation == 0 || rotation == 180) {
                if (!hadShowTip) {
                    tipView.visibility = View.VISIBLE
                    mHandler.post(mRunnable)
                    hadShowTip = true
                }
            } else {
                mHandler.removeCallbacks(mRunnable)
                tipView.visibility = View.GONE
            }
        }

        if (lastRotation == rotation) {
            return
        }


        if (rotation == 0 && lastRotation == 270) {
            lastRotation = -90
        } else if (rotation == 270 && lastRotation == 0) {
            lastRotation = 360
        }
        "onOrientationChanged: from $lastRotation to $rotation".log()

        for (view in animViews) {
            val rotationAnim = ObjectAnimator.ofFloat(
                view,
                "rotation",
                lastRotation.toFloat(),
                rotation.toFloat()
            )
            rotationAnim.duration = 300
            rotationAnim.start()
        }

        lastRotation = rotation
        lastOrientation = orientation
    }
}