package com.andrewkir.andrewforwork.timem8

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences



class FirstLaungPrefs {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var _context: Context

    // shared pref mode
    var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = "intro_slider-welcome"

    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

    constructor(context: Context){
        this._context = context
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