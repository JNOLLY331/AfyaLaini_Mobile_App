package com.jnolly.AfyaLaini.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jnolly.AfyaLaini.R
import com.jnolly.AfyaLaini.model.Appointment

class AppointmentAdapter(
    private val appointments: MutableList<Appointment>,
    private val onClick: (Appointment) -> Unit,
    private val onCancel: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorName: TextView = view.findViewById(R.id.tvDoctorName)
        val doctorSpecialty: TextView = view.findViewById(R.id.tvDoctorSpecialty)
        val date: TextView = view.findViewById(R.id.tvDate)
        val time: TextView = view.findViewById(R.id.tvTime)
        val queueNumber: TextView = view.findViewById(R.id.tvQueueNumber)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val cancelBtn: Button = view.findViewById(R.id.btnCancel)
        val viewQueueBtn: Button = view.findViewById(R.id.btnViewQueue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appt = appointments[position]
        holder.doctorName.text = appt.doctorName
        holder.doctorSpecialty.text = appt.doctorSpecialty.ifEmpty { "General Practice" }
        holder.date.text = formatDate(appt.date)
        holder.time.text = appt.timeSlot
        holder.queueNumber.text = "#${appt.queueNumber}"
        holder.status.text = appt.status.replaceFirstChar { it.uppercase() }
        holder.status.setBackgroundResource(getStatusBackground(appt.status))
        
        holder.cancelBtn.visibility = if (appt.status == "upcoming") View.VISIBLE else View.GONE
        holder.viewQueueBtn.visibility = if (appt.status == "upcoming") View.VISIBLE else View.GONE
        
        holder.itemView.setOnClickListener { onClick(appt) }
        holder.cancelBtn.setOnClickListener { onCancel(appt) }
        holder.viewQueueBtn.setOnClickListener { onClick(appt) }
    }

    private fun formatDate(dateStr: String): String {
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val displayFmt = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
        try {
            return displayFmt.format(fmt.parse(dateStr) ?: java.util.Date())
        } catch (e: Exception) {
            return dateStr
        }
    }

    private fun getStatusBackground(status: String): Int {
        return when (status) {
            "upcoming" -> R.drawable.bg_status_upcoming
            "completed" -> R.drawable.bg_status_completed
            "cancelled" -> R.drawable.bg_status_cancelled
            else -> R.drawable.bg_status_upcoming
        }
    }

    override fun getItemCount() = appointments.size

    fun submitList(newList: List<Appointment>) {
        appointments.clear()
        appointments.addAll(newList)
        notifyDataSetChanged()
    }
}