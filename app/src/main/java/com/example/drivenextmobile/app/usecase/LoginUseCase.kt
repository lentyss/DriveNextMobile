package com.example.drivenextmobile.app.usecase

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.drivenextmobile.app.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    sealed class LoginResult {
        data object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    suspend fun execute(email: String, password: String): LoginResult {
        return try {
            val user = userRepository.findUserByEmail(email) ?:
            return LoginResult.Error("Пользователь не найден")

            if (!BCrypt.verifyer().verify(password.toCharArray(), user.password_hash).verified) {
                return LoginResult.Error("Неверный пароль")
            }

            LoginResult.Success
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }
}