package com.example.uas1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uas1.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPrefManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPrefManager(this)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val localName = pref.getName()

        if (!localName.isNullOrEmpty()) {
            binding.tvGreeting.text = "Halo, $localName"
        } else {
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                database.child("users").child(uid).child("name")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nameFromFirebase = snapshot.getValue(String::class.java)
                            pref.saveName(nameFromFirebase ?: "")
                            binding.tvGreeting.text = "Halo, ${nameFromFirebase ?: "Pengguna"}"
                        }

                        override fun onCancelled(error: DatabaseError) {
                            binding.tvGreeting.text = "Halo, Pengguna"
                        }
                    })
            } else {
                binding.tvGreeting.text = "Halo, Pengguna"
            }
        }

        binding.cardBMI.setOnClickListener {
            startActivity(Intent(this, BMICalculatorActivity::class.java))
        }

        binding.cardWater.setOnClickListener {
            startActivity(Intent(this, WaterReminderActivity::class.java))
        }

        binding.cardCalorie.setOnClickListener {
            startActivity(Intent(this, CalorieCalculatorActivity::class.java))
        }

        binding.cardSummary.setOnClickListener {
            startActivity(Intent(this, ProfileActivity1::class.java))
        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            pref.clearAll()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
