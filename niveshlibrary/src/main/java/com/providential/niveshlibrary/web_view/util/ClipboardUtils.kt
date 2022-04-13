
package com.providential.niveshlibrary.web_view.util

import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

object ClipboardUtils {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun copyText(context: Context, text: String?) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipData.newPlainText("", text)
                .let {
                    cm.setPrimaryClip(it)
                }
        } else {
            @Suppress("DEPRECATION")
            cm.text = text
        }
    }

    fun hasText(context: Context): Boolean {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val description = cm.primaryClipDescription
            val clipData = cm.primaryClip
            clipData != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        } else {
            @Suppress("DEPRECATION")
            cm.hasText()
        }
    }

    fun getText(context: Context): CharSequence {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val description = cm.primaryClipDescription
            val clipData = cm.primaryClip
            if (clipData != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                clipData.getItemAt(0).text
            } else {
                ""
            }
        } else {
            @Suppress("DEPRECATION")
            cm.text
        }
    }
}