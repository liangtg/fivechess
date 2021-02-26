package com.liangtg.fivechess

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        title = "帮助"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val webview: WebView = findViewById(R.id.webview)
        webview.loadUrl("file:android_asset/index.html")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}