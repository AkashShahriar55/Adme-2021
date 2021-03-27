package com.cookietech.namibia.adme.managers

import android.preference.PreferenceManager
import com.cookietech.namibia.adme.Application.AdmeApplication.Companion.APP_CONTEXT
import com.cookietech.namibia.adme.Application.AppComponent.MODE_CLIENT
import com.cookietech.namibia.adme.Application.AppComponent.MODE_SERVICE_PROVIDER
import java.util.*

object SharedPreferenceManager {
    const val SEARCH_HISTORY = "search_history"
    const val VIEW_MODE = "view_mode"
    private const val CHORD_LIBRARY_UPDATE_DATE = "chord_library_update_date"
    const val USER_INFO_UPDATED_AT_LOGIN = "user_info_updated_at_login"
    const val USER_MODE = "user_mode"
    fun addSharedPrefSearchHistory(keyword: String?) {
        val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
        val editor = myPreference.edit()
        var keyword_list = myPreference.getStringSet(SEARCH_HISTORY, null)
        if (keyword_list == null) {
            keyword_list = HashSet()
        }
        keyword_list.add(keyword)
        editor.putStringSet(SEARCH_HISTORY, keyword_list)
        editor.apply()
    }

    val sharedPrefSearchHistory: Set<String>?
        get() {
            val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
            return myPreference.getStringSet(SEARCH_HISTORY, null)
        }

    fun addSharedPrefViewModel(isDarkModeActivated: Boolean) {
        val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
        val editor = myPreference.edit()
        editor.putBoolean(VIEW_MODE, isDarkModeActivated)
        editor.apply()
    }

    val sharedPrefViewMode: Boolean
        get() {
            val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
            return myPreference.getBoolean(VIEW_MODE, false)
        }
    var sharedPrefChordLibraryUpdateDate: String?
        get() {
            val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
            return myPreference.getString(CHORD_LIBRARY_UPDATE_DATE, "none")
        }
        set(updateDate) {
            val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
            val editor = myPreference.edit()
            editor.putString(CHORD_LIBRARY_UPDATE_DATE, updateDate)
            editor.apply()
        }


    val is_user_info_given_at_login: Boolean
        get() {
            val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
            return myPreference.getBoolean(USER_INFO_UPDATED_AT_LOGIN, false)
        }

    var user_mode:String
    get() {
        val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
        return myPreference.getString(USER_MODE, MODE_SERVICE_PROVIDER) ?: MODE_SERVICE_PROVIDER
    }
    set(value) {
        val myPreference = PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT)
        val editor = myPreference.edit()
        editor.putString(USER_MODE, value)
        editor.apply()
    }
}