package com.example.uas1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DrinkReminderAdapter(
    private val list: ArrayList<DrinkReminder>,
    private val onEditClick: (DrinkReminder) -> Unit,
    private val onDeleteClick: (DrinkReminder) -> Unit
) : RecyclerView.Adapter<DrinkReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvReminderTime)
        val tvAmount: TextView = itemView.findViewById(R.id.tvReminderAmount)
        val btnEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val item = list[position]
        holder.tvTime.text = item.time
        holder.tvAmount.text = "${item.amount} ml"

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = list.size
}
