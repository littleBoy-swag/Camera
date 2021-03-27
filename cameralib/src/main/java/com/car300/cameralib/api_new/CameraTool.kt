package com.car300.cameralib.api_new

import android.app.Activity
import androidx.fragment.app.Fragment
import com.car300.cameralib.api_new.work.FunctionManager

/**
 * CameraTool
 *
 * @author panfei.pf
 * @since 2021/3/15 17:27
 */
object CameraTool {

    fun with(activity: Activity): FunctionManager = FunctionManager.create(activity)

    fun with(fragment: Fragment) : FunctionManager = FunctionManager.create(fragment)

    fun with(fragment: android.app.Fragment):FunctionManager = FunctionManager.create(fragment)

}