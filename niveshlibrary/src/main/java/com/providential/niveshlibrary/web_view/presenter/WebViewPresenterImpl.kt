
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.web_view.util.UrlUtils
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URLEncoder

class WebViewPresenterImpl(
    private val context: Context,
    private val view: View
) : WebViewPresenter {

    private fun makeToast(text: CharSequence): Toast =
        Toast.makeText(context, text, Toast.LENGTH_LONG)

    override fun validateUrl(url: String) =
        if (URLUtil.isValidUrl(url)) {
            view.loadUrl(url)
        } else {
            if (!TextUtils.isEmpty(url)) {
                var tempUrl = url
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    tempUrl = "http://$url"
                }
                var host = ""
                try {
                    host = UrlUtils.getHost(tempUrl)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                if (URLUtil.isValidUrl(tempUrl)) {
                    view.loadUrl(tempUrl)
                } else try {
                    tempUrl = "http://www.google.com/search?q=" + URLEncoder.encode(url, "UTF-8")
                    tempUrl = UrlUtils.getHost(tempUrl)
                    view.loadUrl(tempUrl)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    view.showToast(makeToast(context.getString(R.string.message_invalid_url)))
                    view.close()
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            } else {
                view.showToast(makeToast(context.getString(R.string.message_invalid_url)))
                view.close()
            }
        }

    override fun onBackPressed(webView: WebView) =
        when {
            webView.canGoBack() -> view.goBack()
            else -> view.close()
        }

    override fun onReceivedTitle(title: String, url: String) {

    }


    override fun onClick(resId: Int, url: String, popupWindow: PopupWindow) {
    }

    override fun onLongClick(result: HitTestResult) {
//        context.vibrate()

        val type = result.type
        val extra = result.extra ?: return

        when (type) {
            HitTestResult.EMAIL_TYPE -> {
                val items = arrayOf<CharSequence>(
                    context.getString(R.string.send_email),
                    context.getString(R.string.copy_email),
                    context.getString(R.string.copy_link_text)
                )
                AlertDialog.Builder(context)
                    .setTitle(extra)
                    .setItems(items) { _, which ->
                        if (which == 0) {
                            view.openEmail(extra)
                        } else if (which == 1 || which == 2) {
                            view.copyLink(extra)
                            view.showToast(makeToast(context.getString(R.string.message_copy_to_clipboard)))
                        }
                    }
                    .show()
            }
            HitTestResult.GEO_TYPE -> {
                Log.d(TAG, "geo longclicked")
            }
            HitTestResult.SRC_IMAGE_ANCHOR_TYPE, HitTestResult.IMAGE_TYPE -> {
                val items = arrayOf<CharSequence>(
                    context.getString(R.string.copy_link),
                    context.getString(R.string.save_link),
                    context.getString(R.string.save_image),
                    context.getString(R.string.open_image)
                )
                AlertDialog.Builder(context)
                    .setTitle(extra)
                    .setItems(items) { _, which ->
                        when (which) {
                            0 ->
                                view.run {
                                    copyLink(extra)
                                    showToast(makeToast(context.getString(R.string.message_copy_to_clipboard)))
                                }
                            1 -> view.onDownloadStart(extra)
                            2 -> view.onDownloadStart(extra)
                            3 -> view.openPopup(extra)
                            else -> {
                            }
                        }
                    }
                    .show()
            }
            HitTestResult.PHONE_TYPE, HitTestResult.SRC_ANCHOR_TYPE -> {
                val items = arrayOf<CharSequence>(
                    context.getString(R.string.copy_link),
                    context.getString(R.string.copy_link_text),
                    context.getString(R.string.save_link)
                )
                AlertDialog.Builder(context)
                    .setTitle(extra)
                    .setItems(items) { _, which ->
                        when (which) {
                            0 -> view.run {
                                copyLink(extra)
                                showToast(makeToast(context.getString(R.string.message_copy_to_clipboard)))
                            }
                            1 -> view.run {
                                copyLink(extra)
                                showToast(makeToast(context.getString(R.string.message_copy_to_clipboard)))
                            }
                            2 -> view.onDownloadStart(extra)
                            else -> {
                            }
                        }
                    }
                    .show()
            }
        }
    }

    override fun onProgressChanged(progress: Int) {
        var progress = progress
        if (progress == 100) {
            progress = 0
        }
        view.setProgressBar(progress)
    }

    fun startActivity(intent: Intent) =
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            view.showToast(makeToast(context.getString(R.string.message_activity_not_found)))
        }

    interface View {
        fun loadUrl(url: String)
        fun close()
        fun goBack()
        fun goForward()
        fun onRefresh()
        fun copyLink(url: String)
        fun showToast(toast: Toast)
        fun openBrowser(uri: Uri)
        fun openShare(url: String)
        fun onDownloadStart(url: String)
        fun setProgressBar(progress: Int)
        fun openEmail(email: String)
        fun openPopup(url: String)
    }

    companion object {
        private const val TAG = "WebViewPresenterImpl"
    }
}