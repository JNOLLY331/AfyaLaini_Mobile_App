package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.jnolly.AfyaLaini.adapter.QueueRowAdapter
import com.jnolly.AfyaLaini.model.Appointment
import com.jnolly.AfyaLaini.model.Doctor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StaffHomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: QueueRowAdapter
    private var doctors: List<Doctor> = emptyList()
    private var selectedDoctorId: String? = null
    private val today = todayString()
    
    private var appointmentsListener: ListenerRegistration? = null
    private var statusListener: ListenerRegistration? = null
    private var liveDot: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_home)

        setupToolbar()
        setupRecyclerView()
        setupButtons()
        loadDoctorSpinner()
        startLiveIndicator()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerQueue)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = QueueRowAdapter(mutableListOf())
        recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnCallNext).setOnClickListener { callNextPatient() }
        findViewById<Button>(R.id.btnAddDoctor).setOnClickListener {
            startActivity(Intent(this, AddDoctorActivity::class.java))
        }
    }

    private fun loadDoctorSpinner() {
        db.collection("doctors").get().addOnSuccessListener { snapshot ->
            doctors = snapshot.documents.mapNotNull {
                it.toObject(Doctor::class.java)?.apply { id = it.id }
            }
            val spinner = findViewById<Spinner>(R.id.spinnerDoctors)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctors.map { it.name })
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    doctors.getOrNull(position)?.let { loadQueue(it.id) }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            doctors.firstOrNull()?.let { firstDoctor ->
                spinner.setSelection(0)
                loadQueue(firstDoctor.id)
            }
        }
    }

    private fun loadQueue(doctorId: String) {
        selectedDoctorId = doctorId
        
        appointmentsListener?.remove()
        statusListener?.remove()

        appointmentsListener = db.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("date", today)
            .whereEqualTo("status", "upcoming")
            .orderBy("queueNumber")
            .addSnapshotListener { snapshot, _ ->
                val appointments = snapshot?.documents?.mapNotNull { it.toObject(Appointment::class.java) } ?: emptyList()
                adapter.submitList(appointments)
                findViewById<TextView>(R.id.tvWaitingCount).text = appointments.size.toString()
                findViewById<Button>(R.id.btnCallNext).isEnabled = appointments.isNotEmpty()
                findViewById<Button>(R.id.btnCallNext).alpha = if (appointments.isNotEmpty()) 1f else 0.6f
            }

        statusListener = db.collection("queueStatus").document("${doctorId}_$today")
            .addSnapshotListener { snapshot, _ ->
                val nowServing = snapshot?.getLong("nowServing")?.toInt() ?: 0
                findViewById<TextView>(R.id.tvServingCount).text = nowServing.toString()
                adapter.updateNowServing(nowServing)
            }
    }

    private fun callNextPatient() {
        val doctorId = selectedDoctorId ?: return
        val ref = db.collection("queueStatus").document("${doctorId}_$today")
        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val current = snapshot.getLong("nowServing")?.toInt() ?: 0
            transaction.update(ref, "nowServing", current + 1)
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Could not advance queue: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private val liveHandler = Handler(Looper.getMainLooper())
    private val liveRunnable = object : Runnable {
        override fun run() {
            liveDot?.let { dot ->
                dot.alpha = if (dot.alpha == 1f) 0.3f else 1f
            }
            liveHandler.postDelayed(this, 1000)
        }
    }

    private fun startLiveIndicator() {
        liveDot = findViewById(R.id.vLiveDot)
        liveHandler.postDelayed(liveRunnable, 1000)
    }

    private fun todayString(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return fmt.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        liveHandler.removeCallbacks(liveRunnable)
        appointmentsListener?.remove()
        statusListener?.remove()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}