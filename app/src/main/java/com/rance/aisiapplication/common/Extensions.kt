package com.rance.aisiapplication.common

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment


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


