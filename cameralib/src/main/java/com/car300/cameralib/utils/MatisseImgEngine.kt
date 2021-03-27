package com.car300.cameralib.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.car300.imgloader.ImgLoader
import com.che300.matisse.engine.ImageEngine

/**
 * 图库加载器
 *
 * @author panfei.pf
 * @since 2021/3/16 16:31
 */
internal class MatisseImgEngine : ImageEngine {
    override fun loadImage(context: Context?, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?) {
        uri?.let {
            imageView?.let { iv ->
                ImgLoader.asBitmap(context)
                    .url(it)
                    .centerCrop(true)
                    .resize(resizeX, resizeY)
                    .into(iv)
            }
        }
    }

    override fun loadGifImage(context: Context?, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?) {
        loadImage(context, resizeX, resizeY, imageView, uri)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }

    override fun loadGifThumbnail(context: Context?, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) {
        uri?.let {
            imageView?.let { iv ->
                val loader = ImgLoader.asBitmap(context).url(it)
                    .resize(resize, resize).centerCrop(true)
                if (placeholder != null) {
                    loader.placeHolder(placeholder)
                }
                loader.into(iv)
            }
        }
    }

    override fun loadThumbnail(context: Context?, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) {
        uri?.let {
            imageView?.let { iv ->
                val loader = ImgLoader.asBitmap(context).url(it)
                    .resize(resize, resize).centerCrop(true)
                if (placeholder != null) {
                    loader.placeHolder(placeholder)
                }
                loader.into(iv)
            }
        }
    }
}