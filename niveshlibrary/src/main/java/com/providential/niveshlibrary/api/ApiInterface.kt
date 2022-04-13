package com.providential.niveshlibrary.api
import com.providential.niveshlibrary.model.GetTokenResponseModel
import com.providential.niveshlibrary.util.Constants
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @POST(Constants.GET_TOKEN)
    suspend fun getTokenAsync(@Body requestBody: RequestBody): Result<ResponseBody>
}