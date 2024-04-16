package com.example.qrcodes.entity


data class CreatePairRequest(
    val lecturerId: String? = null,
    val startedAt: String? = null,
    val endAt: String? = null,
    val pairName: String? = null
)

