package com.andrewkir.andrewforwork.timem8.Services

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val PREFS_FILENAME = "Prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    private val DATA = "data"
    var data: String
        get() = prefs.getString(DATA, "")
        set(value) = prefs.edit().putString(DATA, value).apply()

}