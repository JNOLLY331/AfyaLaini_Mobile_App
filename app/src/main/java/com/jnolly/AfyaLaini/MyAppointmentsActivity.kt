package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jnolly.AfyaLaini.adapter.AppointmentAdapter
import com.jnolly.AfyaLaini.model.Appointment

class MyAppointmentsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var upcomingAdapter: AppointmentAdapter
    private lateinit var pastAdapter: AppointmentAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_appointments)

        setupToolbar()
        setupViewPager()
        setupEmptyStateButton()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupViewPager() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        upcomingAdapter = AppointmentAdapter(
            mutableListOf(),
            onClick = { appointment ->
                val intent = Intent(this, QueueStatusActivity::class.java)
                intent.putExtra("doctorId", appointment.doctorId)
                intent.putExtra("date", appointment.date)
                intent.putExtra("queueNumber", appointment.queueNumber)
                startActivity(intent)
            },
            onCancel = { appointment -> cancelAppointment(appointment) }
        )

        pastAdapter = AppointmentAdapter(
            mutableListOf(),
            onClick = { appointment ->
                val intent = Intent(this, QueueStatusActivity::class.java)
                intent.putExtra("doctorId", appointment.doctorId)
                intent.putExtra("date", appointment.date)
                intent.putExtra("queueNumber", appointment.queueNumber)
                startActivity(intent)
            },
            onCancel = { _ -> }
        )

        viewPager.adapter = object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int) = when (position) {
                0 -> AppointmentListFragment(upcomingAdapter, "upcoming")
                else -> AppointmentListFragment(pastAdapter, "completed")
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Upcoming" else "Past"
        }.attach()

        loadAppointments("upcoming", upcomingAdapter)
        loadAppointments("completed", pastAdapter)
    }

    private fun setupEmptyStateButton() {
        findViewById<Button>(R.id.btnBookFirst).setOnClickListener {
            tabLayout.getTabAt(0)?.select()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAppointments(status: String, adapter: AppointmentAdapter) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("appointments")
            .whereEqualTo("patientId", uid)
            .whereEqualTo("status", status)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val appointments = snapshot.documents.mapNotNull { it.toObject(Appointment::class.java) }
                adapter.submitList(appointments)
                updateEmptyState(status, appointments.isEmpty())
            }
    }

    private fun updateEmptyState(status: String, isEmpty: Boolean) {
        val emptyContainer = findViewById<android.widget.FrameLayout>(R.id.emptyStateContainer)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        
        if (isEmpty && viewPager.currentItem == (if (status == "upcoming") 0 else 1)) {
            emptyContainer.visibility = android.view.View.VISIBLE
            viewPager.visibility = android.view.View.GONE
            
            val tvTitle = findViewById<TextView>(R.id.tvEmptyTitle)
            val tvSubtitle = findViewById<TextView>(R.id.tvEmptySubtitle)
            val ivEmpty = findViewById<ImageView>(R.id.ivEmptyState)
            
            if (status == "upcoming") {
                tvTitle.text = "No upcoming appointments"
                tvSubtitle.text = "Book your first appointment to get started"
                ivEmpty.setImageResource(R.drawable.ic_no_appointments)
            } else {
                tvTitle.text = "No past appointments"
                tvSubtitle.text = "Your completed appointments will appear here"
                ivEmpty.setImageResource(R.drawable.ic_no_appointments)
            }
        } else {
            emptyContainer.visibility = android.view.View.GONE
            viewPager.visibility = android.view.View.VISIBLE
        }
    }

    private fun cancelAppointment(appointment: Appointment) {
        db.collection("appointments").document(appointment.id)
            .update("status", "cancelled")
            .addOnSuccessListener { loadAppointments("upcoming", upcomingAdapter) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}