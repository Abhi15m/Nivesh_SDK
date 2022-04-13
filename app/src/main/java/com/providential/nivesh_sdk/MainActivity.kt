package com.providential.nivesh_sdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.providential.niveshlibrary.ui.NiveshActivity
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NiveshActivity().getToken()
//        Toast.makeText(this@MainActivity,"toke - $token",Toast.LENGTH_SHORT).show()
    }
}