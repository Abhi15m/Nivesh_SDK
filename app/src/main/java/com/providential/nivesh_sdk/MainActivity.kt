package com.providential.nivesh_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.providential.niveshlibrary.gold_module.GoldModule
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener
import com.providential.niveshlibrary.interfaces.TokenListener
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.ui.NiveshActivity
import com.providential.niveshlibrary.util.Utils
import com.providential.niveshlibrary.web_view.ui.WebViewActivity
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity(), TokenListener,GoldListener {

    var tokenListener:TokenListener? = null

    private lateinit var btnTest: Button
    private lateinit var btnWebView: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTest = findViewById(R.id.btnTest)
        btnWebView = findViewById(R.id.btnWebView)


//        tokenListener = this
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

           NiveshActivity().getToken(this@MainActivity,jsonObject,this,0,false)

        }catch (e:Exception){
            e.printStackTrace()
        }

        btnTest.setOnClickListener {
            GoldModule().getProductInvestment(this@MainActivity,this)
        }

        btnWebView.setOnClickListener {
            val intent = Intent(this,WebViewActivity::class.java)
            startForResult.launch(intent)
        }

    }

    override fun onStartedToken(context: Context,isLoading: Boolean,flag:Int) {
        Toast.makeText(context,"isLoading - $isLoading",Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessToken(context: Context,getTokenResponseModel: GetTokenResponseModel,flag:Int) {
        Toast.makeText(context,"success - ${getTokenResponseModel.response.AuthenticationResult.IdToken}",Toast.LENGTH_SHORT).show()
    }

    override fun onFailureToken(context: Context,message: String,flag:Int) {
        Toast.makeText(context,"Failure - $message",Toast.LENGTH_SHORT).show()
    }

    override fun onLoad(context: Context, isLoading: Boolean) {
    }

    override fun onSuccess(context: Context, body: String) {
        Utils.showLog("MainActivity","Product Investment Response: $body")
    }

    override fun onFailure(context: Context, message: String) {
    }


    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            // Handle the Intent
        }
    }
}