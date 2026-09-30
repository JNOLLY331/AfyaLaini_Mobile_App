package com.jnolly.AfyaLaini.model

data class Doctor(
    var id: String = "",
    var name: String = "",
    var specialty: String = "",
    var availableSlots: List<String> = emptyList(),
    var photoUrl: String = "",
    var createdAt: Long = System.currentTimeMillis()
)