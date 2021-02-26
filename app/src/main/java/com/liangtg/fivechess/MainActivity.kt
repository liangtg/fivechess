package com.liangtg.fivechess

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val userInfo: TextView by lazy { findViewById(R.id.user_info) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.fight_mode).setOnClickListener { startGame(Game.MODE_FIGHT) }
        findViewById<View>(R.id.single_mode).setOnClickListener { startGame(Game.MODE_SINGLE) }
        findViewById<View>(R.id.help).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HelpActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.exit_app).setOnClickListener { finish() }
    }

    private fun startGame(mode: Int) {
        AlertDialog.Builder(this).apply {
            setItems(arrayOf("黑棋", "白棋")) { _, i ->
                GameActivity.startGame(
                    this@MainActivity,
                    if (i == 0) Game.TYPE_BLACK else Game.TYPE_WHITE,
                    mode
                )
            }
            setCancelable(true)
        }.show()
    }

    override fun onResume() {
        super.onResume()
        val user = GameData.userPlayer
        userInfo.text = "胜 ${user.win} 负 ${user.lose} 平 ${user.draw}"
    }

    override fun onClick(v: View?) {
    }
}