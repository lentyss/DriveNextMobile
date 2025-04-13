package com.example.drivenextmobile.app.policy

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConnectivityPolicy(private val context: Context) {

    /**
     * Проверяет доступность интернета асинхронно
     * @return Boolean - true если интернет доступен, false если нет
     */
    suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return@withContext false

        val network = connectivityManager.activeNetwork ?: return@withContext false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return@withContext false

        return@withContext capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Повторяет попытку подключения с заданным интервалом
     * @param maxAttempts - максимальное количество попыток
     * @param intervalMillis - интервал между попытками в миллисекундах
     * @param onSuccess - коллбек при успешном подключении
     * @param onFailure - коллбек после исчерпания попыток
     */
    suspend fun retryConnection(
        maxAttempts: Int = 3,
        intervalMillis: Long = 1000,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        repeat(maxAttempts) { attempt ->
            if (isInternetAvailable()) {
                onSuccess()
                return
            }

            if (attempt < maxAttempts - 1) {
                kotlinx.coroutines.delay(intervalMillis)
            }
        }

        onFailure()
    }
}