package com.team.score.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun setFCMToken(token: String) {
        preferences.edit().putString("FCM_TOKEN", token).apply()
    }

    fun getFCMToken(): String? =
        preferences.getString("FCM_TOKEN", null)

    // 그룹 검색어 저장
    fun saveRecentSearchLimited(context: Context, keyword: String) {
        val prefs = context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val existing = prefs.getString("keywords", "") ?: ""
        val list = existing.split(",").filter { it.isNotBlank() }.toMutableList()

        list.remove(keyword)
        list.add(0, keyword)

        if (list.size > 10) list.removeLast()

        prefs.edit().putString("keywords", list.joinToString(",")).apply()
    }

    fun getRecentSearchesLimited(context: Context): List<String> {
        val prefs = context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val data = prefs.getString("keywords", "") ?: ""
        return data.split(",").filter { it.isNotBlank() }
    }


    fun removeRecentSearch(context: Context, keyword: String) {
        val prefs = context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val existing = prefs.getString("keywords", "") ?: ""
        val list = existing.split(",").filter { it.isNotBlank() }.toMutableList()

        list.remove(keyword)

        prefs.edit().putString("keywords", list.joinToString(",")).apply()
    }


}