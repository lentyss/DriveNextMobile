package com.example.drivenextmobile.ui.registration

data class RegistrationState(
    val email: String,
    val password: String,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val dateOfBirth: String,
    val gender: String,
    val licenseNumber: String,
    val licenseIssueDate: String,
    val termsAccepted: Boolean = true
) {

    fun getFullName(): String {
        return listOfNotNull(lastName, firstName, middleName).joinToString(" ")
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "password" to password,
            "last_name" to lastName,
            "first_name" to firstName,
            "middle_name" to middleName,
            "birth_date" to dateOfBirth,
            "gender" to gender,
            "driver_license_number" to licenseNumber,
            "driver_license_issue_date" to licenseIssueDate,
            "personal_data_agreement" to termsAccepted
        )
    }
}