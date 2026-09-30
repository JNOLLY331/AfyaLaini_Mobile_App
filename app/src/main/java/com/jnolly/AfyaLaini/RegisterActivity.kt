package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.jnolly.AfyaLaini.model.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var selectedRole = "patient"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupRole)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedRole = if (checkedIds.contains(R.id.chipStaff)) "staff" else "patient"
        }

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Please enter your full name"
                etName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Please enter a valid email address"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            btnCreate.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user
                    val uid = firebaseUser?.uid
                    if (uid == null) {
                        progressBar.visibility = View.GONE
                        btnCreate.isEnabled = true
                        Toast.makeText(this, "Registration failed. User ID missing.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Update Auth profile display name
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    firebaseUser.updateProfile(profileUpdates)

                    val user = User(uid = uid, name = name, role = selectedRole, email = email, phone = phone)
                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            progressBar.visibility = View.GONE
                            btnCreate.isEnabled = true
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            val destination = if (selectedRole == "staff") StaffHomeActivity::class.java else HomeActivity::class.java
                            startActivity(Intent(this, destination))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            progressBar.visibility = View.GONE
                            btnCreate.isEnabled = true
                            Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    btnCreate.isEnabled = true
                    Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}