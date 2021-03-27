package com.car300.cameralib.api_new.agent

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 临时Fragment生成
 *
 * @author panfei.pf
 * @since 2021/3/15 17:30
 */
internal class AcceptResultHandlerFactory {

    companion object {
        private const val TAG = "AcceptResultFragment"

        fun create(activity: Activity): IAcceptResultHandler {
            return if (activity is FragmentActivity) {
                activity.supportFragmentManager.run {
                    val acceptResultSupportFragment =
                        findFragmentByTag(TAG) as? AcceptResultSupportFragment?
                    acceptResultSupportFragment ?: getAcceptResultSupportFragment(this)
                }
            } else {
                activity.fragmentManager.run {
                    val acceptResultFragment = findFragmentByTag(TAG) as? AcceptResultFragment?
                    acceptResultFragment ?: getAcceptResultFragment(this)
                }
            }
        }

        private fun getAcceptResultSupportFragment(fm: FragmentManager): AcceptResultSupportFragment {
            val fragment = AcceptResultSupportFragment()
            fm.beginTransaction().add(fragment, TAG).commitNowAllowingStateLoss()
            return fragment
        }

        private fun getAcceptResultFragment(fm: android.app.FragmentManager): AcceptResultFragment {
            val fragment = AcceptResultFragment()
            fm.beginTransaction().add(fragment, TAG).commitAllowingStateLoss()
            fm.executePendingTransactions()
            return fragment
        }

    }

}