package com.providential.niveshlibrary.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.AuthenticationResult
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Coroutines
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.util.Utils
import com.providential.niveshlibrary.web_view.ui.WebViewActivity
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class NiveshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nivesh)

    }

    fun getToken(context: Context,jsonObject: JSONObject, authListener: TokenListener,flag:Int,isInternal:Boolean){
//        var authListener : TokenListener? = null
        val preferenceHelper: IPreferenceHelper =  PreferenceManager(context)
        if (!isInternal) {


//        authListener.onStartedToken(context,true)

            var apiKey = ""

            context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
                .apply {
                    apiKey = metaData.getString("com.providential.nivesh_sdk.ClientId")!!
                    preferenceHelper.setApiKey(apiKey)
                }

            jsonObject.put("clientID", apiKey)
            preferenceHelper.setTokenRequest(jsonObject.toString())
        }



        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Coroutines.main{
            NetworkModule.provideRetrofit().getTokenAsync(body)
                .onSuccess { response1->
                    try {
                        authListener.onStartedToken(context,false,flag)
                        authListener.onSuccessToken(context,response1,flag)
                        preferenceHelper.setTokenId(response1.response.AuthenticationResult.IdToken)
                        Toast.makeText(context,preferenceHelper.getTokenId(),Toast.LENGTH_SHORT).show()

//                        val intent = Intent(context,WebViewActivity::class.java)
//                        context.startActivity(intent)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        authListener.onStartedToken(context,false,flag)
                        authListener.onFailureToken(context,e.printStackTrace().toString(),flag)
                    }

                }
                .onFailure {
                    authListener.onStartedToken(context,false,flag)
                    authListener.onFailureToken(context,it.localizedMessage!!,flag)
                }
        }
    }
}

