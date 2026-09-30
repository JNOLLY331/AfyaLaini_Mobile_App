package com.jnolly.AfyaLaini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jnolly.AfyaLaini.adapter.AppointmentAdapter

class AppointmentListFragment(
    private val adapter: AppointmentAdapter,
    private val status: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_appointment_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAppointments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        return view
    }
}