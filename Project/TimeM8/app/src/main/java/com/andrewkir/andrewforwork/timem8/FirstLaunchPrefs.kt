package com.andrewkir.andrewforwork.timem8

import android.content.Context
import android.content.SharedPreferences


class FirstLaunchPrefs(context: Context) {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var _context: Context = context

    // shared pref mode
    private var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = "intro_slider-welcome"

    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }
}