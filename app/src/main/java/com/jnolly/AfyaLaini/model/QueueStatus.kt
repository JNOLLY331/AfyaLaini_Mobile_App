package com.jnolly.AfyaLaini.model

data class QueueStatus(
    var doctorId: String = "",
    var date: String = "",
    var lastIssuedNumber: Int = 0,
    var nowServing: Int = 0
)