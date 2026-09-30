package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jnolly.AfyaLaini.adapter.DoctorAdapter
import com.jnolly.AfyaLaini.model.Doctor

class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: DoctorAdapter
    private var allDoctors: List<Doctor> = emptyList()
    private var selectedSpecialty: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupToolbar()
        setupUserRoleBadge()
        setupSearch()
        setupFilterChips()
        setupRecyclerView()
        loadDoctors()
        loadAppointmentBadgeCount()
        setupBottomNav()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupUserRoleBadge() {
        val tvWelcome = findViewById<TextView>(R.id.tvWelcomeUser)
        val tvRole = findViewById<TextView>(R.id.tvRoleBadge)
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val name = currentUser.displayName?.ifEmpty { null } ?: currentUser.email?.substringBefore("@") ?: "Patient"
            tvWelcome.text = getString(R.string.welcome_user, name)

            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role")?.uppercase() ?: "PATIENT"
                    tvRole.text = role
                    if (role == "STAFF") {
                        tvRole.setBackgroundResource(R.drawable.bg_status_completed)
                        tvRole.setTextColor(getColor(R.color.brand_primary))
                    } else {
                        tvRole.setBackgroundResource(R.drawable.bg_status_upcoming)
                        tvRole.setTextColor(getColor(R.color.brand_primary))
                    }
                }
        }
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
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadDoctors() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        db.collection("doctors")
            .orderBy("name")
            .get()
            .addOnSuccessListener { snapshot ->
                progressBar.visibility = View.GONE
                allDoctors = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Doctor::class.java)?.apply { id = doc.id }
                }
                filterDoctors("")
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Could not load doctors: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadAppointmentBadgeCount() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("appointments")
            .whereEqualTo("patientId", uid)
            .whereEqualTo("status", "upcoming")
            .addSnapshotListener { snapshot, _ ->
                val count = snapshot?.size() ?: 0
                val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
                if (count > 0) {
                    val badge = nav.getOrCreateBadge(R.id.nav_appointments)
                    badge.number = count
                    badge.isVisible = true
                } else {
                    nav.removeBadge(R.id.nav_appointments)
                }
            }
    }

    private fun setupBottomNav() {
        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.selectedItemId = R.id.nav_home
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_appointments -> {
                    startActivity(Intent(this, MyAppointmentsActivity::class.java))
                    true
                }
                R.id.nav_queue -> {
                    Toast.makeText(this, "Select an appointment from 'My Appointments' to view its live queue", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MyAppointmentsActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_logout -> {
                auth.signOut()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}