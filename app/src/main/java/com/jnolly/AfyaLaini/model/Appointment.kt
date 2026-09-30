package com.jnolly.AfyaLaini.model

data class Appointment(
    var id: String = "",
    var patientId: String = "",
    var patientName: String = "",
    var doctorId: String = "",
    var doctorName: String = "",
    var doctorSpecialty: String = "",
    var date: String = "",
    var timeSlot: String = "",
    var queueNumber: Int = 0,
    var status: String = "upcoming", // "upcoming", "completed", "cancelled"
    var createdAt: Long = System.currentTimeMillis()
)