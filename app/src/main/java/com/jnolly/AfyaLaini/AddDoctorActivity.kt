package com.jnolly.AfyaLaini

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.jnolly.AfyaLaini.model.Doctor

class AddDoctorActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor)

        setupToolbar()
        setupTimeSlotChips()
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveDoctor() }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupTimeSlotChips() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupSlots)
        val defaultSlots = listOf(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "14:00", "14:30", "15:00", "15:30",
            "16:00", "16:30"
        )
        
        for (slot in defaultSlots) {
            val chip = Chip(this).apply {
                text = slot
                isCheckable = true
            }
            chipGroup.addView(chip)
        }
    }

    private fun saveDoctor() {
        val name = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName).text.toString().trim()
        val specialty = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSpecialty).text.toString().trim()
        val slots = collectSelectedSlots()

        if (name.isEmpty() || specialty.isEmpty() || slots.isEmpty()) {
            Toast.makeText(this, "Fill in a name, specialty, and at least one time slot", Toast.LENGTH_SHORT).show()
            return
        }

        val doctor = Doctor(name = name, specialty = specialty, availableSlots = slots)
        db.collection("doctors").add(doctor)
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Could not save: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun collectSelectedSlots(): List<String> {
        val group = findViewById<ChipGroup>(R.id.chipGroupSlots)
        return group.checkedChipIds.map { id -> findViewById<Chip>(id).text.toString() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}