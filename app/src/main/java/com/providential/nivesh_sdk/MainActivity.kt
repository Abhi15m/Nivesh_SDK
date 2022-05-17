package com.providential.nivesh_sdk

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.providential.niveshlibrary.gold_module.GoldModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.util.Utils
import org.json.JSONObject

class MainActivity : AppCompatActivity(),GoldListener{

    var tokenListener:TokenListener? = null

    private lateinit var btnTest: Button
    private lateinit var btnWebView: Button
    private val BUY_GOALD : Int = 101
    private val PRODUCT_INVESTMENT : Int = 102
    private val customBroadcastReceiver = CustomBroadcastReceiver()
    var progressDialog:ProgressDialog ?= null


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

            addressClientData.put("AddressLine1","")
            addressClientData.put("AddressLine2","")
            addressClientData.put("State","")
            addressClientData.put("city","")
            addressClientData.put("country","")
            addressClientData.put("pincode","")

            addressPartnerData.put("AddressLine1","")
            addressPartnerData.put("AddressLine2","")
            addressPartnerData.put("State","")
            addressPartnerData.put("city","")
            addressPartnerData.put("country","")
            addressPartnerData.put("pincode","")

            jsonObjectData.put("userid", "XYX")
            jsonObjectData.put("name", "XYX")
            jsonObjectData.put("emailId", "client@nivesh.com")
            jsonObjectData.put("phoneNo", "+918668619073")
            jsonObjectData.put("pan", "XXXXXXXXXXXXX")
            jsonObjectData.put("AccountNo", "XXXXXXXXXXXXX")
            jsonObjectData.put("IfscCode", "XXXXXXXXXXXXX")
            jsonObjectData.put("gender", "XXXXXXXXXXXXX")
            jsonObjectData.put("dob", "XXXXXXXXXXXXX")
            jsonObjectData.put("Address", addressClientData)


            jsonObjectPartnerData.put("id", "")
            jsonObjectPartnerData.put("name", "Akhilesh")
            jsonObjectPartnerData.put("emailId", "partner@nivesh.com")
            jsonObjectPartnerData.put("phoneNo", "+918668619073")
            jsonObjectPartnerData.put("pan", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("arn", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("euin", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("AccountNo", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("IfscCode", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("gender", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("dob", "XXXXXXXXXXXXX")
            jsonObjectPartnerData.put("Address", addressPartnerData)

            jsonObject.put("userRefNo", "100")
            jsonObject.put("workFlowID", "10")
            jsonObject.put("niveshClientcode", "10")
            jsonObject.put("niveshPartnercode", "10")
            jsonObject.put("isClinetInitaited", false)
            jsonObject.put("clientData", jsonObjectData)
            jsonObject.put("partnerData", jsonObjectPartnerData)

           GoldModule().initiateTransaction(this@MainActivity,jsonObject,BUY_GOALD,this)


        }catch (e:Exception){
            e.printStackTrace()
        }
        }


        btnWebView.setOnClickListener {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("clientcode","1014000001")
                jsonObject.put("ProductId",10)
                GoldModule().getProductInvestment(this@MainActivity,jsonObject,PRODUCT_INVESTMENT,this)
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }

        IntentFilter().apply {
            addAction("com.providential.nivesh.passdata")
            registerReceiver(customBroadcastReceiver,this)
        }
    }

    fun showHideProgress(isShow:Boolean,context: Context){
//        val progressDialog = ProgressDialog(context)
        progressDialog?.setTitle("Please wait")
        progressDialog?.setMessage("Application is loading, please wait")
        if (isShow) {
            progressDialog?.show()
        }else{
            progressDialog?.dismiss()
        }
    }

    override fun onLoad(context: Context, isLoading: Boolean, flag: Int) {
        try {
            showHideProgress(isLoading,context)
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    override fun onSuccess(context: Context, jsonObject: JsonObject, flag: Int) {
        Utils.showLog("MainActivity","Product Investment Response: $jsonObject")
        Toast.makeText(context,jsonObject.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(context: Context, message: String, flag: Int) {
        Utils.showLog("MainActivity","Product Investment Error: $message")
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(customBroadcastReceiver)
    }
}