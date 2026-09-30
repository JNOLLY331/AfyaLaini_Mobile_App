package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.adapter.FragmentStateAdapter
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
    private val auth = FirebaseAuth.getInstance()
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

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int) = when (position) {
                0 -> AppointmentListFragment(upcomingAdapter, "upcoming")
                else -> AppointmentListFragment(pastAdapter, "completed")
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Upcoming" else "Past / Cancelled"
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    loadAppointments("upcoming", upcomingAdapter)
                } else {
                    loadAppointments("completed", pastAdapter)
                }
            }
        })

        loadAppointments("upcoming", upcomingAdapter)
        loadAppointments("completed", pastAdapter)
    }

    private fun setupEmptyStateButton() {
        findViewById<Button>(R.id.btnBookFirst).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAppointments(status: String, adapter: AppointmentAdapter) {
        val uid = auth.currentUser?.uid ?: return
        
        var query = db.collection("appointments")
            .whereEqualTo("patientId", uid)

        if (status == "upcoming") {
            query = query.whereEqualTo("status", "upcoming")
        } else {
            // Include both completed and cancelled in past tab
            query = query.whereIn("status", listOf("completed", "cancelled"))
        }

        query.orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val appointments = snapshot.documents.mapNotNull { it.toObject(Appointment::class.java) }
                adapter.submitList(appointments)
                updateEmptyState(appointments.isEmpty())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Could not load appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        val emptyContainer = findViewById<FrameLayout>(R.id.emptyStateContainer)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val isUpcomingTab = viewPager.currentItem == 0
        if (isEmpty) {
            emptyContainer.visibility = View.VISIBLE
            viewPager.visibility = View.GONE

            val tvTitle = findViewById<TextView>(R.id.tvEmptyTitle)
            val tvSubtitle = findViewById<TextView>(R.id.tvEmptySubtitle)
            val ivEmpty = findViewById<ImageView>(R.id.ivEmptyState)

            if (isUpcomingTab) {
                tvTitle.text = "No upcoming appointments"
                tvSubtitle.text = "Book your first appointment to get started"
                ivEmpty.setImageResource(R.drawable.ic_no_appointments)
            } else {
                tvTitle.text = "No past appointments"
                tvSubtitle.text = "Your completed or cancelled appointments will appear here"
                ivEmpty.setImageResource(R.drawable.ic_no_appointments)
            }
        } else {
            emptyContainer.visibility = View.GONE
            viewPager.visibility = View.VISIBLE
        }
    }

    private fun cancelAppointment(appointment: Appointment) {
        db.collection("appointments").document(appointment.id)
            .update("status", "cancelled")
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment cancelled successfully", Toast.LENGTH_SHORT).show()
                loadAppointments("upcoming", upcomingAdapter)
                loadAppointments("completed", pastAdapter)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Could not cancel appointment: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}