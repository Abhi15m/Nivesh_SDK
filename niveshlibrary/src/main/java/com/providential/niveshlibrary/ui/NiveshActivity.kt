package com.providential.niveshlibrary.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.RefreshTokenListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.DeviceInfo
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Constants.COLORS
import com.providential.niveshlibrary.util.Constants.THEME_COLOR
import com.providential.niveshlibrary.util.Coroutines
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException


internal class NiveshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nivesh)

    }

    private fun getApiKey(context: Context, preferenceHelper: IPreferenceHelper): String {
        var apiKey = ""
        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .apply {
                val bundle = metaData
                try {
                    apiKey = bundle.getString("com.providential.nivesh_sdk.ClientId")!!
                    preferenceHelper.setApiKey(apiKey)
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

                try {
                    COLORS = bundle.getString("com.providential.nivesh_sdk.ThemeColor")!!
                    val clr = COLORS.split("|").toTypedArray()
                    THEME_COLOR = clr[0]
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

                try {
                    val appname = this.loadLabel(context.packageManager).toString()
                    preferenceHelper.setAppName(appname)
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
        return apiKey
    }

    internal fun initiateTransaction(
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

        val activationKey: String = getApiKey(context, preferenceHelper)

        jsonObject.put("activationKey", activationKey)
        val deviceInfo: DeviceInfo = Utils.getDeviceInfo(context)

        val platformInfo: JSONObject = JSONObject(Gson().toJson(deviceInfo))
        jsonObject.put("device_info", platformInfo)
        preferenceHelper.setTokenRequest(jsonObject.toString())

//        Utils.showLog("GetToken", "-->$jsonObject")
        val body = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        lifecycleScope.launch(Dispatchers.IO) {
            NetworkModule.provideRetrofit().getTokenAsync(body)
                .onSuccess { response ->
                    try {

                        val response1 = Gson().fromJson(response,GetTokenResponseModel::class.java)
                        if (response1.statusCode == 200) {
                            authListener.onStartedToken(context, false, flag)
                            authListener.onSuccessToken(context, response1, flag)
                            preferenceHelper.setTokenId(response1.response.AuthenticationResult.IdToken)
                            preferenceHelper.setRefreshToken(response1.response.AuthenticationResult.RefreshToken)

                            val loginDetails =
//                                Gson().toJson(response1.response.AuthenticationResult)
                            JSONObject("{\"ExpiresIn\":9600,\"TokenType\":\"Bearer\",\"RefreshToken\":\"eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.AF1RE3_QIaoqGzjvIuHXopnh9GMruY06ESjYY68dbf5sQwg3n9dTPvGGNHsb6iT0_GJd_9jXh-t7-4QQSDGOYipFTv8zKRSKZJAKttG8KMsmDbtYRfByyPMekQXb38Wfsw6drJSOLx0KyKK71HuBJYea4zSoBy3Xbh1-V_VefjTcEaMkMUta20INbyjU-RYwWeVkfvYF9L-EhM_Etc49sDPHOgW9J04KOKRtrzryMQu_D1tVL3zQErecRvWhO_fsHvVi5wcEXXz1oZegKdIGH4gAcwNUFaYwRmWYTBJdiKw_z-GypBzdKY1B6nDpR4nwWQ6yykBjN7Msxod60EDbcg.Mf6n6vzeJkOEN3BZ.rjcRdymf0rdesmB8gWhM32Q2FHw2tNCHF-wOqTP0whVkvvR21moSK_6DagCkQxfU6MFdfwo1DlIhd7UEqPdnIpsObX6-Jr6G3uDJk_cknsegf3ef5m5dmoTttNNg5F3Ap2hf3aDL5Stv3Jig5cj564n7uOWzG5437sjQxgHZdpqMrvtzvnL-c-i7OG-r1-IJbJ5BSDLJvDX0h7GeERrolktTKOAfABzSCkmePpf51hmR0cIcONCcGULYg8lIHJwiXr54mZsk0XNhgvtbXgXR11UrKY1-KZzWDaTqvJiShabRHpZUyvOlQMF4UAjnhpov8z4chsAqG9n7dauEIO8QvBQoXsfqL4P1Qq6GpSEyIK3LSGjeL4v9-MYE9U3V3eo4y0p1Qhcx3ZIYCzcHWRizBWqwn8K1QdOJ0UjXAXl9Y4mZBnMyD2Ul9ogwtH3ziRzG62dAkQZfFnRqCbIaibFOcE_3JoY7aln_KQPH8RH84byZLjdz2mw1iZkkgSqS6b_NWKcL0FeZHNKmb8q87IkFthPNJhQPCWpwHU6OUuqDdc1Jwo6zswoWr1jzDZSGCY3-ku_mvBbiFDdCHSQCEKMmWvKX8s_o4vnAyZRJg5Z_HiM9-iMTep0oA_jTRrE_IwlvLzOA3ZbAKoadyHwfXSsH0OIuYQ1J2AeuChqzp4no5g_SG7izHXj7YVjuGFZTNARvbJHw0HET4Eu8yshZLhIxJzLMjy4xiP4PwRZRnpekzp_zu4lDvCINv3r3Vt21sReuwZK-bTyWX2SKir5sSLj4bW2JelnjZ-H6AfVfUFgm_nV93g7zcuUIlJV_NTbqUrlGJJP2EUx7dVH7TF-GGCIWjuYBigKDhNrGKR1BLZRouYCvet-iP4Tw9LZA1gW6-2N9XM1XimekMGznZ8bfzWrB3AWEMg3CQLvAjc_FpwCsjKPLvZd4c_L4p3HmJG1-IZbSA3ExNbKvJp02aNaVv1UNWtXKySmJYOPfaaedUVP1ZTvm3czYUINJEMNO54OnthfFoLwXcO4Nvf774y_s2V_5zRb9O71dSXRt8VrhkJ2L-PlhVeEqACzxyG_PspAWheNYHRIKpWWubj8HfbwkXOi_PoCszuQRgc2wkUZoDO5YMF8eDdKbAKM2Og7MU3I6K5avdYQXh7jTqbFBL_Wz6qeH4iBLRWS6vQ0cQunv8U8xO24lNLVK6Yr_R2lHDmnZhSlAFu678x6_Nf_cgDjcFmfJ6unksKG1PqjSorZVGgKRGyu4jySX-G1PWlT9A7IJv5wIYKq2GoA6ioaGumbC.lx5xvr8ccjcaxMy4JyVrEQ\",\"IdToken\":\"eyJraWQiOiJUXC9GaExMem0wVFlsQXgrazdrRW85akY4ZXRIQTl2djRSaHZWWU0wemlCMD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiYmUxNGFhNy1lYzk3LTRkNTgtYTA3OC1mYzA2YTVlMjgyMTQiLCJjdXN0b206cGFydG5lckNvZGUiOiIxMDQxIiwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmFwLXNvdXRoLTEuYW1hem9uYXdzLmNvbVwvYXAtc291dGgtMV9RWmhaQlhrRzMiLCJjb2duaXRvOnVzZXJuYW1lIjoiMTA0MTAwMDAwMiIsIm9yaWdpbl9qdGkiOiI1ZDgzOTJlMi1hM2RjLTQ0MjQtYTY1Ni0wZWQ5ZGUxNDUxOWEiLCJhdWQiOiIxNXBtZ2hzMDVnbGNuaGpoczhnNm9wcnF1OSIsImV2ZW50X2lkIjoiY2ZjMDQ2MDUtNzhkOS00NzRhLWEzOTMtMzE3YjE4ZTQ2OGFkIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE2NTI3MDk3NzksInBob25lX251bWJlciI6Iis5MTg2Njg2MTkwNzMiLCJjdXN0b206Y2xpZW50Q29kZSI6IjEwNDEwMDAwMDIiLCJleHAiOjE2NTI3MTkzNzksImlhdCI6MTY1MjcwOTc3OSwianRpIjoiYWFkYjY1NTUtYWY3Yy00NzExLThmOWEtMTlmYTZlY2E2OWJkIiwiZW1haWwiOiJjbGllbnRAbml2ZXNoLmNvbSJ9.SdhL85MBUnHTVR73HGhthOCtY3nMSDf2GM9p7pYTtxfc5WkZznDB6_B4hBf1JtOIEb4DxXX2gNy4WJfzQ4Wn9EJo41DZ1YpG7YEJL7x-x2xQlXAca1rJABU94OZWZVMjrSnJXn8A1aDuNuoS-I4D-YxI0JQm1jkND3qm7fwqG95AOKF3KN-S5QDp5ebciOP9RYFCmedOCiSkKJ8wDyUdWFtBLuSxMVR70Hsk03iXbPSkp2MlPbSPhTkdirFnCRUebR_CLXKCJNO1Bf49Jf66pIab2cE2xQpY_Z_CKuQOeGiU434CyX-90t8Y1_RBRc8WnQ25Yq3t-tivSxCgqk5RLA\",\"clientcode\":\"1041000002\",\"parentcode\":\"1041\",\"ActivationKey\":\"15pmghs05glcnhjhs8g6oprqu9\"}")
                            preferenceHelper.setLoginDetails(loginDetails.toString())
                        }else{
                            authListener.onFailureToken(context, response.toString(), flag)
                        }
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

    internal fun refreshToken(context: Context, refreshTokenListener: RefreshTokenListener, flag: Int) {
        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)

        val header = HashMap<String,String>()
        header["activationKey"] = getApiKey(context, preferenceHelper)
        header["refreshToken"] = preferenceHelper.getRefreshToken()
//        Utils.showLog("refreshToken", "-->$header")
        Coroutines.io {
            NetworkModule.provideRetrofit().getRefreshToken(header)
                .onSuccess { response1 ->
                    try {
                        refreshTokenListener.onSuccessRefreshToken(
                            context,
                            response1,
                            flag,
                            preferenceHelper
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        refreshTokenListener.onErrorRefreshToken(context, e.message!!, flag)
                    }

                }
                .onFailure {
                    try {
                        refreshTokenListener.onErrorRefreshToken(
                            context,
                            Utils.errorMessage(it),
                            flag
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        refreshTokenListener.onErrorRefreshToken(
                            context,
                            it.localizedMessage!!,
                            flag
                        )
                    }
                }
        }
    }


    internal fun getProductInvestment(
        context: Context,
        jsonObject: JSONObject,
        goldListener: GoldListener,
        flag: Int,
        listenerContext: RefreshTokenListener
    ) {

        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)
        goldListener.onLoad(context, true, flag)

//        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val token: String = preferenceHelper.getTokenId()


        Coroutines.io {
            NetworkModule.provideRetrofit().getProductInvestment(token, body)
                .onSuccess { response1 ->
                    try {
                        goldListener.onLoad(context, false, flag)
                        goldListener.onSuccess(context, response1, flag)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        goldListener.onLoad(context, false, flag)
                        goldListener.onFailure(context, e.printStackTrace().toString(), flag)
                    }

                }
                .onFailure {
                    try {
                        val exception = it as HttpException
                        if (exception.code() == 403) {
                            refreshToken(context, listenerContext, flag)
                        } else {
                            goldListener.onLoad(context, false, flag)
                            goldListener.onFailure(context, Utils.errorMessage(it), flag)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        goldListener.onLoad(context, false, flag)
                        goldListener.onFailure(context, it.localizedMessage!!, flag)
                    }
                }
        }
    }
}

