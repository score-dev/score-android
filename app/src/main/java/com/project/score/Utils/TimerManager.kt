package com.project.score.Utils

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
}



