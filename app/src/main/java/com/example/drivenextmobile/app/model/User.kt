package com.example.drivenextmobile.app.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class User(
    val id: Int? = null,
    val created_at: Instant? = null,
    val email: String,
    val password_hash: String,
    val first_name: String,
    val last_name: String,
    val middle_name: String? = null,
    val gender: String,
    val birth_date: String,
    val driver_license_number: String,
    val driver_license_issue_date: String,
    val personal_data_agreement: Boolean? = false
)