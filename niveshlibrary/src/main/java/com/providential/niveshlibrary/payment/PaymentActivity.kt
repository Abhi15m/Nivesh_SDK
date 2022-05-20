package com.providential.niveshlibrary.payment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.util.Constants.THEME_COLOR
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.web_view.ui.WebViewActivity
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener{

    private lateinit var mContext: Context
    var preferenceHelper: IPreferenceHelper ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        preferenceHelper = PreferenceManager(this)
        startPayment()
    }

    private fun startPayment() {
//        this.mContext = context

        try {
            val data = intent.getStringExtra("payment_data")
           val obj = JSONObject(data.toString())
            val co = Checkout()
            co.setKeyID(obj.getString("Key"))
//            co.setKeyID("rzp_test_bcrt5VZSda3cRi")
            val options = JSONObject()
            options.put("currency", "INR")
            options.put("name", preferenceHelper?.getAppName())
            options.put("description", "")
            options.put("image", "")
//            options.put("order_id", "order_JRr1ntilgNPGMH")
            options.put("order_id", obj.getString("Id"))
//            options.put("amount", jsonObject.getString("amount"))
//            options.put("amount", jsonObject.getString("amount"))
            options.put("send_sms_hash", false)
            val retryObj = JSONObject()
            retryObj.put("enabled", false)
            retryObj.put("max_count", 1)
            options.put("retry", retryObj)
            val notes = JSONObject()
            notes.put("address", "")
            options.put("notes", notes)
            val themes = JSONObject()
            themes.put("color", "#$THEME_COLOR")
            options.put("theme", themes)
            val preFill = JSONObject()
            preFill.put("email", "abhinav.kumar@nivesh.com")
            preFill.put("contact", "8073895204")
            options.put("prefill", preFill)
            co.open(this@PaymentActivity, options)
//            co.open(mContext as Activity?, options)
        } catch (e: Exception) {
            Toast.makeText(this@PaymentActivity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try{
            val jsonObject = JSONObject()
            jsonObject.put("razorpay_payment_id",p1?.paymentId)
            jsonObject.put("razorpay_order_id",p1?.orderId)
            jsonObject.put("razorpay_signature",p1?.signature)
            jsonObject.put("description","")
            jsonObject.put("status_code","200")
            p1?.let { sendData(200,jsonObject.toString())}
        }catch (ex:Exception){
            ex.printStackTrace()
            p1?.let { sendData(200,p0!!)}
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try{
            val jsonObject1 = p1?.let { JSONObject(it) }
            val jsonObject = JSONObject()
            jsonObject.put("razorpay_payment_id",jsonObject1?.getJSONObject("error")?.getJSONObject("metadata")?.getString("payment_id"))
            jsonObject.put("razorpay_order_id",jsonObject1?.getJSONObject("error")?.getJSONObject("metadata")?.getString("order_id"))
            jsonObject.put("razorpay_signature","")
            jsonObject.put("description",jsonObject1?.getJSONObject("error")?.getString("description"))
            jsonObject.put("status_code","400")
            p1?.let { sendData(400,jsonObject.toString())}
        }catch (ex:Exception){
            ex.printStackTrace()
            p1?.let { sendData(400,p1)}
        }
    }

    private fun sendData(status: Int,mData:String){
        runOnUiThread(Runnable {
            try {
                val str : String = mData
                val intent = Intent(this,WebViewActivity::class.java)
                intent.putExtra("payment_status", str)
                intent.putExtra("status_code", status)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
                finish()
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        })

    }

}
