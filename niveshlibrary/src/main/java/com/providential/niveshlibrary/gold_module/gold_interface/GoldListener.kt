package com.providential.niveshlibrary.gold_module.gold_interface

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject

interface GoldListener{
    fun onLoad(context: Context, isLoading: Boolean, flag: Int)
    fun onSuccess(context: Context, jsonObject: JsonObject, flag: Int)
    fun onFailure(context: Context, message: String,flag : Int)

}