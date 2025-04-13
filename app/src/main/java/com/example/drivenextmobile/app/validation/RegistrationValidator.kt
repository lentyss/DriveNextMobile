package com.example.drivenextmobile.app.validation

import android.net.Uri
import com.example.drivenextmobile.app.model.User
import java.util.Calendar
import java.util.regex.Pattern

/* Валидация регистрации аккаунта
*  test@test.ru 1111111a
*  Тест 01.01.2000
*  0000000000 01.01.2000
*  пароль: >= 8 символов, минимум 1 цифра 1 буква
*  дата рождения/удостоверение: 18 <= && <= 120 лет, корректные даты
*  номер водительского удостоверения: 10 символов
*  обязательно все, кроме отчества и фото профиля
*/
object RegistrationValidator {
    // Шаг 1
    fun validateEmail(email: String): ValidationResult {
        return AuthValidator.validateEmail(email)
    }

    fun validatePassword(password: String): ValidationResult {
        return AuthValidator.validatePassword(password)
    }

    fun validatePasswordConfirmation(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult.Error("Подтвердите пароль")
            password != confirmPassword -> ValidationResult.Error("Пароли не совпадают")
            else -> ValidationResult.Success
        }
    }

    fun validateAgreement(agreed: Boolean): ValidationResult {
        return if (!agreed) ValidationResult.Error("Необходимо согласие на обработку данных")
        else ValidationResult.Success
    }

    // Шаг 2
    fun validateName(name: String, fieldName: String): ValidationResult {
        val namePattern = Pattern.compile("^[а-яА-ЯёЁa-zA-Z-]+\$")
        return when {
            name.isBlank() -> ValidationResult.Error("$fieldName не может быть пустым")
            name.any { it.isDigit() } -> ValidationResult.Error("$fieldName не должен содержать цифр")
            !namePattern.matcher(name).matches() -> ValidationResult.Error("$fieldName содержит недопустимые символы")
            else -> ValidationResult.Success
        }
    }

    fun validateGender(gender: String?): ValidationResult {
        return if (gender.isNullOrBlank()) ValidationResult.Error("Укажите пол")
        else ValidationResult.Success
    }

    fun validateBirthDate(date: String): ValidationResult {
        if (date.isBlank()) return ValidationResult.Error("Дата рождения не может быть пустой")

        return try {
            val parts = date.split('.')
            if (parts.size != 3 || parts.any { it.isBlank() }) {
                return ValidationResult.Error("Некорректный формат даты. Используйте ДД.ММ.ГГГГ")
            }

            val day = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val year = parts[2].toInt()

            // Проверка валидности даты
            if (month !in 0..11) return ValidationResult.Error("Некорректный месяц (01-12)")

            val calendar = Calendar.getInstance()
            calendar.setLenient(false)
            calendar.set(year, month, day)
            calendar.timeInMillis // Бросит исключение если дата невалидна

            // Проверка возраста (18+)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (currentYear - year < 18) {
                return ValidationResult.Error("Вам должно быть больше 18 лет")
            }

            // Проверка на разумные пределы (не старше 120 лет)
            if (currentYear - year > 120) {
                return ValidationResult.Error("Проверьте дату рождения")
            }

            // Проверка дней в месяце
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (day !in 1..maxDay) {
                return ValidationResult.Error("Некорректный день для этого месяца")
            }

            ValidationResult.Success
        } catch (e: Exception) {
            ValidationResult.Error("Некорректная дата рождения. Используйте формат ДД.ММ.ГГГГ")
        }
    }

    // Шаг 3
    fun validateDriverLicense(license: String): ValidationResult {
        return when {
            license.isBlank() -> ValidationResult.Error("Номер водительского удостоверения не может быть пустым")
            license.length != 10 -> ValidationResult.Error("Номер должен содержать 10 символов")
            else -> ValidationResult.Success
        }
    }

    fun validateLicenseIssueDate(date: String): ValidationResult {
        if (date.isBlank()) return ValidationResult.Error("Дата выдачи не может быть пустой")

        return try {
            val parts = date.split('.')
            if (parts.size != 3 || parts.any { it.isBlank() }) {
                return ValidationResult.Error("Некорректный формат даты. Используйте ДД.ММ.ГГГГ")
            }

            val day = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val year = parts[2].toInt()

            // Проверка валидности даты
            if (month !in 0..11) return ValidationResult.Error("Некорректный месяц (01-12)")

            val calendar = Calendar.getInstance()
            calendar.setLenient(false)
            calendar.set(year, month, day)
            calendar.timeInMillis

            // Проверка что дата не в будущем
            if (calendar.after(Calendar.getInstance())) {
                return ValidationResult.Error("Дата выдачи не может быть в будущем")
            }

            // Проверка на разумные пределы (не старше 100 лет)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (currentYear - year > 100) {
                return ValidationResult.Error("Проверьте дату выдачи")
            }

            // Проверка дней в месяце
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (day !in 1..maxDay) {
                return ValidationResult.Error("Некорректный день для этого месяца")
            }

            ValidationResult.Success
        } catch (e: Exception) {
            ValidationResult.Error("Некорректная дата выдачи. Используйте формат ДД.ММ.ГГГГ")
        }
    }
    fun validateProfilePhoto(uri: Uri?): ValidationResult {
        // Фото профиля необязательно
        return ValidationResult.Success
    }

    fun validateDriverLicensePhoto(uri: Uri?): ValidationResult {
        return if (uri == null) {
            ValidationResult.Error("Загрузите фото водительского удостоверения")
        } else {
            ValidationResult.Success
        }
    }

    fun validatePassportPhoto(uri: Uri?): ValidationResult {
        return if (uri == null) {
            ValidationResult.Error("Загрузите фото паспорта")
        } else {
            ValidationResult.Success
        }
    }

    data class FullValidationResult(
        val isValid: Boolean,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val agreementError: String? = null,
        val lastNameError: String? = null,
        val firstNameError: String? = null,
        val middleNameError: String? = null,
        val genderError: String? = null,
        val birthDateError: String? = null,
        val licenseError: String? = null,
        val licenseDateError: String? = null,
        val driverLicensePhotoError: String? = null,
        val passportPhotoError: String? = null
    )

    fun validateAll(
        email: String,
        password: String,
        confirmPassword: String,
        agreed: Boolean,
        lastName: String,
        firstName: String,
        middleName: String?,
        gender: String?,
        birthDate: String,
        license: String,
        licenseDate: String,
        driverLicensePhoto: Uri?,
        passportPhoto: Uri?
    ): FullValidationResult {
        val emailResult = validateEmail(email)
        val passwordResult = validatePassword(password)
        val confirmResult = validatePasswordConfirmation(password, confirmPassword)
        val agreementResult = validateAgreement(agreed)
        val lastNameResult = validateName(lastName, "Фамилия")
        val firstNameResult = validateName(firstName, "Имя")
        val middleNameResult = if (!middleName.isNullOrBlank()) validateName(middleName, "Отчество") else ValidationResult.Success
        val genderResult = validateGender(gender)
        val birthDateResult = validateBirthDate(birthDate)
        val licenseResult = validateDriverLicense(license)
        val licenseDateResult = validateLicenseIssueDate(licenseDate)
        val driverLicensePhotoResult = validateDriverLicensePhoto(driverLicensePhoto)
        val passportPhotoResult = validatePassportPhoto(passportPhoto)

        return FullValidationResult(
            isValid = emailResult is ValidationResult.Success &&
                    passwordResult is ValidationResult.Success &&
                    confirmResult is ValidationResult.Success &&
                    agreementResult is ValidationResult.Success &&
                    lastNameResult is ValidationResult.Success &&
                    firstNameResult is ValidationResult.Success &&
                    middleNameResult is ValidationResult.Success &&
                    genderResult is ValidationResult.Success &&
                    birthDateResult is ValidationResult.Success &&
                    licenseResult is ValidationResult.Success &&
                    licenseDateResult is ValidationResult.Success &&
                    driverLicensePhotoResult is ValidationResult.Success &&
                    passportPhotoResult is ValidationResult.Success,
            emailError = (emailResult as? ValidationResult.Error)?.message,
            passwordError = (passwordResult as? ValidationResult.Error)?.message,
            confirmPasswordError = (confirmResult as? ValidationResult.Error)?.message,
            agreementError = (agreementResult as? ValidationResult.Error)?.message,
            lastNameError = (lastNameResult as? ValidationResult.Error)?.message,
            firstNameError = (firstNameResult as? ValidationResult.Error)?.message,
            middleNameError = (middleNameResult as? ValidationResult.Error)?.message,
            genderError = (genderResult as? ValidationResult.Error)?.message,
            birthDateError = (birthDateResult as? ValidationResult.Error)?.message,
            licenseError = (licenseResult as? ValidationResult.Error)?.message,
            licenseDateError = (licenseDateResult as? ValidationResult.Error)?.message,
            driverLicensePhotoError = (driverLicensePhotoResult as? ValidationResult.Error)?.message,
            passportPhotoError = (passportPhotoResult as? ValidationResult.Error)?.message
        )
    }
}