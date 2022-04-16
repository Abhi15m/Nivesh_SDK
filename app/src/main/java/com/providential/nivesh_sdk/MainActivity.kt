package com.providential.nivesh_sdk

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.ui.NiveshActivity
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity(), TokenListener {

    var tokenListener:TokenListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenListener = this
        try {
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
            jsonObject.put("clientData", jsonObjectData)
            jsonObject.put("partnerData", jsonObjectPartnerData)

//           NiveshActivity().getToken(this@MainActivity,jsonObject,this)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onStartedToken(context: Context,isLoading: Boolean) {
        Toast.makeText(context,"isLoading - $isLoading",Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessToken(context: Context,getTokenResponseModel: GetTokenResponseModel) {
        Toast.makeText(context,"success - ${getTokenResponseModel.response.AuthenticationResult.IdToken}",Toast.LENGTH_SHORT).show()
    }

    override fun onFailureToken(context: Context,message: String) {
        Toast.makeText(context,"Failure - $message",Toast.LENGTH_SHORT).show()
    }
}