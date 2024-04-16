package com.example.qrcodes.entity

data class MarkStudentOnPairRequest(
    val studentId: String? = null,
    val pairId: String? = null,
    val secret: String? = null
)
