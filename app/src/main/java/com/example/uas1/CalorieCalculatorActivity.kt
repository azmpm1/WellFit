package com.example.uas1

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas1.databinding.ActivityCalorieCalculatorBinding

class CalorieCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalorieCalculatorBinding
    // Deklarasi untuk manager SharedPreferences
    private lateinit var pref: SharedPrefManager

    // Menggunakan Map untuk menyimpan daftar makanan beserta kalorinya
    private val makananMap = mapOf(
        "Nasi Goreng" to 500,
        "Mie Ayam" to 450,
        "Bakso" to 300,
        "Sate Ayam" to 400,
        "Tempe Goreng" to 150,
        "Ayam Bakar" to 350
    )

    // Variabel untuk menyimpan kalori dari makanan yang dipilih
    private var selectedKalori = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalorieCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPrefManager
        pref = SharedPrefManager(this)

        // Menyiapkan Adapter untuk Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            makananMap.keys.toList() // Mengambil nama makanan untuk ditampilkan
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMakanan.adapter = adapter

        // Listener untuk item yang dipilih di Spinner
        binding.spinnerMakanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // Mendapatkan nama makanan yang dipilih
                val selectedFoodName = makananMap.keys.toList()[position]
                // Mendapatkan nilai kalori dari map berdasarkan nama makanan
                selectedKalori = makananMap[selectedFoodName] ?: 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Jika tidak ada yang dipilih, kalori diatur ke 0
                selectedKalori = 0
            }
        }

        // Listener untuk tombol tambah kalori
        binding.btnTambahKalori.setOnClickListener {
            val currentTotal = pref.getCalories()
            val updatedTotal = currentTotal + selectedKalori

            // Menyimpan total kalori yang sudah diperbarui ke SharedPreferences
            pref.saveCalories(updatedTotal)

            Toast.makeText(this, "$selectedKalori kkal ditambahkan", Toast.LENGTH_SHORT).show()
            updateDisplay() // Memperbarui tampilan
        }

        // Listener untuk tombol reset kalori
        binding.btnResetKalori.setOnClickListener {
            // Mengatur ulang total kalori menjadi 0 di SharedPreferences
            pref.saveCalories(0)
            Toast.makeText(this, "Total kalori berhasil direset", Toast.LENGTH_SHORT).show()
            updateDisplay() // Memperbarui tampilan
        }

        // Memuat tampilan awal saat activity dibuat
        updateDisplay()
    }

    /**
     * Fungsi untuk memperbarui tampilan total kalori di UI.
     */
    private fun updateDisplay() {
        val totalCalories = pref.getCalories()
        binding.tvTotalKalori.text = "Total Kalori Hari Ini: $totalCalories kkal"
        binding.tvEdukasi.text = "Target harian kalori untuk dewasa rata-rata adalah 2000-2500 kkal."
    }
}
