package com.example.uas1

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveName(name: String) {
        pref.edit().putString("NAME", name).apply()
    }

    fun getName(): String? {
        return pref.getString("NAME", null)
    }

    fun saveBMI(bmi: Double) {
        pref.edit().putFloat("BMI", bmi.toFloat()).apply()
    }

    fun getBMI(): Double {
        return pref.getFloat("BMI", 0f).toDouble()
    }


    fun saveWaterCount(count: Int) {
        pref.edit().putInt("WATER", count).apply()
    }

    fun getWaterCount(): Int {
        return pref.getInt("WATER", 0)
    }

    fun saveCalories(calories: Int) {
        pref.edit().putInt("CALORIES", calories).apply()
    }

    fun getCalories(): Int {
        return pref.getInt("CALORIES", 0)
    }

    fun clearAll() {
        pref.edit().clear().apply()
    }

    fun clear() {
        val edit = pref.edit()
        edit.clear()
        edit.apply()
    }
}