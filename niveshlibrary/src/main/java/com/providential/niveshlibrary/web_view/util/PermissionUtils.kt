package com.providential.niveshlibrary.web_view.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0

    fun hasPermission(activity: Activity, permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            return false
        }
        return true
    }
}