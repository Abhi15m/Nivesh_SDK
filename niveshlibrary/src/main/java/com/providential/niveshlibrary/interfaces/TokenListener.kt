package com.providential.niveshlibrary.interfaces

import android.content.Context
import com.providential.niveshlibrary.model.GetTokenResponseModel

interface TokenListener {
    fun onStartedToken(context: Context,isLoading: Boolean,flag:Int)
    fun onSuccessToken(context: Context,getTokenResponseModel: GetTokenResponseModel,flag:Int)
    fun onFailureToken(context: Context,message: String,flag:Int)
}