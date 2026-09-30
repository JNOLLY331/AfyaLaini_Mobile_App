package com.jnolly.AfyaLaini

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jnolly.AfyaLaini.model.Appointment
import com.jnolly.AfyaLaini.model.Doctor
import com.jnolly.AfyaLaini.model.QueueStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DoctorDetailActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var doctor: Doctor
    private var selectedDate = todayString()
    private var selectedSlot: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_detail)

        setupToolbar()
        
        val doctorId = intent.getStringExtra("doctorId") ?: return finish()
        db.collection("doctors").document(doctorId).get()
            .addOnSuccessListener { doc ->
                doctor = doc.toObject(Doctor::class.java)?.apply { id = doc.id } ?: return@addOnSuccessListener
                bindDoctor()
            }

        setupDatePicker()
        findViewById<Button>(R.id.btnConfirmBooking).setOnClickListener { bookAppointment() }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupDatePicker() {
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        etDate.setText(formatDisplayDate(selectedDate))
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                calendar.time = fmt.parse(selectedDate) ?: Date()
            } catch (e: Exception) {
                calendar.time = Date()
            }
            DatePickerDialog(this, { _, year, month, day ->
                val c = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                selectedDate = fmt.format(c.time)
                etDate.setText(formatDisplayDate(selectedDate))
                loadTimeSlots()
                updateAppointmentDetails()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun loadTimeSlots() {
        val slotGroup = findViewById<ChipGroup>(R.id.chipGroupSlots)
        slotGroup.removeAllViews()
        selectedSlot = null
        for (slot in doctor.availableSlots) {
            val chip = Chip(this).apply {
                text = slot
                isCheckable = true
                setOnClickListener { 
                    selectedSlot = slot
                    updateAppointmentDetails()
                    updateConfirmButton()
                }
            }
            slotGroup.addView(chip)
        }
    }

    private fun bindDoctor() {
        findViewById<TextView>(R.id.tvDoctorName).text = doctor.name
        findViewById<TextView>(R.id.tvDoctorSpecialty).text = doctor.specialty
        loadTimeSlots()
        updateAppointmentDetails()
    }

    private fun updateAppointmentDetails() {
        findViewById<TextView>(R.id.tvSelectedDate).text = formatDisplayDate(selectedDate)
        findViewById<TextView>(R.id.tvSelectedSlot).text = selectedSlot?.let { "Available" } ?: "Select a time slot"
        findViewById<TextView>(R.id.tvDoctorLocation).text = "AfyaLaini Medical Center, Nairobi"
        updateConfirmButton()
    }

    private fun updateConfirmButton() {
        val btnConfirm = findViewById<Button>(R.id.btnConfirmBooking)
        val isEnabled = selectedSlot != null
        btnConfirm.isEnabled = isEnabled
        btnConfirm.alpha = if (isEnabled) 1f else 0.6f
    }

    private fun formatDisplayDate(dateStr: String): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFmt = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        try {
            return displayFmt.format(fmt.parse(dateStr) ?: Date())
        } catch (e: Exception) {
            return dateStr
        }
    }

    private fun bookAppointment() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val slot = selectedSlot
        if (slot == null) {
            Toast.makeText(this, "Pick a time slot first", Toast.LENGTH_SHORT).show()
            return
        }

        val queueStatusRef = db.collection("queueStatus").document("${doctor.id}_$selectedDate")
        val appointmentRef = db.collection("appointments").document()

        db.runTransaction { transaction ->
            val snapshot = transaction.get(queueStatusRef)
            val currentLast = snapshot.getLong("lastIssuedNumber")?.toInt() ?: 0
            val nextNumber = currentLast + 1

            transaction.set(queueStatusRef, QueueStatus(
                doctorId = doctor.id,
                date = selectedDate,
                lastIssuedNumber = nextNumber,
                nowServing = snapshot.getLong("nowServing")?.toInt() ?: 0
            ))

            transaction.set(appointmentRef, Appointment(
                id = appointmentRef.id,
                patientId = uid,
                patientName = FirebaseAuth.getInstance().currentUser?.displayName?.ifEmpty { "Patient" } ?: "Patient",
                doctorId = doctor.id,
                doctorName = doctor.name,
                doctorSpecialty = doctor.specialty,
                date = selectedDate,
                timeSlot = slot,
                queueNumber = nextNumber
            ))
            nextNumber
        }.addOnSuccessListener { queueNumber ->
            val intent = Intent(this, QueueStatusActivity::class.java)
            intent.putExtra("doctorId", doctor.id)
            intent.putExtra("date", selectedDate)
            intent.putExtra("queueNumber", queueNumber as Int)
            startActivity(intent)
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun todayString(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return fmt.format(Date())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}