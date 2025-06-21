package com.example.uas1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas1.databinding.ActivityInputNameBinding

class InputNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputNameBinding
    private lateinit var pref: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPrefManager(this)

        // Kalau nama sudah ada di SharedPref, langsung skip ke MainActivity
        if (!pref.getName().isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Tombol Simpan Nama
        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Masukkan nama terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                pref.saveName(name)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}