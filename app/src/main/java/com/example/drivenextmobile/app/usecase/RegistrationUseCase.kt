package com.example.drivenextmobile.app.usecase

import android.net.Uri
import com.example.drivenextmobile.app.model.User
import com.example.drivenextmobile.app.repository.UserRepository
import com.example.drivenextmobile.app.validation.RegistrationValidator
import com.example.drivenextmobile.app.validation.ValidationResult

class RegistrationUseCase(
    private val userRepository: UserRepository
) {
    sealed class RegistrationResult {
        data object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }

    data class Step1Validation(
        val isValid: Boolean,
        val emailError: String?,
        val passwordError: String?
    )

    data class Step2Validation(
        val isValid: Boolean,
        val lastNameError: String?,
        val firstNameError: String?,
        val middleNameError: String?,
        val genderError: String?,
        val birthDateError: String?
    )

    data class Step3Validation(
        val isValid: Boolean,
        val licenseError: String?,
        val licenseDateError: String?,
        val driverLicensePhotoError: String?,
        val passportPhotoError: String?
    )

    suspend fun registerUser(user: User): RegistrationResult {
        return try {
            // Проверка существования пользователя
            if (userRepository.findUserByEmail(user.email) != null) {
                return RegistrationResult.Error("Пользователь с таким email уже существует")
            }

            if (userRepository.findUserByLicense(user.driver_license_number) != null) {
                return RegistrationResult.Error("Пользователь с такими правами уже существует")
            }

            if (userRepository.registerUser(user)) {
                RegistrationResult.Success
            } else {
                RegistrationResult.Error("Ошибка при регистрации")
            }
        } catch (e: Exception) {
            RegistrationResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }

    fun validateStep1(email: String, password: String): Step1Validation {
        val emailValidation = RegistrationValidator.validateEmail(email)
        val passwordValidation = RegistrationValidator.validatePassword(password)

        return Step1Validation(
            isValid = emailValidation is ValidationResult.Success &&
                    passwordValidation is ValidationResult.Success,
            emailError = (emailValidation as? ValidationResult.Error)?.message,
            passwordError = (passwordValidation as? ValidationResult.Error)?.message
        )
    }

    fun validateStep2(
        lastName: String,
        firstName: String,
        middleName: String?,
        gender: String?,
        birthDate: String
    ): Step2Validation {
        val lastNameResult = RegistrationValidator.validateName(lastName, "Фамилия")
        val firstNameResult = RegistrationValidator.validateName(firstName, "Имя")
        val middleNameResult = if (!middleName.isNullOrBlank()) RegistrationValidator.validateName(middleName, "Отчество") else ValidationResult.Success
        val genderResult = RegistrationValidator.validateGender(gender)
        val birthDateResult = RegistrationValidator.validateBirthDate(birthDate)

        return Step2Validation(
            isValid = lastNameResult is ValidationResult.Success &&
                    firstNameResult is ValidationResult.Success &&
                    middleNameResult is ValidationResult.Success &&
                    genderResult is ValidationResult.Success &&
                    birthDateResult is ValidationResult.Success,
            lastNameError = (lastNameResult as? ValidationResult.Error)?.message,
            firstNameError = (firstNameResult as? ValidationResult.Error)?.message,
            middleNameError = (middleNameResult as? ValidationResult.Error)?.message,
            genderError = (genderResult as? ValidationResult.Error)?.message,
            birthDateError = (birthDateResult as? ValidationResult.Error)?.message
        )
    }

    fun validateStep3(
        license: String,
        licenseDate: String,
        driverLicensePhoto: Uri?,
        passportPhoto: Uri?
    ): Step3Validation {
        val licenseResult = RegistrationValidator.validateDriverLicense(license)
        val licenseDateResult = RegistrationValidator.validateLicenseIssueDate(licenseDate)
        val driverLicensePhotoResult = RegistrationValidator.validateDriverLicensePhoto(driverLicensePhoto)
        val passportPhotoResult = RegistrationValidator.validatePassportPhoto(passportPhoto)

        return Step3Validation(
            isValid = licenseResult is ValidationResult.Success &&
                    licenseDateResult is ValidationResult.Success &&
                    driverLicensePhotoResult is ValidationResult.Success &&
                    passportPhotoResult is ValidationResult.Success,
            licenseError = (licenseResult as? ValidationResult.Error)?.message,
            licenseDateError = (licenseDateResult as? ValidationResult.Error)?.message,
            driverLicensePhotoError = (driverLicensePhotoResult as? ValidationResult.Error)?.message,
            passportPhotoError = (passportPhotoResult as? ValidationResult.Error)?.message
        )
    }
}