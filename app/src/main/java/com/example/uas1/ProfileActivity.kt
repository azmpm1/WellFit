package com.example.uas1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.uas1.databinding.ActivityProfileBinding
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var pref: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPrefManager(this)

        // Ambil data pengguna dari SharedPreferences
        val name = pref.getName() ?: "-"
        val bmi = pref.getBMI()
        val water = pref.getWaterCount()
        val calories = pref.getCalories()

        // Tampilkan ke layout
        binding.tvNama.text = "Nama: $name"
        binding.tvBMI.text = "BMI Terakhir: ${String.format("%.2f", bmi)}"
        binding.tvAir.text = "Air Diminum: $water gelas"
        binding.tvKalori.text = "Kalori Hari Ini: $calories kkal"

        // Simpan ke Firebase ketika tombol diklik
        binding.btnSaveToFirebase.setOnClickListener {
            val profile = Profile(name, bmi, water, calories)
            val dbRef = FirebaseDatabase.getInstance().getReference("profiles")

            dbRef.push().setValue(profile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan ke Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}


