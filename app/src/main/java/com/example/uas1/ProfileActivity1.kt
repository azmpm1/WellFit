package com.example.uas1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas1.databinding.ActivityProfile1Binding
import com.google.firebase.database.*
import android.util.Log


class ProfileActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivityProfile1Binding
    private lateinit var pref: SharedPrefManager
    private lateinit var database: DatabaseReference
    private lateinit var profileList: ArrayList<Profile>
    private lateinit var adapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfile1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreferences dan Firebase
        pref = SharedPrefManager(this)
        database = FirebaseDatabase.getInstance().getReference("profiles")

        // Ambil data dari SharedPreferences
        val name = pref.getName() ?: "-"
        val bmi = pref.getBMI()
        val water = pref.getWaterCount()
        val calories = pref.getCalories()

        // Hitung kategori BMI
        val kategoriBMI = when {
            bmi < 18.5 -> "Kurus"
            bmi < 25 -> "Normal"
            bmi < 30 -> "Kelebihan berat"
            else -> "Obesitas"
        }

        // Tampilkan data ke tampilan atas
        binding.tvNama.text = "Nama: $name"
        binding.tvBMI.text = "BMI Terakhir: ${String.format("%.2f", bmi)} ($kategoriBMI)"
        binding.tvAir.text = "Air Diminum: $water gelas"
        binding.tvKalori.text = "Kalori Hari Ini: $calories kkal"

        // Siapkan RecyclerView dan Adapter
        profileList = arrayListOf()
        adapter = ProfileAdapter(profileList)
        binding.rvProfiles.layoutManager = LinearLayoutManager(this)
        binding.rvProfiles.adapter = adapter

        // Tombol Simpan ke Firebase
        binding.btnSaveToFirebase.setOnClickListener {
            val profile = Profile(
                name = pref.getName() ?: "Tidak diketahui",
                bmi = pref.getBMI(),
                water = pref.getWaterCount(),
                calories = pref.getCalories()
            )
            database.child("profiles").push().setValue(profile)

            database.push().setValue(profile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan ke Firebase", Toast.LENGTH_SHORT).show()
                    loadProfileList()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        // Load data saat Activity dibuka
        loadProfileList()
    }

    private fun loadProfileList() {
        database.child("profiles").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileList.clear()
                for (data in snapshot.children) {
                    try {
                        val profile = data.getValue(Profile::class.java)
                        if (profile != null) {
                            profileList.add(profile)
                        }
                    } catch (e: Exception) {
                        Log.e("LoadProfile", "Gagal convert: ${e.message}")
                    }
                }
                profileList.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity1, "Gagal load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}