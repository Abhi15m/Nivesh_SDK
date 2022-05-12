package com.providential.niveshlibrary.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import com.providential.niveshlibrary.R


class ThemeColors(context: Context) {
    @ColorInt
    var color: Int

    // Checking if title text color will be black
    private val isLightActionBar: Boolean
        private get() { // Checking if title text color will be black
            val rgb: Int = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
            return rgb > 210
        }

    companion object {
        private const val NAME = "ThemeColors"
        private const val KEY = "color"
        fun setNewThemeColor(activity: Activity, color: String) {
            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            editor.putString(KEY, color)
            editor.apply()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) activity.recreate() else {
                val i: Intent? = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName())
                i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(i)
            }
        }
    }

    init {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val stringColor: String? = sharedPreferences.getString(KEY, "004bff")
        color = Color.parseColor("#$stringColor")
        if (isLightActionBar) context.setTheme(R.style.ThemeOverlay_Nivesh_SDK_FullscreenContainer)
        context.setTheme(
            context.resources
                .getIdentifier("T_$stringColor", "style", context.getPackageName())
        )
    }
}