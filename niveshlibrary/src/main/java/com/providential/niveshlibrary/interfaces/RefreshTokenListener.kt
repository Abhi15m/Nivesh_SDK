package com.providential.niveshlibrary.interfaces

import android.content.Context
import com.google.gson.JsonObject

interface RefreshTokenListener {
    fun onSuccessRefreshToken(context: Context, jsonObject: JsonObject, flag:Int)
    fun onErrorRefreshToken(context: Context,message:String,flag:Int)
}