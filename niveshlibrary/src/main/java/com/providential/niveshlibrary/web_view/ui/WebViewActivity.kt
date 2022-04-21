package com.providential.niveshlibrary.web_view.ui

import WebViewPresenterImpl
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ContextMenu
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.providential.niveshlibrary.R
import com.providential.niveshlibrary.interfaces.IPreferenceHelper
import com.providential.niveshlibrary.util.PreferenceManager
import com.providential.niveshlibrary.web_view.util.ClipboardUtils
import com.providential.niveshlibrary.web_view.util.FileUtils
import com.providential.niveshlibrary.web_view.util.PermissionUtils


class WebViewActivity : AppCompatActivity(),
    WebViewPresenterImpl.View,
    View.OnClickListener,
    DownloadListener,
    View.OnCreateContextMenuListener {

    private val presenter by lazy { WebViewPresenterImpl(this, this) }

    private var url = "https://www.nivesh.com/en"
//    private var url = "https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_win_localstorage"
    private var filePathCallbackLollipop: ValueCallback<Array<Uri>>? = null
    private var filePathCallbackNormal: ValueCallback<Uri>? = null
    private var downloadManager: DownloadManager? = null
    private var mLastDownloadId: Long = 0
    private val coordinatorlayout by lazy {
        findViewById<CoordinatorLayout>(R.id.a_web_viewer_coordinatorlayout)
    }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }
    private val webView by lazy { findViewById<WebView>(R.id.web_view) }

    val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web_view)
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            .apply {
                addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
            }.let {
                registerReceiver(downloadReceiver, it)
            }

        bindView()
        presenter.validateUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val result = webView.hitTestResult
        presenter.onLongClick(result)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXTRA_URL, url)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        url = savedInstanceState.getString(EXTRA_URL) ?: ""
        super.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (RESULT_OK == resultCode) {
            when (requestCode) {
                REQUEST_FILE_CHOOSER -> {
                    if (filePathCallbackNormal == null) {
                        return
                    }
                    val result = data?.data
                    filePathCallbackNormal!!.onReceiveValue(result)
                    filePathCallbackNormal = null
                }
                REQUEST_FILE_CHOOSER_FOR_LOLLIPOP -> {
                    if (filePathCallbackLollipop == null) {
                        return
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        filePathCallbackLollipop!!.onReceiveValue(
                            WebChromeClient.FileChooserParams.parseResult(
                                resultCode,
                                data
                            )
                        )
                    }
                    filePathCallbackLollipop = null
                }
                REQUEST_PERMISSION_SETTING -> {
                    if (downloadUrl != null && downloadMimetype != null) {
                        mLastDownloadId =
                            FileUtils.downloadFile(this, downloadUrl!!, downloadMimetype!!)
                    }
                }
            }
        } else {
            when (requestCode) {
                REQUEST_PERMISSION_SETTING -> {
                    Toast.makeText(
                        this,
                        R.string.write_permission_denied_message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun bindView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    displayZoomControls = false
                }
                builtInZoomControls = true
                setSupportZoom(true)
                domStorageEnabled = true
            }

            webChromeClient = MyWebChromeClient()
            webViewClient = MyWebViewClient()
            setDownloadListener(this@WebViewActivity)
        }
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
        presenter.onReceivedTitle("", this.url)
    }

    override fun close() {
        finish()
    }

    override fun goBack() {
        webView.goBack()
    }

    override fun goForward() {
        webView.goForward()
    }

    override fun copyLink(url: String) {
        ClipboardUtils.copyText(this, url)
    }

    override fun showToast(toast: Toast) {
        toast.show()
    }

    override fun openBrowser(uri: Uri) {
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun openShare(url: String) {
        Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_TEXT, webView.url)
            .setType("text/plain")
            .let {
                startActivity(Intent.createChooser(it, resources.getString(R.string.menu_share)))
            }
    }

    override fun onDownloadStart(url: String) {
        onDownloadStart(url, null, null, "image/jpeg", 0)
    }

    override fun setProgressBar(progress: Int) {
        progressBar.progress = progress
    }

    override fun openEmail(email: String) {
        Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
            .let {
                startActivity(Intent.createChooser(it, getString(R.string.email)))
            }
    }

    override fun openPopup(url: String) {
        Intent(this, WebViewActivity::class.java)
            .putExtra(EXTRA_URL, url)
            .let {
                startActivity(it)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if (downloadUrl != null && downloadMimetype != null) {
                    mLastDownloadId =
                        FileUtils.downloadFile(this, downloadUrl!!, downloadMimetype!!)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        AlertDialog.Builder(this@WebViewActivity)
                            .setTitle(R.string.write_permission_denied_title)
                            .setMessage(R.string.write_permission_denied_message)
                            .setNegativeButton(R.string.dialog_dismiss, null)
                            .setPositiveButton(R.string.dialog_settings) { dialog, which ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private var downloadUrl: String? = null
    private var downloadMimetype: String? = null

    override fun onDownloadStart(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String,
        contentLength: Long
    ) {
        if (downloadManager == null) {
            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        }
        downloadUrl = url
        downloadMimetype = mimeType
        val hasPermission = PermissionUtils.hasPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            PermissionUtils.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
        )
        if (hasPermission) {
            mLastDownloadId = FileUtils.downloadFile(this, url, mimeType)
        }
    }

    override fun onRefresh() {
        webView.reload()
    }

    inner class MyWebChromeClient : WebChromeClient() {
        // For Android 3.0+
        // For Android < 3.0
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
            filePathCallbackNormal = uploadMsg

            Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
                .let {
                    startActivityForResult(
                        Intent.createChooser(it, getString(R.string.select_image)),
                        REQUEST_FILE_CHOOSER
                    )
                }
        }

        // For Android 5.0+
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop!!.onReceiveValue(null)
                filePathCallbackLollipop = null
            }
            filePathCallbackLollipop = filePathCallback
            Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
                .let {
                    startActivityForResult(
                        Intent.createChooser(it, getString(R.string.select_image)),
                        REQUEST_FILE_CHOOSER_FOR_LOLLIPOP
                    )
                }

            return true
        }

        override fun onJsAlert(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            AlertDialog.Builder(this@WebViewActivity)
                .setMessage(message)
                .setPositiveButton(R.string.yes) { _, _ -> result.confirm() }
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }.run {
                    show()
                }
            return true
        }

        override fun onJsConfirm(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            AlertDialog.Builder(this@WebViewActivity)
                .setMessage(message)
                .setPositiveButton(R.string.yes) { _, _ -> result.confirm() }
                .setNegativeButton(R.string.no) { _, _ -> result.cancel() }
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }.run {
                    show()
                }
            return true
        }

        override fun onProgressChanged(view: WebView, progress: Int) {
            presenter.onProgressChanged(progress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            presenter.onReceivedTitle(title, view.url ?: "")
        }
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            val view = view ?: return
            val url = url ?: return

            presenter.onReceivedTitle(view.title ?: "", url)
            progressBar.visibility = View.GONE
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                localStorage(view!!)
            }

            progressBar.visibility = View.VISIBLE
        }

        private fun localStorage(webView: WebView) {
            val key = "token"
            val `val`: String = preferenceHelper.getTokenId()
            val key2 = "is_app"
            val val2 = "101"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("window.localStorage.setItem('$key','$`val`');", null)
                webView.evaluateJavascript("window.localStorage.setItem('$key2','$val2');", null)
            } else {
                webView.loadUrl("javascript:localStorage.setItem('$key','$`val`');")
                webView.loadUrl("javascript:localStorage.setItem('$key2','$val2');")
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val view = view ?: return false
            val url = url ?: return false

            return when {
                url.endsWith(".mp4") -> {
                    Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.parse(url), "video/*")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .let {
                            view.context.startActivity(it)
                        }
                    true
                }
                url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:")
                        || url.startsWith("mms:") || url.startsWith("mmsto:") || url.startsWith("mailto:") -> {

                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .let {
                            view.context.startActivity(it)
                        }
                    true
                }
                else -> super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed(webView)
    }

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                if (downloadManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        val downloadedUri =
                            downloadManager!!.getUriForDownloadedFile(mLastDownloadId)
                        val mimeType =
                            downloadManager!!.getMimeTypeForDownloadedFile(mLastDownloadId)
                        NotifyDownloadedTask().execute(downloadedUri.toString(), mimeType)
                    }
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED == action) {
                val notiIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
                notiIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(notiIntent)
            }
        }
    }

    private inner class NotifyDownloadedTask : AsyncTask<String?, Void?, Array<String?>?>() {

        @SuppressLint("Range")
        override fun doInBackground(vararg params: String?): Array<String?>? {
            if (params == null || params.size != 2) {
                return null
            }
            val uriStr = params[0]
            val mimeType = params[1]
            var fileName = ""
            val query = DownloadManager.Query()
            query.setFilterById(mLastDownloadId)
            val c = downloadManager!!.query(query)
            if (c.moveToFirst()) {
                val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                }
            }
            return arrayOf(uriStr, fileName, mimeType)
        }

        override fun onPostExecute(results: Array<String?>?) {
            if (results != null && results.size == 3) {
                val uriStr = results[0]
                val fileName = results[1]
                val mimeType = results[2]
                Snackbar.make(
                    coordinatorlayout,
                    fileName + getString(R.string.downloaded_message),
                    Snackbar.LENGTH_LONG
                ).apply {
                    duration = resources.getInteger(R.integer.snackbar_duration)
                    setAction(getString(R.string.open)) {
                        Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.parse(uriStr), mimeType)
                            .let {
                                presenter.startActivity(it)
                            }
                    }
                }.run {
                    show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "WebViewerActivity"
        private const val REQUEST_FILE_CHOOSER = 0
        private const val REQUEST_FILE_CHOOSER_FOR_LOLLIPOP = 1
        private const val REQUEST_PERMISSION_SETTING = 2
        const val EXTRA_URL = "url"
    }

    override fun onClick(v: View?) {

    }

    private fun sendData(data:String){
        val intent = Intent()
        intent.putExtra("keyName", data)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}