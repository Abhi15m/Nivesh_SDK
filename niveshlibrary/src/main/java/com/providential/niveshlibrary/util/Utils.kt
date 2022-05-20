package com.providential.niveshlibrary.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.viewbinding.BuildConfig
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.model.DeviceInfo
import com.razorpay.BuildConfig.VERSION_NAME
import retrofit2.HttpException
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

        fun errorMessage(throwable: Throwable): String {
            var message = "";
            val exception = throwable as HttpException
            message = exception.response()?.errorBody()?.string()!!
            return message
        }

        @SuppressLint("HardwareIds")
        fun getDeviceInfo(mContext: Context): DeviceInfo {
            val preferenceHelper: IPreferenceHelper = PreferenceManager(mContext)
            val deviceInfo = DeviceInfo()
            var device_id: String? = ""
            try {
                val telephonyManager =
                    mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                deviceInfo.deviceModel = if (TextUtils.isEmpty(Build.DEVICE)) "" else Build.DEVICE
                deviceInfo.deviceOsVersion = if (TextUtils.isEmpty(Build.VERSION.RELEASE)) "" else Build.VERSION.RELEASE
                deviceInfo.deviceName = if (TextUtils.isEmpty(Build.PRODUCT)) "" else Build.PRODUCT
                deviceInfo.deviceType = "Android"

                if (device_id == null || device_id.isEmpty() || device_id.equals(
                        "unknown",
                        ignoreCase = true
                    )
                ) {
                    device_id = if (TextUtils.isEmpty(Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID))
                    ) "" else Settings.Secure.getString(
                        mContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
                showLog("device_id", "-> $device_id")
                deviceInfo.deviceId = device_id + "#" + preferenceHelper.getAppName()
                deviceInfo.device_id_for_UMSId = device_id
                deviceInfo.sdk_version = com.providential.niveshlibrary.BuildConfig.VERSION_NAME


                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        device_id = telephonyManager.imei
                        if (device_id == null) {
                            device_id =
                                if (TextUtils.isEmpty(Build.getSerial())) "" else Build.getSerial()
                        }
                    } else {
                        device_id = telephonyManager.deviceId
                        if (device_id == null) {
                            device_id =
                                if (TextUtils.isEmpty(Build.SERIAL)) "" else Build.SERIAL
                        }
                    }
                } else {
                    device_id = telephonyManager.deviceId
                    if (device_id == null) {
                        device_id = if (TextUtils.isEmpty(Build.SERIAL)) "" else Build.SERIAL
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (device_id == null || device_id.isEmpty() || device_id.equals(
                        "unknown",
                        ignoreCase = true
                    )
                ) {
                    device_id = if (TextUtils.isEmpty(
                            Settings.Secure.getString(
                                mContext.contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                        )
                    ) "" else Settings.Secure.getString(
                        mContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
                showLog("device_id", "-> $device_id")
                deviceInfo.deviceId = device_id + "#" + preferenceHelper.getAppName()
                deviceInfo.device_id_for_UMSId = device_id
            }
            return deviceInfo
        }

        fun showToast(context: Context,message:String){
            try{
                Toast.makeText(context,message,Toast.LENGTH_LONG).show()
            }catch (ex : Exception){
                ex.printStackTrace()
            }
        }
    }
}