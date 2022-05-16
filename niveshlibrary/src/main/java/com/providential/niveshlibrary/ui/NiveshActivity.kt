package com.providential.niveshlibrary.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.RefreshTokenListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.util.Constants.COLORS
import com.providential.niveshlibrary.util.Constants.THEME_COLOR
import com.providential.niveshlibrary.util.Coroutines
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.util.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException


class NiveshActivity : AppCompatActivity() {

    var count :Int = 0;

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

        Utils.showLog("GetToken", "-->$jsonObject")
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
                        preferenceHelper.setRefreshToken(response1.response.AuthenticationResult.RefreshToken)

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

    fun refreshToken(context: Context,refreshTokenListener: RefreshTokenListener,flag: Int) {
        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)
        val jsonObject = JSONObject()
        jsonObject.put("ActivationKey",getApiKey(context,preferenceHelper))
        jsonObject.put("RefreshToken",preferenceHelper.getRefreshToken())
        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        Coroutines.main {
            NetworkModule.provideRetrofit().getRefreshToken(body)
                .onSuccess { response1 ->
                    try {
                        refreshTokenListener.onSuccessRefreshToken(context,response1,flag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        refreshTokenListener.onErrorRefreshToken(context,e.message!!,flag)
                    }

                }
                .onFailure{
                    refreshTokenListener.onErrorRefreshToken(context,it.localizedMessage!!,flag)
                }
        }
    }


    fun getProductInvestment(context: Context, jsonObject: JSONObject, goldListener: GoldListener, flag: Int,listenerContext: RefreshTokenListener) {
        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)
        goldListener.onLoad(context, true,flag)

        preferenceHelper.setTokenRequest(jsonObject.toString())

        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val token:String = preferenceHelper.getTokenId()
//        val token:String = "eyJraWQiOiJUXC9GaExMem0wVFlsQXgrazdrRW85akY4ZXRIQTl2djRSaHZWWU0wemlCMD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI5NDM3MTEyYS0zNWQ0LTQ2OWMtODM5Ni00MzZjNDUxZTc5YjkiLCJjdXN0b206cGFydG5lckNvZGUiOiIxMDE0IiwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmFwLXNvdXRoLTEuYW1hem9uYXdzLmNvbVwvYXAtc291dGgtMV9RWmhaQlhrRzMiLCJjb2duaXRvOnVzZXJuYW1lIjoiMTAxNDAwMDAwMSIsIm9yaWdpbl9qdGkiOiJhMjZmZDY5My0yMjdjLTRhZWUtYjgzYi0zMjJlYzRmMjA3Y2UiLCJhdWQiOiIxNXBtZ2hzMDVnbGNuaGpoczhnNm9wcnF1OSIsImV2ZW50X2lkIjoiOWI3ZjBiNjYtZjI5NS00YTI0LTk5NjYtNWIzYWFkOTJlNGFjIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE2NTIxODQzNzksInBob25lX251bWJlciI6Iis5MTg2Njg2MTkwNzMiLCJjdXN0b206Y2xpZW50Q29kZSI6IjEwMTQwMDAwMDEiLCJleHAiOjE2NTIxOTM5NzksImlhdCI6MTY1MjE4NDM3OSwianRpIjoiNjJmNzgxOWMtMGYwNy00NWZiLWI5YjItOWViYTA0ZjgwMjU4IiwiZW1haWwiOiJjbGllbnRAbml2ZXNoLmNvbSJ9.lIayuDB_Br1Bmy1Js3gnhhbMmUdG52qMfM5nB5sYtIo1VRO6JDrvlmB5kEYfwgBJd2Zl6Yg6EpRogKkuGhumSL98wdeTmdU2kT7Meb-WVmEytXPCq6209VDvtX_igC-EPyURmQbAL82XIqWrjB6jOUBLR0imGaMmsYYPHixN0ocDg_cQZdGXzJXF6nps-EBwDTb7tIXXJf6OXxxzHIRBiQpt37F6Kb96i0VwiUzFiQQGI9eQM_JsQwLHpIAOZYy1tdJPNHH7reSKESQuKz02jc_7nZX4NuXxtzC7iXPUahJChrHTz3Ivn_N3s7zPxNZBQG-cgKliihJYbMC0NIITIg"
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
                    val exception = it as HttpException
                    if (exception.code() == 403){
                            refreshToken(context, listenerContext, flag)
                    }else {
                        goldListener.onLoad(context, false, flag)
                        goldListener.onFailure(context, it.localizedMessage, flag)
                    }
                }
        }
    }
}

