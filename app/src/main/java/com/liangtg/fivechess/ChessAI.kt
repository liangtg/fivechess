package com.liangtg.fivechess

import android.graphics.Point

class ChessAI(val game: Game) {
    var blackWeight = Array(game.gameWidth) { IntArray(game.gameWidth) }
    var whiteWeight = Array(game.gameWidth) { IntArray(game.gameWidth) }
    var maxBlack = 0
    var maxWhite = 0

    fun reset() {
        blackWeight = Array(game.gameWidth) { IntArray(game.gameWidth) }
        whiteWeight = Array(game.gameWidth) { IntArray(game.gameWidth) }
    }

    fun addBlack(x: Int, y: Int) {
        addWeight(blackWeight, x, y, Game.TYPE_BLACK)
    }

    fun addWhite(x: Int, y: Int) {
        addWeight(whiteWeight, x, y, Game.TYPE_WHITE)
    }

    fun rollBlack(x: Int, y: Int) {
        rollWeight(blackWeight, x, y, Game.TYPE_BLACK)
    }

    fun rollWhite(x: Int, y: Int) {
        rollWeight(whiteWeight, x, y, Game.TYPE_WHITE)
    }

    fun getOut(): Point {
        maxWhite = whiteWeight[0][0]
        maxBlack = blackWeight[0][0]
        val bp = Point(0, 0)
        val wp = Point(0, 0)
        for (x in 0 until game.gameWidth) {
            for (y in 0 until game.gameWidth) {
                if (game.gameMap[x][y] != 0) continue
                if (whiteWeight[x][y] > maxWhite) {
                    wp.set(x, y)
                    maxWhite = whiteWeight[x][y]
                }
                if (blackWeight[x][y] > maxBlack) {
                    maxBlack = blackWeight[x][y]
                    bp.set(x, y)
                }
            }
        }
        return if (maxBlack >= maxWhite) bp else wp
    }

    private fun rollWeight(map: Array<IntArray>, x: Int, y: Int, type: Int) {
        map[x][y] += 10 * 1000
        val stepX = intArrayOf(-1, 0, 1, 1, 1, 0, -1, -1)
        val stepY = intArrayOf(-1, -1, -1, 0, 1, 1, 1, 0)
        for (i in 0..7) {
            var weight = -5
            for (j in 1..4) {
                val tx = x + stepX[i] * j
                val ty = y + stepY[i] * j
                if (tx < 0 || tx >= game.gameWidth || ty < 0 || ty >= game.gameWidth) break
                if (game.gameMap[tx][ty] == 0) {
                    map[tx][ty] += weight
                    weight++
                } else if (game.gameMap[tx][ty] == type) {
                    weight -= 2
                } else {
                    break
                }

            }
        }

    }

    private fun addWeight(map: Array<IntArray>, x: Int, y: Int, type: Int) {
        map[x][y] -= 10 * 1000
        val stepX = intArrayOf(-1, 0, 1, 1, 1, 0, -1, -1)
        val stepY = intArrayOf(-1, -1, -1, 0, 1, 1, 1, 0)
        for (i in 0..7) {
            var weight = 5
            for (j in 1..4) {
                val tx = x + stepX[i] * j
                val ty = y + stepY[i] * j
                if (tx < 0 || tx >= game.gameWidth || ty < 0 || ty >= game.gameWidth) break
                if (game.gameMap[tx][ty] == 0) {
                    map[tx][ty] += weight
                    weight--
                } else if (game.gameMap[tx][ty] == type) {
                    weight += 2
                } else {
                    break
                }

            }
        }

    }

}