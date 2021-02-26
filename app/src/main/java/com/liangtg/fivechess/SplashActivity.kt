package com.liangtg.fivechess

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        LoadTask().execute()
    }

    inner class LoadTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            var time = SystemClock.currentThreadTimeMillis()
            GameData.loadData(this@SplashActivity)
            time = SystemClock.currentThreadTimeMillis() - time
            try {
                Thread.sleep(Math.max(0, 1000 - time))
            } catch (e: Exception) {
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (isFinishing) return
            window.decorView.postOnAnimation {
                startActivity(
                    Intent(
                        this@SplashActivity,
                        MainActivity::class.java
                    )
                )
                finish()
            }
        }

    }
}