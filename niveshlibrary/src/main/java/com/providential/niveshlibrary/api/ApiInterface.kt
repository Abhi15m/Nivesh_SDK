package com.providential.niveshlibrary.api

import com.google.gson.JsonObject
import com.providential.niveshlibrary.exception.ResultCall
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Constants
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

interface ApiInterface {

    @POST(Constants.GET_TOKEN)
    suspend fun getTokenAsync(@Body requestBody: RequestBody): Result<GetTokenResponseModel>


    @POST(Constants.GET_PRODUCT_INVESTMENT)
    suspend fun getProductInvestment(@Header("authorization") token:String, @Body requestBody: RequestBody):
            Result<JsonObject>

    @POST(Constants.GET_REFRESH_TOKEN)
    suspend fun getRefreshToken(@Body requestBody: RequestBody): Result<JsonObject>
}