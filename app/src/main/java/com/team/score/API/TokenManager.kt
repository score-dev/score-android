package com.team.score.API

import android.content.Context
import android.content.SharedPreferences

class TokenManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)

    fun saveUserId(id: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("userId", id)
        editor.apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("userId", -1)
    }

    fun saveEnter(isFirst: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirst", isFirst)
        editor.apply()
    }

    fun getEnter(): Boolean {
        return sharedPreferences.getBoolean("isFirst", true)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.putString("refresh_token", refreshToken)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    // Access 토큰 삭제
    fun deleteAccessToken() {
        val sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("access_token").commit()
        editor.apply()
    }

    // Refresh 토큰 삭제
    fun deleteRefreshToken() {
        val sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("refresh_token")
        editor.apply()
    }
}