package com.providential.niveshlibrary.gold_module.gold_interface

import android.content.Context
import com.google.gson.JsonObject

interface GoldListener{
    fun onLoad(context: Context, isLoading: Boolean, flag: Int)
    fun onSuccess(context: Context, jsonObject: JsonObject, flag: Int)
    fun onFailure(context: Context, message: String,flag : Int)

}