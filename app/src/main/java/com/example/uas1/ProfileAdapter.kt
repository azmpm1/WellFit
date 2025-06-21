package com.example.uas1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter(private val profileList: List<Profile>) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvBMI: TextView = itemView.findViewById(R.id.tvBMI)
        val tvWater: TextView = itemView.findViewById(R.id.tvWater)
        val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profileList[position]

        try {
            val name = profile.name ?: "-"
            val bmi = profile.bmi
            val bmiCategory = when {
                bmi < 18.5 -> "Kurus"
                bmi < 25 -> "Normal"
                bmi < 30 -> "Kelebihan berat"
                else -> "Obesitas"
            }

            holder.tvName.text = "Nama: $name"
            holder.tvBMI.text = "BMI: ${String.format("%.2f", bmi)} ($bmiCategory)"
            holder.tvWater.text = "Air: ${profile.water} gelas"
            holder.tvCalories.text = "Kalori: ${profile.calories} kkal"
        } catch (e: Exception) {
            // Logging kalau terjadi kesalahan saat menampilkan data ke UI
            android.util.Log.e("AdapterError", "Error saat bind data: $profile", e)
        }
    }


    override fun getItemCount(): Int {
        return profileList.size
    }
}

