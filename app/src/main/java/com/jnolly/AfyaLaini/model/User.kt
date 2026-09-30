package com.jnolly.AfyaLaini.model

data class User(
    var uid: String = "",
    var name: String = "",
    var role: String = "patient", // "patient" or "staff"
    var email: String = "",
    var phone: String = "",
    var createdAt: Long = System.currentTimeMillis()
)