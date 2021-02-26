package com.liangtg.fivechess

import android.graphics.Point
import java.util.*

data class Player(val name: String, val type: Int, var win: Int, var lose: Int, var draw: Int)
interface GameCallback {
    fun onChessAdd(x: Int, y: Int)
}

class Game(val blackPlayer: Player, val whitePlayer: Player, val gameWidth: Int) {

    companion object {
        const val MODE_SINGLE = 0
        const val MODE_FIGHT = 1
        const val TYPE_BLACK = 1
        const val TYPE_WHITE = 2
    }

    var gameCallback: GameCallback? = null

    var gameMap: Array<IntArray> = Array(gameWidth) { IntArray(gameWidth) }
    val actions: LinkedList<Point> = LinkedList()
    var curPlayer: Player = blackPlayer
    var gameEnd = false
    var gameDraw = false
    private var playerReady = false

    fun roolback(): Point? {
        return actions.pollLast()?.also {
            gameMap[it.x][it.y] = 0
            if (!gameEnd) switchPlayer()
            gameEnd = false
            gameDraw = false
        }
    }

    private fun switchPlayer() {
        curPlayer = if (curPlayer == blackPlayer) whitePlayer else blackPlayer
    }

    fun canAdd(x: Int, y: Int): Boolean {
        if (!playerReady || x >= gameMap.size || y >= gameMap[x].size) return false
        return gameMap[x][y] == 0
    }

    var tmpPoint: Point? = null
    fun addTmpPoint(x: Int, y: Int) {
        tmpPoint = tmpPoint?.also { it.set(x, y) } ?: Point(x, y)
    }

    fun removeTmpPoint() {
        tmpPoint = null
    }

    fun addChess(x: Int, y: Int) {
        gameMap[x][y] = curPlayer.type
        actions.add(Point(x, y))
        gameDraw = actions.size == gameWidth * gameWidth
        gameEnd = if (gameDraw) true else isGameEnd(x, y)
        playerReady = false
        if (!gameEnd) switchPlayer()
        gameCallback?.onChessAdd(x, y)
    }

    fun setPlayerReady() {
        playerReady = true
    }

    fun reset() {
        gameMap = Array(gameWidth) { IntArray(gameWidth) }
        actions.clear()
        curPlayer = blackPlayer
        gameEnd = false
        gameDraw = false
        playerReady = false
        tmpPoint = null
    }

    private fun isGameEnd(x: Int, y: Int): Boolean {
        var left = if (x < 4) 0 else (x - 4)
        var right = if (x + 4 > gameWidth - 1) (gameWidth - 1) else (x + 4)
        var top = if (y < 4) 0 else y - 4
        var bottom = if (y + 4 > gameWidth - 1) (gameWidth - 1) else (y + 4);
        var count = 0;
        val type = curPlayer.type
        for (i in left..right) {//横向
            if (gameMap[i][y] == type) {
                count++
                if (count >= 5) return true
            } else {
                count = 0
            }
        }
        count = 0
        for (i in top..bottom) {//竖向
            if (gameMap[x][i] == type) {
                count++
                if (count >= 5) return true
            } else {
                count = 0
            }
        }
        count = 0
        var ty = top
        for (i in left..right) { //左斜
            if (gameMap[i][ty] == type) {
                count++
                if (count >= 5) return true
            } else {
                count = 0
            }
            ty++
            if (ty > bottom) return false
        }

        count = 0
        ty = bottom
        for (i in left..right) { //右斜
            if (gameMap[i][ty] == type) {
                count++
                if (count >= 5) return true
            } else {
                count = 0
            }
            ty--
            if (ty < top) return false
        }

        return false
    }

}