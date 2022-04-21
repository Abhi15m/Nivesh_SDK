package com.providential.niveshlibrary.api

import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Constants
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiInterface {

    @POST(Constants.GET_TOKEN)
    suspend fun getTokenAsync(@Body requestBody: RequestBody): Result<GetTokenResponseModel>


    @POST(Constants.GET_PRODUCT_INVESTMENT)
    suspend fun getProductInvestment(@HeaderMap headerMap: Map<String,String>, @Body requestBody: RequestBody): Result<ResponseBody>
}