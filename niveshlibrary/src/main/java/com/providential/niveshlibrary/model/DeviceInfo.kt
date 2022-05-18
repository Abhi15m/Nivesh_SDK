package com.providential.niveshlibrary.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DeviceInfo : Parcelable {
    @SerializedName("device_id")
    @Expose
    var deviceId: String? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null

    @SerializedName("device_type")
    @Expose
    var deviceType: String? = null

    @SerializedName("device_model")
    @Expose
    var deviceModel: String? = null

    @SerializedName("device_name")
    @Expose
    var deviceName: String? = null

    @SerializedName("device_os_version")
    @Expose
    var deviceOsVersion: String? = null

    @SerializedName("app_version")
    @Expose
    var appVersion: String? = null

    @SerializedName("device_id_for_UMSId")
    @Expose
    var device_id_for_UMSId: String? = null

    @SerializedName("sdk_version")
    @Expose
    var sdk_version: String? = null

    protected constructor(`in`: Parcel) {
        deviceId = `in`.readString()
        deviceToken = `in`.readString()
        deviceType = `in`.readString()
        deviceModel = `in`.readString()
        deviceName = `in`.readString()
        deviceOsVersion = `in`.readString()
        appVersion = `in`.readString()
        device_id_for_UMSId = `in`.readString()
        sdk_version = `in`.readString()
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(deviceId)
        dest.writeString(deviceToken)
        dest.writeString(deviceType)
        dest.writeString(deviceModel)
        dest.writeString(deviceName)
        dest.writeString(deviceOsVersion)
        dest.writeString(appVersion)
        dest.writeString(device_id_for_UMSId)
        dest.writeString(sdk_version)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DeviceInfo?> = object : Parcelable.Creator<DeviceInfo?> {
            override fun createFromParcel(`in`: Parcel): DeviceInfo? {
                return DeviceInfo(`in`)
            }

            override fun newArray(size: Int): Array<DeviceInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}