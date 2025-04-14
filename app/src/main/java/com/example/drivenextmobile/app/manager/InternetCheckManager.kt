package com.example.drivenextmobile.app.manager

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.drivenextmobile.ui.fragments.NoInternetActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
    Менеджер проверки интернет-соединения
 */
object InternetCheckManager {

    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun checkWithActivity(
        context: Context,
        onSuccess: () -> Unit
    ) {
        // Запуск корутины в фоновом потоке
        CoroutineScope(Dispatchers.IO).launch {
            val hasInternet = hasInternetConnection(context)

            // главный поток
            CoroutineScope(Dispatchers.Main).launch {
                if (hasInternet) {
                    onSuccess()
                } else {
                    context.startActivity(Intent(context, NoInternetActivity::class.java))
                }
            }
        }
    }
}