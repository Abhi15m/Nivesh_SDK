package com.providential.nivesh_sdk

import android.app.ProgressDialog
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.providential.niveshlibrary.gold_module.GoldModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.interfaces.RefreshTokenListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.util.Utils
import org.json.JSONObject

class MainActivity : AppCompatActivity(), GoldListener, RefreshTokenListener {

    var tokenListener: TokenListener? = null

    private lateinit var btnTest: Button
    private lateinit var btnWebView: Button
    private val BUY_GOALD: Int = 101
    private val PRODUCT_INVESTMENT: Int = 102
    private val customBroadcastReceiver = CustomBroadcastReceiver()
    var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest = findViewById(R.id.btnTest)
        btnWebView = findViewById(R.id.btnWebView)
        progressDialog = ProgressDialog(this)
//        setWebView()
        btnTest.setOnClickListener {
            try {
                val jsonObject = JSONObject()
                val jsonObjectData = JSONObject()
                val jsonObjectPartnerData = JSONObject()
                val addressClientData = JSONObject()
                val addressPartnerData = JSONObject()

                addressClientData.put("addressLine1", "")
                addressClientData.put("addressLine2", "")
                addressClientData.put("state", "")
                addressClientData.put("city", "")
                addressClientData.put("country", "")
                addressClientData.put("pincode", "")

                addressPartnerData.put("addressLine1", "")
                addressPartnerData.put("addressLine2", "")
                addressPartnerData.put("state", "")
                addressPartnerData.put("city", "")
                addressPartnerData.put("country", "")
                addressPartnerData.put("pincode", "")

                jsonObjectData.put("userid", "XYX")
                jsonObjectData.put("name", "XYX")
                jsonObjectData.put("emailId", "client@nivesh.com")
                jsonObjectData.put("phoneNo", "8668619073")
                jsonObjectData.put("pan", "BNXPC3905P")
                jsonObjectData.put("accountNo", "23710239132")
                jsonObjectData.put("ifscCode", "12345678912")
                jsonObjectData.put("gender", "")
                jsonObjectData.put("dob", "")
                jsonObjectData.put("address", addressClientData)


                jsonObjectPartnerData.put("id", "")
                jsonObjectPartnerData.put("name", "Akhilesh")
                jsonObjectPartnerData.put("emailId", "partner@nivesh.com")
                jsonObjectPartnerData.put("phoneNo", "8668619074")
                jsonObjectPartnerData.put("pan", "BNXPC3905P")
                jsonObjectPartnerData.put("accountNo", "2738129183334466")
                jsonObjectPartnerData.put("ifscCode", "IGH34567891")
                jsonObjectPartnerData.put("gender", "")
                jsonObjectPartnerData.put("dob", "")
                jsonObjectPartnerData.put("arn", "")
                jsonObjectPartnerData.put("euin", "")
                jsonObjectPartnerData.put("address", addressPartnerData)

//            jsonObject.put("userRefNo", "100")
                jsonObject.put("workFlowID", "10")
                jsonObject.put("niveshClientcode", "")
                jsonObject.put("niveshPartnercode", "")
                jsonObject.put("isClinetInitaited", false)
                jsonObject.put("clientData", jsonObjectData)
                jsonObject.put("partnerData", jsonObjectPartnerData)

                GoldModule().initiateTransaction(this@MainActivity, jsonObject, BUY_GOALD, this)


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        btnWebView.setOnClickListener {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("niveshClientcode","1014000002")
                jsonObject.put("ProductId",10)
                GoldModule().getProductInvestment(this@MainActivity,jsonObject,PRODUCT_INVESTMENT,this)

//                NiveshActivity().refreshToken(this, this, 101)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        IntentFilter().apply {
            addAction("com.providential.nivesh.passdata")
            registerReceiver(customBroadcastReceiver, this)
        }
    }

    fun showHideProgress(isShow: Boolean, context: Context) {
//        val progressDialog = ProgressDialog(context)
        progressDialog?.setTitle("Please wait")
        progressDialog?.setMessage("Application is loading, please wait")
        if (isShow) {
            progressDialog?.show()
        } else {
            progressDialog?.dismiss()
        }
    }

    override fun onLoad(context: Context, isLoading: Boolean, flag: Int) {
        try {
            showHideProgress(isLoading, context)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onSuccess(context: Context, jsonObject: JsonObject, flag: Int) {
        Utils.showLog("MainActivity", "Product Investment Response: $jsonObject")
        Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(context: Context, message: String, flag: Int) {
        Utils.showLog("MainActivity", "Product Investment Error: $message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(customBroadcastReceiver)
    }

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
        Toast.makeText(this,tokenId,Toast.LENGTH_SHORT).show()
    }

    override fun onErrorRefreshToken(context: Context, message: String, flag: Int) {

    }
}