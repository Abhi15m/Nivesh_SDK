package com.providential.niveshlibrary.util

import android.content.Context
import android.content.SharedPreferences
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject

open class PreferenceManager constructor(context: Context) : IPreferenceHelper {
    private val PREFS_NAME = "SharedPreferenceDemo"
    private var preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val TOKEN_REQUEST = "token_request"
        const val TOKEN_ID = "token_id_"
        const val API_KEY = "api_key"
        const val REFRESH_TOKEN = "_refreshtoken"
        const val TOKEN_TYPE = "_token_type"
        const val CLIENT_CODE = "_client_code"
        const val PARENT_CODE = "_parent_code"
        const val LOGIN_DETAILS = "_login_details"
    }

    override fun setTokenRequest(tokenRequest:String) {
        preferences[TOKEN_REQUEST] = tokenRequest
    }

    override fun getTokenRequest(): String {
        return preferences[TOKEN_REQUEST] ?: ""
    }

    override fun setApiKey(apikey:String) {
        preferences[API_KEY] = apikey
    }

    override fun getApiKey(): String {
        return preferences[API_KEY] ?: ""
    }

    override fun setRefreshToken(refreshToken: String) {
        preferences[REFRESH_TOKEN] = refreshToken
    }

    override fun getRefreshToken(): String {
        return preferences[REFRESH_TOKEN] ?: ""
    }

    override fun setTokenId(tokenId: String) {
        preferences[TOKEN_ID] = tokenId
    }
    override fun getTokenId(): String {
        return preferences[TOKEN_ID] ?: ""
    }

    override fun setLoginDetails(loginDetails: String) {
        preferences[LOGIN_DETAILS] = loginDetails
    }

    override fun getLoginDetails(): String {
        return preferences[LOGIN_DETAILS] ?: ""
    }



    override fun clearPrefs() {
        preferences.edit().clear().apply()
    }
}



/**
 * SharedPreferences extension function, to listen the edit() and apply() fun calls
 * on every SharedPreferences operation.
 */
private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}
/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
private operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}
/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
private inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}