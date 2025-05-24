package com.team.score.Utils

import android.content.Context

object TimerManager {
    var startedAtMillis: Long? = null
    var startedAtIso: String? = null

    var completedAtMillis: Long? = null
    var completedAtIso: String? = null

    var isRunning: Boolean = false

    fun reset() {
        startedAtMillis = null
        startedAtIso = null
        completedAtMillis = null
        completedAtIso = null
        isRunning = false
    }

    // TimerManager.kt
    fun save(context: Context) {
        val prefs = context.getSharedPreferences("timer", Context.MODE_PRIVATE)
        prefs.edit().putLong("startedAtMillis", startedAtMillis ?: -1L).apply()
    }

    fun load(context: Context) {
        val prefs = context.getSharedPreferences("timer", Context.MODE_PRIVATE)
        val saved = prefs.getLong("startedAtMillis", -1L)
        startedAtMillis = if (saved != -1L) saved else null
    }

    fun clear(context: Context) {
        context.getSharedPreferences("timer", Context.MODE_PRIVATE).edit().clear().apply()
    }

}



