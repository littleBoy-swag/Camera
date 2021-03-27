package com.car300.cameralib.utils

import androidx.exifinterface.media.ExifInterface
import io.fotoapparat.exception.FileSaveException
import java.io.File
import java.io.IOException

/**
 * ExifWriter
 *
 * @author PAN FEI
 * @since 2021/2/24 10:56
 */
internal object ExifWriter : ExifOrientationWriter {

    @Throws(FileSaveException::class)
    override fun writeExifOrientation(file: File, rotationDegrees: Int) {
        try {
            ExifInterface(file.path).apply {
                setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    toExifOrientation(rotationDegrees).toString()
                )
                saveAttributes()
            }
        } catch (e: IOException) {
            throw FileSaveException(e)
        }
    }

    private fun toExifOrientation(rotationDegrees: Int): Int {
        val compensationRotationDegrees = (360 - rotationDegrees) % 360

        return when (compensationRotationDegrees) {
            90 -> ExifInterface.ORIENTATION_ROTATE_90
            180 -> ExifInterface.ORIENTATION_ROTATE_180
            270 -> ExifInterface.ORIENTATION_ROTATE_270
            else -> ExifInterface.ORIENTATION_NORMAL
        }
    }

}


interface ExifOrientationWriter {

    /**
     * Writes EXIF orientation tag into a file, overwriting it if it already exists.
     *
     * @param file     File of the image.
     * @param photo    Photo stored in the file.
     * @throws FileSaveException If writing has failed.
     */
    fun writeExifOrientation(file: File, rotationDegrees: Int)
}