package com.providential.niveshlibrary.api

import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Constants
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {

    @POST(Constants.GET_TOKEN)
    suspend fun getTokenAsync(@Body requestBody: RequestBody): Result<GetTokenResponseModel>
}