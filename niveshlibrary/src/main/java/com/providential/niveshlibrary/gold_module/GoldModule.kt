package com.providential.niveshlibrary.gold_module

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.ui.NiveshActivity
import com.providential.niveshlibrary.util.Constants
import com.providential.niveshlibrary.util.Coroutines
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.util.Utils
import com.providential.niveshlibrary.web_view.ui.WebViewActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.properties.Delegates

class GoldModule : AppCompatActivity(),TokenListener {

    private lateinit var mContext: Context
    private lateinit var mGoldListener: GoldListener
    private var count:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_module)
    }

    fun goldInvestment(context: Context){
        val intent = Intent(context, WebViewActivity::class.java)
        context.startActivity(intent)
    }

    fun getProductInvestment(context: Context,goldListener: GoldListener){
        mContext = context
        mGoldListener = goldListener
        val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(context) }
        var jsonObject: JSONObject? = null
        jsonObject?.put("clientcode","1014000001")
        jsonObject?.put("ProductId","10")

        val header:Map<String,String> = mapOf("Authorization" to preferenceHelper.getTokenId())

        Utils.showLog("getProductInvestment", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        goldListener.onLoad(context,true)

        Coroutines.main{
            NetworkModule.provideRetrofit().getProductInvestment(header,body)
                .onSuccess {
                    try {
                        val response: String = it.string()
                            goldListener.onLoad(context, false)
                            goldListener.onSuccess(context, response)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        goldListener.onLoad(context,false)
                        goldListener.onFailure(context,e.printStackTrace().toString())
                    }

                }
                .onFailure {
                    goldListener.onLoad(context,false)
                    goldListener.onFailure(context,it.localizedMessage!!)

                    count++

                    NiveshActivity().getToken(context,
                        JSONObject(preferenceHelper.getTokenRequest()),this,Constants.PRODUCT_INVESTMENT,true)
                }
        }
    }

    override fun onStartedToken(context: Context, isLoading: Boolean,flag:Int) {
    }

    override fun onSuccessToken(context: Context, getTokenResponseModel: GetTokenResponseModel,flag:Int) {
        if (count !=2) {
            if (flag == Constants.PRODUCT_INVESTMENT) {
                getProductInvestment(mContext, mGoldListener)
            }
        }
    }

    override fun onFailureToken(context: Context, message: String,flag:Int) {
    }
}