package com.jnolly.AfyaLaini.utils

import android.text.TextUtils
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object SecurityUtils {
    
    // Input validation patterns
    private const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    private const val PHONE_PATTERN = "^[+]?[0-9]{10,15}$"
    private const val NAME_PATTERN = "^[A-Za-z\\s'-]{2,50}$"
    
    // Sanitization
    fun sanitizeInput(input: String?): String {
        return input?.trim()?.replace("[<>\"&]".toRegex(), "") ?: ""
    }
    
    fun sanitizeHtml(input: String?): String {
        return input?.trim()?.replace("<", "<")?.replace(">", ">") ?: ""
    }
    
    // Validation
    fun isValidEmail(email: String?): Boolean {
        return email != null && TextUtils.isEmpty(email).not() && email.matches(EMAIL_PATTERN.toRegex())
    }
    
    fun isValidPhone(phone: String?): Boolean {
        return phone == null || TextUtils.isEmpty(phone) || phone.matches(PHONE_PATTERN.toRegex())
    }
    
    fun isValidName(name: String?): Boolean {
        return name != null && TextUtils.isEmpty(name).not() && name.matches(NAME_PATTERN.toRegex())
    }
    
    fun isValidPassword(password: String?): Boolean {
        return password != null && TextUtils.isEmpty(password).not() && password.length >= 6
    }
    
    fun validateRequired(field: EditText, fieldName: String): Boolean {
        val text = field.text.toString().trim()
        if (TextUtils.isEmpty(text)) {
            field.error = "$fieldName is required"
            field.requestFocus()
            return false
        }
        field.error = null
        return true
    }
    
    // Role-Based Access Control
    enum class UserRole {
        PATIENT, STAFF, UNKNOWN
    }
    
    interface RoleCallback {
        fun onRoleDetermined(role: UserRole)
    }
    
    fun getCurrentUserRole(callback: RoleCallback) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            callback.onRoleDetermined(UserRole.UNKNOWN)
            return
        }
        
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")
                val roleStr = role?.lowercase() ?: "patient"
                callback.onRoleDetermined(when (roleStr) {
                    "staff" -> UserRole.STAFF
                    else -> UserRole.PATIENT
                })
            }
            .addOnFailureListener { 
                callback.onRoleDetermined(UserRole.UNKNOWN)
            }
    }
    
    fun requireRole(requiredRole: UserRole, callback: RoleCallback) {
        getCurrentUserRole(object : RoleCallback {
            override fun onRoleDetermined(role: UserRole) {
                if (role == requiredRole || role == UserRole.STAFF) { // Staff can access patient features
                    callback.onRoleDetermined(role)
                } else {
                    callback.onRoleDetermined(UserRole.UNKNOWN)
                }
            }
        })
    }
    
    // Firestore query helpers with RBAC
    fun getPatientAppointmentsQuery(patientId: String) = FirebaseFirestore.getInstance()
        .collection("appointments")
        .whereEqualTo("patientId", patientId)
    
    fun getStaffAppointmentsQuery(doctorId: String? = null) = FirebaseFirestore.getInstance()
        .collection("appointments")
        .apply { if (doctorId != null) whereEqualTo("doctorId", doctorId) }
    
    fun getPatientDataQuery(patientId: String) = FirebaseFirestore.getInstance()
        .collection("users")
        .document(patientId)
    
    // Token validation for state changes
    fun validateAuthToken(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}