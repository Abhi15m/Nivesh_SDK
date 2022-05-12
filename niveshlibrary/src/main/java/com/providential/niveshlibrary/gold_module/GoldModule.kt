package com.providential.niveshlibrary.gold_module

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.ui.NiveshActivity
import com.providential.niveshlibrary.util.Constants.INITIATE_GOLD_INVESTMENT
import com.providential.niveshlibrary.web_view.ui.WebViewActivity
import org.json.JSONObject

class GoldModule : AppCompatActivity(),TokenListener{

    var mContext: Context ?= null
    var mGoldListener: GoldListener ?= null
    var mFlag:Int = 0
    var startForResult : ActivityResultLauncher<Intent> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_module)
    }

    fun goldInvestment(context: Context, jsonObject: JSONObject, flag: Int, goldListener: GoldListener){
        this.mGoldListener = goldListener
        this.mContext = context
        this.mFlag = flag

        NiveshActivity().getToken(context,jsonObject,this,INITIATE_GOLD_INVESTMENT,true)
    }

    fun getProductInvestment(context: Context,jsonObject: JSONObject,flag: Int, goldListener: GoldListener){
        this.mGoldListener = goldListener
        NiveshActivity().getProductInvestment(context,jsonObject,goldListener,flag,true)
    }

    override fun onStartedToken(context: Context, isLoading: Boolean,flag:Int) {
        mGoldListener?.onLoad(context, true, flag)
    }

    override fun onSuccessToken(context: Context, getTokenResponseModel: GetTokenResponseModel,flag:Int,) {
        mGoldListener?.onLoad(context, false, flag)
        if (flag == INITIATE_GOLD_INVESTMENT) {
            try {
                val intent = Intent(context, WebViewActivity::class.java)
                context.startActivity(intent)
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }
    }


    override fun onFailureToken(context: Context, message: String,flag:Int) {
        mGoldListener?.onLoad(context, false, flag)
        mGoldListener?.onFailure(context,message,flag)
    }
}
