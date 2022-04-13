package com.providential.niveshlibrary.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.providential.niveshlibrary.NiveshApplication
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.di.NetworkModule
import com.providential.niveshlibrary.util.Utils
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class NiveshActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nivesh)


        applicationContext.packageManager.getApplicationInfo(applicationContext.packageName,PackageManager.GET_META_DATA).apply {
            val value = metaData.getString("providential.clientID")!!
            Utils.showLog("Main","value - $value")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getToken(){
        var response: String = ""
        var value =""

        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        val jsonObjectPartnerData = JSONObject()

        jsonObjectData.put("name", "XYX")
        jsonObjectData.put("emailId", "client@nivesh.com")
        jsonObjectData.put("phoneNo", "+918668619073")
        jsonObjectData.put("pan", "XXXXXXXXXXXXX")

        jsonObjectPartnerData.put("name", "Akhilesh")
        jsonObjectPartnerData.put("emailId", "partner@nivesh.com")
        jsonObjectPartnerData.put("phoneNo", "+918668619073")
        jsonObjectPartnerData.put("pan", "XXXXXXXXXXXXX")

        jsonObject.put("userRefNo", "100")
        jsonObject.put("workFlowID", "10")
        jsonObject.put("clientID", "15pmghs05glcnhjhs8g6oprqu9")
        jsonObject.put("clientID", value)
        jsonObject.put("clientData", jsonObjectData)
        jsonObject.put("partnerData", jsonObjectPartnerData)


        Utils.showLog("GetQuestion", "-->$jsonObject")
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            GlobalScope.launch(Dispatchers.Main) {
                NetworkModule.provideRetrofit().getTokenAsync(body)
                    .onSuccess { response1 ->
                        try {
                            response = response1.string()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    .onFailure {
                        response = it.localizedMessage!!
                    }
            }
        }
}