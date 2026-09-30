package com.jnolly.AfyaLaini.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.jnolly.AfyaLaini.R
import com.jnolly.AfyaLaini.model.Doctor

class DoctorAdapter(
    private val doctors: MutableList<Doctor>,
    private val onClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgDoctor)
        val name: TextView = view.findViewById(R.id.tvName)
        val specialty: TextView = view.findViewById(R.id.tvSpecialty)
        val bookBtn: Button = view.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.name.text = doctor.name
        holder.specialty.text = doctor.specialty
        Glide.with(holder.image.context)
            .load(doctor.photoUrl)
            .placeholder(R.drawable.placeholder_doctor)
            .transform(CircleCrop())
            .into(holder.image)

        val open = { onClick(doctor) }
        holder.itemView.setOnClickListener { open() }
        holder.bookBtn.setOnClickListener { open() }
    }

    override fun getItemCount() = doctors.size

    fun submitList(newList: List<Doctor>) {
        doctors.clear()
        doctors.addAll(newList)
        notifyDataSetChanged()
    }
}