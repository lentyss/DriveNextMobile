package com.example.drivenextmobile.app.repository

import com.example.drivenextmobile.app.model.User
import com.example.drivenextmobile.app.utils.Supabase
import io.github.jan.supabase.postgrest.from

interface UserRepository {
    suspend fun findUserByEmail(email: String): User?
    suspend fun findUserByLicense(license: String): User?
    suspend fun registerUser(user: User): Boolean
}
/**
    Реализация UserRepository (CRUD)
**/
class UserRepositoryImpl(private val supabase: Supabase) : UserRepository {
    override suspend fun findUserByEmail(email: String): User? {
        return try {
            supabase.client.from("users")
                .select { filter { eq("email", email) } }
                .decodeSingleOrNull<User>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun findUserByLicense(license: String): User? {
        return try {
            supabase.client.from("users")
                .select { filter { eq("driver_license_number", license) } }
                .decodeSingleOrNull<User>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun registerUser(user: User): Boolean {
        return try {
            supabase.client.from("users").insert(user)
            true
        } catch (e: Exception) {
            false
        }
    }
}