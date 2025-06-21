package com.example.uas1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.uas1.databinding.ActivityBmiCalculatorBinding

class BMICalculatorActivity : AppCompatActivity() {

    // Menggunakan ViewBinding untuk mengakses view dengan aman dan mudah
    private lateinit var binding: ActivityBmiCalculatorBinding
    // Deklarasi untuk manager SharedPreferences
    private lateinit var pref: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout menggunakan ViewBinding
        binding = ActivityBmiCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPrefManager
        pref = SharedPrefManager(this)

        // Menambahkan listener pada tombol "Hitung"
        binding.btnHitung.setOnClickListener {
            calculateAndShowBmi()
        }
    }

    /**
     * Fungsi untuk menghitung BMI, menampilkannya ke UI,
     * dan menyimpannya ke SharedPreferences.
     */
    private fun calculateAndShowBmi() {
        // Mengambil input dari EditText sebagai String
        val weightStr = binding.etBerat.text.toString()
        val heightStr = binding.etTinggi.text.toString()

        // Memastikan kedua input tidak kosong
        if (weightStr.isNotEmpty() && heightStr.isNotEmpty()) {
            val weight = weightStr.toFloat()
            // Mengonversi tinggi dari cm ke meter untuk kalkulasi BMI
            val height = heightStr.toFloat() / 100

            // Menghitung BMI dengan rumus: berat (kg) / (tinggi (m) * tinggi (m))
            val bmi = weight / (height * height)

            // --- BARIS KODE YANG DIMINTA DITAMBAHKAN ---
            // Menyimpan nilai BMI ke SharedPreferences.
            // Menggunakan toDouble() untuk mengonversi Float ke Double sesuai kebutuhan fungsi saveBMI.
            pref.saveBMI(bmi.toDouble())
            // ------------------------------------------

            // Memformat hasil BMI menjadi satu angka di belakang koma
            val bmiFormatted = String.format("%.1f", bmi)

            val bmiCategory: String
            val categoryColor: Int

            // Menentukan kategori BMI dan warna yang sesuai
            when {
                bmi < 18.5 -> {
                    bmiCategory = "Berat Badan Kurang"
                    categoryColor = R.color.bmi_underweight
                }
                bmi < 24.9 -> {
                    bmiCategory = "Berat Badan Ideal"
                    categoryColor = R.color.bmi_ideal
                }
                bmi < 29.9 -> {
                    bmiCategory = "Berat Badan Berlebih"
                    categoryColor = R.color.bmi_overweight
                }
                else -> {
                    bmiCategory = "Obesitas"
                    categoryColor = R.color.bmi_obese
                }
            }

            // Menampilkan hasil kalkulasi ke TextViews
            binding.tvBmiScore.text = bmiFormatted
            binding.tvBmiCategory.text = bmiCategory
            binding.tvBmiCategory.setTextColor(ContextCompat.getColor(this, categoryColor))

            // Menampilkan kartu hasil yang sebelumnya disembunyikan
            binding.resultCard.visibility = View.VISIBLE

        } else {
            // Menampilkan pesan jika salah satu atau kedua kolom input kosong
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
        }
    }
}
