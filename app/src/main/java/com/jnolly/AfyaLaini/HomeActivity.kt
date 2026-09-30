package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.jnolly.AfyaLaini.adapter.DoctorAdapter
import com.jnolly.AfyaLaini.model.Doctor

class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: DoctorAdapter
    private var allDoctors: List<Doctor> = emptyList()
    private var selectedSpecialty: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupToolbar()
        setupSearch()
        setupFilterChips()
        setupRecyclerView()
        loadDoctors()
        setupBottomNav()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupSearch() {
        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilterChips() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupSpecialties)
        val specialties = listOf("All", "General Practice", "Dentistry", "Pediatrics", "Cardiology", "Dermatology")
        
        for (specialty in specialties) {
            val chip = Chip(this).apply {
                text = specialty
                isCheckable = true
                isChecked = specialty == "All"
                setOnClickListener {
                    val id = this@apply.id
                    chipGroup.check(id)
                    selectedSpecialty = if (specialty == "All") null else specialty
                    filterDoctors(findViewById<TextInputEditText>(R.id.etSearch).text.toString())
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerDoctors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DoctorAdapter(mutableListOf()) { doctor ->
            val intent = Intent(this, DoctorDetailActivity::class.java)
            intent.putExtra("doctorId", doctor.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun filterDoctors(query: String) {
        val filtered = allDoctors.filter { doctor ->
            val matchesQuery = query.isBlank() || 
                doctor.name.contains(query, ignoreCase = true) || 
                doctor.specialty.contains(query, ignoreCase = true)
            val matchesSpecialty = selectedSpecialty == null || doctor.specialty == selectedSpecialty
            matchesQuery && matchesSpecialty
        }
        adapter.submitList(filtered)
        
        val emptyState = findViewById<LinearLayout>(R.id.emptyState)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerDoctors)
        if (filtered.isEmpty()) {
            emptyState.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
        } else {
            emptyState.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun loadDoctors() {
        db.collection("doctors")
            .orderBy("name")
            .get()
            .addOnSuccessListener { snapshot ->
                allDoctors = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Doctor::class.java)?.apply { id = doc.id }
                }
                filterDoctors("")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Could not load doctors: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupBottomNav() {
        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_appointments -> {
                    startActivity(Intent(this, MyAppointmentsActivity::class.java))
                    true
                }
                R.id.nav_queue -> {
                    Toast.makeText(this, "Select a doctor first to view queue", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_profile -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}