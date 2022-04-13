package com.providential.niveshlibrary.model

data class AuthenticationResult(
    val ExpiresIn: Int,
    val IdToken: String,
    val RefreshToken: String,
    val TokenType: String,
    val clientcode: String,
    val parentcode: String
)