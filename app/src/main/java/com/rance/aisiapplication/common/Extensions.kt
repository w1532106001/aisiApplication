package com.rance.aisiapplication.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.rance.aisiapplication.R
import com.rance.aisiapplication.utils.TimeUtil


fun Fragment.toast(message: String) = activity?.toast(message)

fun Context.toast(message: String): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

fun Context.longToast(message: Int): Toast = Toast
    .makeText(this, message, Toast.LENGTH_LONG)
    .apply {
        show()
    }

var singleClickInterval = 200L
var lastClickTime = 0L
fun View.setSingleClickListener(
    listener: (View) -> Unit
) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        if (currentClickTime - lastClickTime >= singleClickInterval)
            lastClickTime = currentClickTime
        listener.invoke(this)
    }
}

fun ImageView.loadImage(url: String?) {
    if (TextUtils.isEmpty(url)) return
    Glide.with(this.context).load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(TimeUtil.getTodayString()))
        .placeholder(R.drawable.pic_loading)
        .error(R.drawable.pic_load_failed)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                this@loadImage.setImageResource(R.drawable.pic_load_failed)
                this@loadImage.setOnClickListener {
                    this@loadImage.loadImage(url)
                    this@loadImage.setOnClickListener(null)
                    this@loadImage.isClickable = false
                    this@loadImage.isFocusable = false
                }
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}

fun Fragment.runOnUiThread(action: () -> Unit) {
    activity?.runOnUiThread {
        action()
    }
}


