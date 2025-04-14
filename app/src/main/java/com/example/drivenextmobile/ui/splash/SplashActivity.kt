package com.example.drivenextmobile.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenextmobile.app.manager.InternetCheckManager
import com.example.drivenextmobile.databinding.LoadingScreenBinding
import com.example.drivenextmobile.ui.login.LoginActivity
import com.example.drivenextmobile.ui.onboarding.OnboardingActivity
import com.example.drivenextmobile.ui.registration.RegistrationActivity
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: LoadingScreenBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemUI()
        setupClickListeners()
        observeEffects()

        if (intent.getBooleanExtra("SHOW_BUTTONS", false)) {
            showButtons()
        } else {
            hideButtons()
            viewModel.onEvent(SplashContract.Event.InitialCheckCompleted)
        }
    }

    // Настройка системного UI
    private fun setupSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Для API 30+ используем новый подход с WindowInsetsController
            window.insetsController?.let { controller ->
                controller.show(android.view.WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Для API 28-29 старый подход
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_VISIBLE
                    )
        }
    }

    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = { viewModel.onEvent(SplashContract.Event.LoginClicked) }
            )
        }

        binding.buttonRegistration.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = { viewModel.onEvent(SplashContract.Event.RegisterClicked) }
            )
        }
    }

    private fun observeEffects() {
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    SplashContract.Effect.NavigateToLogin -> navigateToLogin()
                    SplashContract.Effect.NavigateToRegister -> navigateToRegister()
                    SplashContract.Effect.NavigateToOnboarding -> navigateToOnboarding()
                }
            }
        }
    }

    private fun showButtons() {
        binding.buttonLogin.visibility = View.VISIBLE
        binding.buttonRegistration.visibility = View.VISIBLE
    }

    private fun hideButtons() {
        binding.buttonLogin.visibility = View.GONE
        binding.buttonRegistration.visibility = View.GONE
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }
}