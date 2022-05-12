package com.providential.niveshlibrary.interfaces

import org.json.JSONObject

interface IPreferenceHelper {

    fun setTokenRequest(tokenRequest:String)
    fun getTokenRequest():String
    fun setTokenId(tokenId: String)
    fun getTokenId(): String
    fun setApiKey(apikey: String)
    fun getApiKey(): String

    fun setLoginDetails(loginDetails:String)
    fun getLoginDetails():String


    fun clearPrefs()
}