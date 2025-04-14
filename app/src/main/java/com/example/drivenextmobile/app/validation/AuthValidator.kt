package com.example.drivenextmobile.app.validation

/** Валидация входа в аккаунт
test@test.ru 1111111A
пароль: не меньше 8 символов, минимум 1 цифра 1 буква
*/
object AuthValidator {
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("Email не может быть пустым")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Error("Введите корректный email")
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("Пароль не может быть пустым")
            password.length < 8 -> ValidationResult.Error("Пароль должен содержать минимум 8 символов")
            !password.any { it.isDigit() } -> ValidationResult.Error("Пароль должен содержать хотя бы одну цифру")
            !password.any { it.isLetter() } -> ValidationResult.Error("Пароль должен содержать хотя бы одну букву")
            else -> ValidationResult.Success
        }
    }
}