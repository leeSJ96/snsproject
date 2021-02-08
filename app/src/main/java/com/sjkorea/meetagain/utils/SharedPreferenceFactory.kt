package com.sjkorea.meetagain.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.sjkorea.meetagain.App

object SharedPreferenceFactory {

    private fun getSharedPreferences(): SharedPreferences {
        return App.instance.getSharedPreferences("prefs_name", MODE_PRIVATE)
    }

    fun getStrValue(KEY: String, defaultValue: String? = null): String? {

        return getSharedPreferences().getString(KEY, defaultValue)
    }

    fun getIntValue(KEY: String, defaultValue: Int? = 0): Int? {

        return defaultValue?.let {  getSharedPreferences().getInt(KEY, it) }
    }

    fun putStrValue(KEY: String, valueString: String?) {
        val editor =  getSharedPreferences().edit()
        editor.putString(KEY, valueString)
        editor.apply()
    }

    fun putIntValue(KEY: String, valueInt: Int?) {
        val editor =  getSharedPreferences().edit()
        valueInt?.let { editor.putInt(KEY, it) }
        editor.apply()
    }

    fun clearAllValue() {
        val editor =  getSharedPreferences().edit()
        editor.clear()
        editor.apply()
        editor.commit()
    }

}
