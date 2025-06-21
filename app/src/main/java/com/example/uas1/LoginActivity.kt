package com.example.uas1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas1.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pref: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        pref = SharedPrefManager(this)

        binding.tvRegisterLink.setOnClickListener {
            // Buat Intent untuk berpindah dari LoginActivity ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            // Mulai activity baru
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid

                            if (uid != null) {
                                // Ambil nama dari Firebase Realtime Database
                                val database = FirebaseDatabase.getInstance().getReference("users")
                                database.child(uid).child("name").get()
                                    .addOnSuccessListener { snapshot ->
                                        val name = snapshot.getValue(String::class.java)

                                        if (name != null) {
                                            // Simpan ke Shared Preferences
                                            val pref = SharedPrefManager(this)
                                            pref.saveName(name)

                                            // Lanjut ke MainActivity
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Gagal ambil nama",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            this,
                                            "Gagal terhubung ke database",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Login gagal: ${it.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}
