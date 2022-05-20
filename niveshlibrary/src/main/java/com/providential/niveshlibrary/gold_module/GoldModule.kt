package com.providential.niveshlibrary.gold_module

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.JsonObject
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.RefreshTokenListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.ui.NiveshActivity
import com.providential.niveshlibrary.util.Constants.INITIATE_GOLD_INVESTMENT
import com.providential.niveshlibrary.web_view.ui.WebViewActivity
import org.json.JSONObject

class GoldModule : AppCompatActivity(), TokenListener, RefreshTokenListener {

    private var mContext: Context? = null
    private var mGoldListener: GoldListener? = null
    private var mFlag: Int = 0
    private var mJsonObject: JSONObject? = null
    private var count: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_module)
    }

    fun initiateTransaction(
        context: Context,
        jsonObject: JSONObject,
        flag: Int,
        goldListener: GoldListener
    ) {
        this.mGoldListener = goldListener
        this.mContext = context
        this.mFlag = flag

        NiveshActivity().initiateTransaction(
            context,
            jsonObject,
            this,
            INITIATE_GOLD_INVESTMENT,
            true
        )
    }

    fun getProductInvestment(
        context: Context,
        jsonObject: JSONObject,
        flag: Int,
        goldListener: GoldListener
    ) {
        this.mContext = context
        this.mJsonObject = jsonObject
        this.mFlag = flag
        this.mGoldListener = goldListener
        NiveshActivity().getProductInvestment(context, jsonObject, goldListener, flag, this)
    }

    //Get Token for new Client
    override fun onStartedToken(context: Context, isLoading: Boolean, flag: Int) {
        mGoldListener?.onLoad(context, true, flag)
    }

    override fun onSuccessToken(
        context: Context,
        getTokenResponseModel: GetTokenResponseModel,
        flag: Int,
    ) {
        mGoldListener?.onLoad(context, false, flag)
        if (flag == INITIATE_GOLD_INVESTMENT) {
            try {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("initiate_transaction",true)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onFailureToken(context: Context, message: String, flag: Int) {
        mGoldListener?.onLoad(context, false, flag)
        mGoldListener?.onFailure(context, message, flag)
    }

    // Refresh Token
    override fun onSuccessRefreshToken(
        context: Context,
        jsonObject: JsonObject,
        flag: Int,
        preferenceHelper: IPreferenceHelper
    ) {
        val jsonObjectData: JSONObject = JSONObject(jsonObject.toString())
        val tokenId: String =
            jsonObjectData.getJSONObject("data").getJSONObject("AuthenticationResult")
                .getString("IdToken")
        preferenceHelper.setTokenId(tokenId)
        NiveshActivity().getProductInvestment(
            mContext!!,
            mJsonObject!!,
            mGoldListener!!,
            mFlag,
            this
        )
    }

    override fun onErrorRefreshToken(context: Context, message: String, flag: Int) {
        count++
        if (count != 3) {
            NiveshActivity().refreshToken(context, this, flag)
        } else if (count == 3) {
            mGoldListener?.onFailure(context, message, flag)
        }
    }
}
