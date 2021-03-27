package com.car300.camerademo;

import android.app.Activity;

import com.car300.cameralib.FlashMode;
import com.car300.cameralib.api_new.CameraTool;
import com.car300.cameralib.api_new.callback.GetPhotoCallback;
import com.car300.cameralib.api_new.result_data.SingleResult;

import org.jetbrains.annotations.NotNull;

/**
 * 添加类注释
 *
 * @author panfei.pf
 * @since 2021/3/10 10:27
 */
class JavaUse {

    public static void camera(Activity activity) {

        CameraTool.INSTANCE.with(activity)
                .single()
                .topTip("CameraTool CameraTool")
                .mask("xxx")
                .flashMode(FlashMode.FlashAuto.INSTANCE)
                .start(new GetPhotoCallback<SingleResult>() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSuccess(SingleResult data) {

                    }

                    @Override
                    public void onFail(@NotNull Exception e) {

                    }
                });
    }

}
