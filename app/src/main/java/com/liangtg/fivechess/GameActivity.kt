package com.liangtg.fivechess

import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity(), GameCallback {
    companion object {
        const val KEY_TYPE = "user_type"
        const val KEY_MODE = "game_mode"
        fun startGame(
            context: Context,
            userType: Int = Game.TYPE_BLACK,
            mode: Int = Game.MODE_FIGHT
        ) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(KEY_TYPE, userType)
            intent.putExtra(KEY_MODE, mode)
            context.startActivity(intent)
        }
    }

    private var chessAI: ChessAI? = null

    private val game: Game by lazy {
        Game(
            if (userPlayer.type == Game.TYPE_BLACK) userPlayer else otherPlayer,
            if (userPlayer.type == Game.TYPE_BLACK) otherPlayer else userPlayer, gameWidth
        )
    }

    private val gameView: GameView by lazy {
        findViewById(R.id.gameview)
    }
    private lateinit var userPlayer: Player
    private lateinit var otherPlayer: Player
    private val gameWidth by lazy { resources.getInteger(R.integer.gamewidth) }
    private val curImage: ImageView by lazy { findViewById(R.id.cur_player) }
    private val playTimeText: TextView by lazy { findViewById(R.id.play_time) }
    private var startTime = SystemClock.uptimeMillis()
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (isFinishing) return
            if (!game.gameEnd) {
                val now = (SystemClock.uptimeMillis() - startTime) / 1000
                playTimeText.text = "${now / 60}:${now % 60}"
            }
            sendEmptyMessageDelayed(10, 1000)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        findViewById<View>(R.id.end_game).setOnClickListener { finish() }
        findViewById<View>(R.id.reset_game).setOnClickListener { resetGame() }
        findViewById<View>(R.id.rollback).setOnClickListener { rollback() }
        userPlayer = Player(
            GameData.userPlayer.name,
            intent.getIntExtra(KEY_TYPE, Game.TYPE_BLACK),
            GameData.userPlayer.win,
            GameData.userPlayer.lose,
            GameData.userPlayer.draw
        )
        otherPlayer = Player(
            "",
            if (userPlayer.type == Game.TYPE_BLACK) Game.TYPE_WHITE else Game.TYPE_BLACK,
            0,
            0,
            0
        )

        game.gameCallback = this
        gameView.game = game
        handler.sendEmptyMessage(10)
        if (intent.getIntExtra(KEY_MODE, Game.MODE_FIGHT) == Game.MODE_SINGLE) {
            chessAI = ChessAI(game)
            if (otherPlayer.type == Game.TYPE_BLACK) {
                game.addChess(gameWidth / 2, gameWidth / 2)
            }
        }
        game.setPlayerReady()

    }

    private fun showResultDialog(msg: String) {
        AlertDialog.Builder(this).apply {
            setMessage(msg)
            setPositiveButton("确定", null)
            setNegativeButton("重来") { _, _ -> resetGame() }
        }.show()
    }

    private fun resetGame() {
        startTime = SystemClock.uptimeMillis()
        game.reset()
        gameView.invalidate()
        chessAI?.reset()
        addIfAiFirst()
        updateCurImage()
        game.setPlayerReady()
    }

    private fun addIfAiFirst() {
        chessAI?.let {
            if (otherPlayer.type == Game.TYPE_BLACK) game.addChess(
                game.gameWidth / 2,
                game.gameWidth / 2
            )
        }
    }

    private fun rollback() {
        val ai = chessAI
        val first = game.roolback()
        if (null == ai) {
        } else {
            first?.let {
                if (game.actions.size % 2 == 0) ai.rollBlack(it.x, it.y) else ai.rollWhite(
                    it.x,
                    it.y
                )
            }
            game.roolback()?.let {
                if (game.actions.size % 2 == 0) ai.rollBlack(it.x, it.y) else ai.rollWhite(
                    it.x,
                    it.y
                )
            }
        }
        updateCurImage()
        gameView.invalidate()
        if (game.actions.isEmpty()) addIfAiFirst()
        game.setPlayerReady()
    }

    override fun onChessAdd(x: Int, y: Int) {
        val ai = chessAI
        ai?.run {
            if (game.curPlayer.type == Game.TYPE_BLACK) addWhite(x, y) else addBlack(x, y)
        }
        if (game.gameEnd) {
            gameEnd()
        } else {
            updateCurImage()
            if (null == ai) {
                game.setPlayerReady()
            } else {
                game.setPlayerReady()
                if (game.curPlayer == otherPlayer) {
                    val out = ai.getOut()
                    if (game.canAdd(out.x, out.y)) {
                        game.addChess(out.x, out.y)
                    } else {
                        //计算出错
                        Log.e("ai", "error add $x $y out: $out")
                        game.gameEnd = true
                        game.gameDraw = true
                        gameEnd()
                    }
                }

            }
        }
    }

    private fun updateCurImage() {
        curImage.setImageResource(if (game.curPlayer.type == Game.TYPE_BLACK) R.drawable.black else R.drawable.white)
    }

    private fun gameEnd() {
        if (game.gameDraw) {
            userPlayer.draw++
            showResultDialog("平局")
        } else {
            if (game.curPlayer.type == userPlayer.type) {
                userPlayer.win++
            } else {
                userPlayer.lose++
            }
            showResultDialog(if (game.curPlayer.type == Game.TYPE_BLACK) "黑棋胜" else "白棋胜")
        }
        GameData.saveUser(userPlayer)
    }
}