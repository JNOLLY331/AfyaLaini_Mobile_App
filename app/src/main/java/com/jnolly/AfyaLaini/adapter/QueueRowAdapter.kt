package com.jnolly.AfyaLaini.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jnolly.AfyaLaini.R
import com.jnolly.AfyaLaini.model.Appointment

class QueueRowAdapter(
    private val rows: MutableList<Appointment>
) : RecyclerView.Adapter<QueueRowAdapter.ViewHolder>() {
    private var nowServing = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.tvQueueNumber)
        val name: TextView = view.findViewById(R.id.tvPatientName)
        val time: TextView = view.findViewById(R.id.tvTimeSlot)
        val nowBadge: TextView = view.findViewById(R.id.tvNowBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_queue_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appt = rows[position]
        holder.number.text = appt.queueNumber.toString()
        holder.name.text = appt.patientName
        holder.time.text = appt.timeSlot
        holder.nowBadge.visibility =
            if (appt.queueNumber == nowServing) View.VISIBLE else View.GONE
        
        // Update card appearance for now serving
        if (appt.queueNumber == nowServing) {
            holder.itemView.setBackgroundResource(R.drawable.bg_queue_now_serving)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_queue_normal)
        }
    }

    override fun getItemCount() = rows.size

    fun submitList(newList: List<Appointment>) {
        rows.clear()
        rows.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateNowServing(value: Int) {
        nowServing = value
        notifyDataSetChanged()
    }
}