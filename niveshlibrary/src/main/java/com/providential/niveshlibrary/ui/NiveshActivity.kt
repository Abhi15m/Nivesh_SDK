package com.providential.niveshlibrary.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.AuthenticationResult
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Coroutines
import com.providential.niveshlibrary.util.Utils
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class NiveshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nivesh)

    }

    fun getToken(context: Context,jsonObject: JSONObject, authListener: TokenListener){
//        var authListener : TokenListener? = null

        authListener.onStartedToken(context,true)
        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        Coroutines.main{
            NetworkModule.provideRetrofit().getTokenAsync(body)
                .onSuccess { response1->
                    try {
                        authListener.onStartedToken(context,false)
                        authListener.onSuccessToken(context,response1)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        authListener.onStartedToken(context,false)
                        authListener.onFailureToken(context,e.printStackTrace().toString())
                    }

                }
                .onFailure {
                    authListener.onStartedToken(context,false)
                    authListener.onFailureToken(context,it.localizedMessage!!)
                }
        }
    }
}

