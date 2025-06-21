package com.example.uas1

data class Profile(
    val name: String = "",
    val bmi: Double = 0.0,
    val water: Int = 0,
    val calories: Int = 0,
    var key: String? = null  // untuk hapus
)

