package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
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

        findViewById<Chip>(R.id.chipPatient).setOnClickListener { selectedRole = "patient" }
        findViewById<Chip>(R.id.chipStaff).setOnClickListener { selectedRole = "staff" }

        findViewById<Button>(R.id.btnCreateAccount).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString().trim()
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()
            val phone = findViewById<EditText>(R.id.etPhone).text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Fill all fields (password needs 6+ characters)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    val user = User(uid = uid, name = name, role = selectedRole, email = email, phone = phone)
                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            val destination = if (selectedRole == "staff") StaffHomeActivity::class.java else HomeActivity::class.java
                            startActivity(Intent(this, destination))
                            finish()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}