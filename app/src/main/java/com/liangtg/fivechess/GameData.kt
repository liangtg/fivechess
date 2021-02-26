package com.liangtg.fivechess

import android.content.Context
import android.content.SharedPreferences

object GameData {

    const val KEY_WIN = "key_win"
    const val KEY_LOSE = "key_lose"
    const val KEY_DRAW = "key_draw"
    val userPlayer: Player = Player("user", 0, 0, 0, 0)
    lateinit var sp: SharedPreferences
    fun loadData(context: Context) {
        sp = context.getSharedPreferences(userPlayer.name, Context.MODE_PRIVATE)
        userPlayer.win = sp.getInt(KEY_WIN, 0)
        userPlayer.lose = sp.getInt(KEY_LOSE, 0)
        userPlayer.draw = sp.getInt(KEY_DRAW, 0)
    }

    fun saveUser(player: Player) {
        userPlayer.win = player.win
        userPlayer.lose = player.lose
        userPlayer.draw = player.draw
        sp.edit().putInt(KEY_WIN, player.win).putInt(KEY_LOSE, player.lose)
            .putInt(KEY_DRAW, player.draw).apply()
    }
}