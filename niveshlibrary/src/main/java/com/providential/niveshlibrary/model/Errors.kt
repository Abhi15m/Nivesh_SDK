package com.providential.niveshlibrary.model

import com.google.gson.annotations.SerializedName

data class Errors(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)