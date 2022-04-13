package com.providential.niveshlibrary.web_view.util

import java.net.MalformedURLException
import java.net.URL

object UrlUtils {

    @Throws(MalformedURLException::class)
    fun getHost(url: String?): String = URL(url).host
}