package com.example.uas1

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas1.databinding.ActivityWaterReminderBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class WaterReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterReminderBinding
    private lateinit var database: DatabaseReference
    private lateinit var reminderList: ArrayList<DrinkReminder>
    // --- TAMBAHAN ---
    // Deklarasi untuk manager SharedPreferences
    private lateinit var pref: SharedPrefManager
    // ----------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- TAMBAHAN ---
        // Inisialisasi SharedPrefManager
        pref = SharedPrefManager(this)
        // ----------------

        database = FirebaseDatabase.getInstance().getReference("drink_reminders")
        reminderList = arrayListOf()
        binding.rvReminders.layoutManager = LinearLayoutManager(this)

        binding.btnPilihJam.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                binding.tvJamTerpilih.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            }, hour, minute, true).show()
        }

        binding.btnTambah.setOnClickListener {
            val time = binding.tvJamTerpilih.text.toString()
            val amount = binding.etJumlahAir.text.toString().toIntOrNull()

            if (time.isNotEmpty() && amount != null) {
                val id = database.push().key ?: return@setOnClickListener
                val reminder = DrinkReminder(id, time, amount)
                database.child(id).setValue(reminder)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                        loadReminders()
                    }
            } else {
                Toast.makeText(this, "Isi waktu dan jumlah dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTambahGelas.setOnClickListener {
            tambahAirKeRingkasan(250)
        }

        binding.btnResetGelas.setOnClickListener {
            tampilkanDialogReset()
        }

        loadReminders()
        loadJumlahAirHariIni()
    }

    private fun loadReminders() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reminderList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(DrinkReminder::class.java)
                    if (item != null) reminderList.add(item)
                }

                reminderList.reverse()

                binding.rvReminders.adapter = DrinkReminderAdapter(
                    reminderList,
                    onEditClick = { reminder -> showEditDialog(reminder) },
                    onDeleteClick = { reminder -> deleteReminder(reminder) }
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WaterReminderActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteReminder(reminder: DrinkReminder) {
        database.child(reminder.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditDialog(reminder: DrinkReminder) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_reminder, null)
        val etTime = dialogView.findViewById<EditText>(R.id.etEditTime)
        val etAmount = dialogView.findViewById<EditText>(R.id.etEditAmount)

        etTime.setText(reminder.time)
        etAmount.setText(reminder.amount.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Pengingat")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newTime = etTime.text.toString()
                val newAmount = etAmount.text.toString().toIntOrNull() ?: reminder.amount

                val updated = DrinkReminder(reminder.id, newTime, newAmount)
                database.child(reminder.id).setValue(updated)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data diperbarui", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahAirKeRingkasan(jumlah: Int = 250) {
        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val ref = FirebaseDatabase.getInstance().getReference("profiles").child(tanggal)

        ref.child("water").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalSebelumnya = snapshot.getValue(String::class.java)?.toIntOrNull() ?: 0
                val totalBaru = totalSebelumnya + jumlah
                ref.child("water").setValue(totalBaru.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this@WaterReminderActivity, "1 Gelas air ditambahkan", Toast.LENGTH_SHORT).show()
                        updateJumlahGelas(totalBaru)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WaterReminderActivity, "Gagal tambah air", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadJumlahAirHariIni() {
        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val ref = FirebaseDatabase.getInstance().getReference("profiles").child(tanggal)

        ref.child("water").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jumlahAir = snapshot.getValue(String::class.java)?.toIntOrNull() ?: 0
                updateJumlahGelas(jumlahAir)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WaterReminderActivity, "Gagal ambil jumlah air", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateJumlahGelas(totalMl: Int) {
        val satuGelas = 250
        val totalGelas = totalMl / satuGelas
        val targetGelas = 8
        binding.tvJumlahGelas.text = "$totalGelas/$targetGelas gelas"

        // --- BARIS KODE YANG DIMINTA DITAMBAHKAN ---
        // Menyimpan jumlah total air (dalam mL) ke SharedPreferences.
        pref.saveWaterCount(totalMl)
        // ------------------------------------------
    }

    private fun tampilkanDialogReset() {
        AlertDialog.Builder(this)
            .setTitle("Reset Air Minum")
            .setMessage("Apakah kamu yakin ingin mengatur ulang jumlah air yang diminum hari ini?")
            .setPositiveButton("Ya") { _, _ -> resetJumlahAir() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun resetJumlahAir() {
        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val ref = FirebaseDatabase.getInstance().getReference("profiles").child(tanggal)

        ref.child("water").setValue("0")
            .addOnSuccessListener {
                Toast.makeText(this, "Jumlah gelas berhasil direset", Toast.LENGTH_SHORT).show()
                updateJumlahGelas(0)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal reset air", Toast.LENGTH_SHORT).show()
            }
    }
}
