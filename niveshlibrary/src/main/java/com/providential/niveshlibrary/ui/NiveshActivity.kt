package com.providential.niveshlibrary.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.Errors
import com.providential.niveshlibrary.util.Constants.COLORS
import com.providential.niveshlibrary.util.Constants.THEME_COLOR
import com.providential.niveshlibrary.util.Coroutines
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.util.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


class NiveshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nivesh)

    }

    private fun getApiKey(context: Context, preferenceHelper: IPreferenceHelper): String {
        var apiKey = ""
        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .apply {

                val bundle = metaData
                apiKey = bundle.getString("com.providential.nivesh_sdk.ClientId")!!
                preferenceHelper.setApiKey(apiKey)

                COLORS = bundle.getString("com.providential.nivesh_sdk.ThemeColor")!!
                val clr  = COLORS.split("|").toTypedArray()
                THEME_COLOR = clr[0]
            }
        return apiKey
    }

    fun getToken(
        context: Context,
        jsonObject: JSONObject,
        authListener: TokenListener,
        flag: Int,
        isInternal: Boolean
    ) {
        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)
        authListener.onStartedToken(context, true, flag)
        preferenceHelper.setApiKey("")
        preferenceHelper.clearPrefs()

        jsonObject.put("clientID", getApiKey(context, preferenceHelper))
        preferenceHelper.setTokenRequest(jsonObject.toString())

        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Coroutines.main {
            NetworkModule.provideRetrofit().getTokenAsync(body)
                .onSuccess { response1 ->
                    try {
                        val jsonObject = response1
                        authListener.onStartedToken(context, false, flag)
                        authListener.onSuccessToken(context, response1, flag)
                        preferenceHelper.setTokenId(response1.response.AuthenticationResult.IdToken)

                        val loginDetails = Gson().toJson(response1.response.AuthenticationResult)
                        preferenceHelper.setLoginDetails(loginDetails)



                    } catch (e: Exception) {
                        e.printStackTrace()
                        authListener.onStartedToken(context, false, flag)
                        authListener.onFailureToken(context, e.printStackTrace().toString(), flag)
                    }

                }
                .onFailure {
                    authListener.onStartedToken(context, false, flag)
                    authListener.onFailureToken(context, it.localizedMessage!!, flag)
                }
        }
    }


    fun getProductInvestment(context: Context, jsonObject: JSONObject, goldListener: GoldListener, flag: Int, isInternal: Boolean
    ) {
        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)
        goldListener.onLoad(context, true,flag)

        preferenceHelper.setTokenRequest(jsonObject.toString())

        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val token:String = preferenceHelper.getTokenId()
        Coroutines.main {
            NetworkModule.provideRetrofit().getProductInvestment(token,body)
                .onSuccess { response1 ->
                    try {
                        goldListener.onLoad(context, false,flag)
                        goldListener.onSuccess(context,response1,flag)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        goldListener.onLoad(context, false, flag)
                        goldListener.onFailure(context, e.printStackTrace().toString(), flag)
                    }

                }
                .onFailure{
                    goldListener.onLoad(context, false, flag)
                    goldListener.onFailure(context, it.localizedMessage, flag)
                }
        }
    }
}

