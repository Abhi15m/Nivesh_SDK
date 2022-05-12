package com.providential.nivesh_sdk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class CustomBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let {

            val value: String? = intent?.getStringExtra("goldData")
            Toast.makeText(context,value,Toast.LENGTH_SHORT).show()
        }
    }
}