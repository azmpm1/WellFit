package com.example.uas1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var profileList: ArrayList<Profile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.rvProfiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        profileList = arrayListOf()

        val dbRef = FirebaseDatabase.getInstance().getReference("profiles")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileList.clear()

                for (data in snapshot.children) {
                    val profile = data.getValue(Profile::class.java)
                    if (profile != null) {
                        profileList.add(profile)
                    }
                }

                recyclerView.adapter = ProfileAdapter(profileList) // ✅ Perbaikan di sini
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistoryActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
