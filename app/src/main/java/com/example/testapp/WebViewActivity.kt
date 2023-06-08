package com.example.testapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.testapp.databinding.ActivityWebViewBinding

@SuppressLint("SetJavaScriptEnabled")
class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(LINK, Context.MODE_PRIVATE)
        setContentView(binding.root)
        supportActionBar?.hide()
        settingsWebView()
        val fireBaseLink = preferences.getString(URL, "")
        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        }else{
            binding.webView.loadUrl(fireBaseLink.toString())
        }
    }


    private fun settingsWebView() {


        val cookieManager = CookieManager.getInstance()
        val webView: WebView = binding.webView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
            }
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(false)
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        cookieManager.setAcceptCookie(true)


    }

    override fun onStop() {
        super.onStop()
        CookieManager.getInstance().flush()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        }
    }

    companion object {
        const val LINK = "link_from_firebase"
        const val URL = "url"
    }
}