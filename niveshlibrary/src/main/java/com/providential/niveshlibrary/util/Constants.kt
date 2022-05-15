package com.providential.niveshlibrary.util

import android.content.Context
import com.providential.niveshlibrary.gold_module.gold_interface.GoldListener

object Constants {
//    const val BASE_URL = "https://d2b3v7jl57kdyw.cloudfront.net/auth/v1/"
    const val BASE_URL = "https://sdkapi-sandbox.nivesh.com/"

    //Api
    const val GET_TOKEN = "auth/v1/login"
    const val GET_PRODUCT_INVESTMENT = "common/v1/getProductInvestment"
    const val GET_REFRESH_TOKEN = "auth/v1/getfreshtoken"

    const val INITIATE_GOLD_INVESTMENT = 10101
    var THEME_COLOR : String = ""
    var COLORS : String = ""
}