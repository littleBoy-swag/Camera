package com.car300.camerademo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.car300.cameralib.PictureQuality
import com.car300.cameralib.api_new.CameraTool
import com.car300.cameralib.api_new.callback.GetPhotoCallback
import com.car300.cameralib.api_new.result_data.AlbumResult
import com.car300.cameralib.api_new.result_data.EditResult
import com.car300.cameralib.api_new.result_data.MultiResult
import com.car300.cameralib.api_new.result_data.SingleResult
import com.che300.image_edite.preview.IMGPreViewActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_single.setOnClickListener {
            AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted {
                    CameraTool.with(this).single().mask(R.mipmap.mask_front)
                        .landScape()
                        .pictureQuality(PictureQuality.Normal)
                        .targetPath("")
                        .topTip("CameraTool CameraTool")
                        .start(object : GetPhotoCallback<SingleResult> {
                            override fun onSuccess(data: SingleResult) {
                                tv_paths.text = data.path ?: ""
                            }

                            override fun onFail(e: Exception) {
                                showToast(e.message.toString())
                            }
                        })
                }.onDenied {
                    showToast("没有权限真办不到~")
                }.start()
        }

        btn_continuous.setOnClickListener {
            AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted {
                    CameraTool.with(this).multi().pictureQuality(PictureQuality.Low)
                        .maxCount(9)
                        .pathList(arrayListOf())
                        .start(object : GetPhotoCallback<MultiResult> {
                            override fun onSuccess(data: MultiResult) {
                                tv_paths.text = ""
                                data.pathList.forEach {
                                    tv_paths.text =
                                        tv_paths.text.toString().plus("\n").plus(it)
                                }
                            }
                        })
                }
                .onDenied {
                    showToast("没有权限真办不到~")
                }.start()
        }

        btn_album.setOnClickListener {
            AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted {
                    CameraTool.with(this).album().maxCount(1)
                        .start(object : GetPhotoCallback<AlbumResult> {
                            override fun onSuccess(data: AlbumResult) {
                                tv_paths.text = ""
                                data.pathList.forEach {
                                    tv_paths.text =
                                        tv_paths.text.toString().plus("\n").plus(it)
                                }
                            }

                            override fun onFail(e: Exception) {
                                showToast(e.message.toString())
                            }
                        })
                }.onDenied {
                    showToast("没有权限真办不到~")
                }.start()

        }

        btn_multi_album.setOnClickListener {
            CameraTool.with(this)
                .multi()
                .maxCount(2)
                .then()
                .album()
                .maxCount(2)
                .then()
                .edit()
                .start(object :GetPhotoCallback<EditResult>{
                    override fun onSuccess(data: EditResult) {
                        tv_paths.text = ""
                        data.pathList.forEach {
                            tv_paths.text =
                                tv_paths.text.toString().plus("\n").plus(it)
                        }
                        tv_paths.text = tv_paths.text.toString().plus("\n")
                            .plus("只有两个地址是因为只有编辑才会接收之前操作的地址")
                    }

                    override fun onCancel() {
                        super.onCancel()
                        showToast("onCancel")
                    }

                    override fun onFail(e: Exception) {
                        super.onFail(e)
                        showToast(e.message.toString())
                    }
                })
        }

        btn_edit.setOnClickListener {
            AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted {
                    CameraTool.with(this).album().maxCount(16)
                        .start(object : GetPhotoCallback<AlbumResult> {
                            override fun onSuccess(data: AlbumResult) {
                                val i = Intent(this@MainActivity, IMGPreViewActivity::class.java)
                                i.putExtra("list", ArrayList<Uri>(data.uriList))
                                startActivity(i)
                                // TODO 少个编辑后回调
                            }

                            override fun onFail(e: Exception) {
                                showToast(e.message.toString())
                            }
                        })
                }.onDenied {
                    showToast("没有权限真办不到~")
                }.start()

        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}