package com.providential.niveshlibrary.interfaces

import android.content.Context
import com.providential.niveshlibrary.model.GetTokenResponseModel

interface TokenListener {
    fun onStartedToken(context: Context,isLoading: Boolean)
    fun onSuccessToken(context: Context,getTokenResponseModel: GetTokenResponseModel)
    fun onFailureToken(context: Context,message: String)
}