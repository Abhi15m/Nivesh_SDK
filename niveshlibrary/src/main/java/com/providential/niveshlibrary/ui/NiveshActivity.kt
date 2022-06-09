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
import com.providential.niveshlibrary.util.Constants.STATUS_BAR_COLOR
import com.providential.niveshlibrary.util.Constants.THEME_COLOR
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
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
                    STATUS_BAR_COLOR = clr[6]
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

        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())

        lifecycleScope.launch(Dispatchers.IO) {
            NetworkModule.provideRetrofit().getTokenAsync(body)
                .onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        try {

                            val response1 =
                                Gson().fromJson(response, GetTokenResponseModel::class.java)
                            if (response1.statusCode == 200) {
                                authListener.onStartedToken(context, false, flag)
                                authListener.onSuccessToken(context, response1, flag)
                                preferenceHelper.setTokenId(response1.response.AuthenticationResult.IdToken)
                                preferenceHelper.setRefreshToken(response1.response.AuthenticationResult.RefreshToken)

                                val loginDetails = Gson().toJson(response1.response.AuthenticationResult)
                                preferenceHelper.setLoginDetails(loginDetails.toString())
                            } else {
                                authListener.onFailureToken(context, response.toString(), flag)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            authListener.onStartedToken(context, false, flag)
                            authListener.onFailureToken(
                                context,
                                e.printStackTrace().toString(),
                                flag
                            )
                        }
                    }

                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        authListener.onStartedToken(context, false, flag)
                        authListener.onFailureToken(context, it.localizedMessage!!, flag)
                    }
                }
        }
    }

    internal fun refreshToken(context: Context, refreshTokenListener: RefreshTokenListener, flag: Int) {
        val preferenceHelper: IPreferenceHelper = PreferenceManager(context)

        val header = HashMap<String,String>()
        header["activationKey"] = getApiKey(context, preferenceHelper)
        header["refreshToken"] = preferenceHelper.getRefreshToken()
//        Utils.showLog("refreshToken", "-->$header")
        lifecycleScope.launch(Dispatchers.IO) {
            NetworkModule.provideRetrofit().getRefreshToken(header)
                .onSuccess { response1 ->
                    withContext(Dispatchers.Main) {
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

                }
                .onFailure {
                    withContext(Dispatchers.Main) {
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
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            jsonObject.toString()
        )
        val token: String = preferenceHelper.getTokenId()


        lifecycleScope.launch(Dispatchers.IO) {
            NetworkModule.provideRetrofit().getProductInvestment(token, body)
                .onSuccess { response1 ->
                    withContext(Dispatchers.Main) {
                        try {
                            goldListener.onLoad(context, false, flag)
                            goldListener.onSuccess(context, response1, flag)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            goldListener.onLoad(context, false, flag)
                            goldListener.onFailure(context, e.printStackTrace().toString(), flag)
                        }
                    }

                }
                .onFailure {
                    withContext(Dispatchers.Main) {
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
}

