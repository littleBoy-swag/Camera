package com.car300.cameralib.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * 添加类注释
 *
 * @author PAN FEI
 * @since 2021/2/24 10:59
 */
internal object PicHelp {

    /**
     * 保存Bitmap至文件
     */
    fun saveBitmapFile(bmp: Bitmap, file: File, rotationDegrees: Int, backCamera:Boolean, f: (success: Boolean) -> Unit) {
        TaskThread.addTask(Runnable {
            try {
                val outputStream = FileOutputStream(file).buffered()
                outputStream.use {
                    var b = bmp
                    if (!backCamera) { // 前置需要进行镜像翻转
                        val matrix = Matrix()
                        matrix.postScale(1f,-1f)
                        b = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
                    }
                    b.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    it.flush()
                }
                ExifWriter.writeExifOrientation(file, rotationDegrees)
                TaskThread.mainPost { f.invoke(true) }
            } catch (e: Exception) {
                TaskThread.mainPost { f.invoke(false) }
                e.printStackTrace()
            }
        })
    }

    /**
     * 生成一个图片路径
     */
    fun generatePicPath(context: Context): String {
        var dir = context.getDir("cameraLib", Context.MODE_PRIVATE)
        if (dir == null) {
            dir = File(context.getDir("", Context.MODE_PRIVATE).absolutePath + "/cameraLib")
        }
        val id = UUID.randomUUID().toString().replace("-", "")
        return File(dir, "cameraLib_${id}.jpg").absolutePath
    }

}