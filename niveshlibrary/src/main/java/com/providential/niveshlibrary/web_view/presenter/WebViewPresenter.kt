import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.PopupWindow

interface WebViewPresenter {

    fun validateUrl(url: String)
    fun onBackPressed(webView: WebView)
    fun onReceivedTitle(title: String, url: String)
    fun onClick(resId: Int, url: String, popupWindow: PopupWindow)
    fun onLongClick(result: HitTestResult)
    fun onProgressChanged(progress: Int)
}