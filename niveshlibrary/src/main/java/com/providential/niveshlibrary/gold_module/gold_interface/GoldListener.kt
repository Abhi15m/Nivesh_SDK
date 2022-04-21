package com.providential.niveshlibrary.gold_module.gold_interface

import android.content.Context
import okhttp3.ResponseBody

interface GoldListener {
    fun onLoad(context: Context, isLoading: Boolean)
    fun onSuccess(context: Context, body: String)
    fun onFailure(context: Context, message: String)
}