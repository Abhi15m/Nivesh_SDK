package com.providential.niveshlibrary.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.viewbinding.BuildConfig
import kotlin.math.min
import kotlin.math.roundToInt

class Utils {

    companion object {
        fun startActivity(context: Context, clazz: Class<*>) {
            val intent = Intent(context, clazz)
            context.startActivity(intent)

        }

        fun showLog(TAG: String?, sb: String) {
            if (BuildConfig.DEBUG) {
                if (sb.length > 4000) {
                    Log.e(TAG, "sb.length = " + sb.length)
                    val chunkCount = sb.length / 4000 // integer division
                    for (i in 0..chunkCount) {
                        val max = 4000 * (i + 1)
                        if (max >= sb.length) {
                            Log.e(
                                TAG,
                                "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i)
                            )
                        } else {
                            Log.e(
                                TAG,
                                "chunk $i of $chunkCount:" + sb.substring(
                                    4000 * i,
                                    max
                                )
                            )
                        }
                    }
                } else {
                    Log.e(TAG, sb)
                }
            }
        }

        fun manipulateColor(color: Int, factor: Float): Int {
            val a: Int = Color.alpha(color)
            val r = (Color.red(color) * factor).roundToInt()
            val g = (Color.green(color) * factor).roundToInt()
            val b = (Color.blue(color) * factor).roundToInt()
            return Color.argb(a, min(r, 255), min(g, 255), min(b, 255)
            )
        }
    }
}