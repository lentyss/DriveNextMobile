package com.example.drivenextmobile.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenextmobile.R
import com.example.drivenextmobile.app.manager.InternetCheckManager
import com.example.drivenextmobile.app.usecase.LoginUseCase
import com.example.drivenextmobile.app.repository.UserRepositoryImpl
import com.example.drivenextmobile.app.utils.Supabase
import com.example.drivenextmobile.app.validation.AuthValidator
import com.example.drivenextmobile.app.validation.ValidationResult
import com.example.drivenextmobile.databinding.AlertFragmentBinding
import com.example.drivenextmobile.databinding.AuthScreenBinding
import com.example.drivenextmobile.ui.MainActivity
import com.example.drivenextmobile.ui.registration.RegistrationActivity
import com.example.drivenextmobile.ui.fragments.SuccessFragment
import com.example.drivenextmobile.ui.splash.SplashActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: AuthScreenBinding
    private lateinit var bindingAlert: AlertFragmentBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(LoginUseCase(UserRepositoryImpl(Supabase)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeEffects()
    }

    private fun setupClickListeners() {
        binding.buttonLoginnn.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = {
                    val email = binding.emailInnerTextField.text.toString()
                    val password = binding.passwordInnerTextField.text.toString()

                    val emailValidation = AuthValidator.validateEmail(email)
                    val passwordValidation = AuthValidator.validatePassword(password)

                    if (emailValidation is ValidationResult.Error) {
                        binding.emailInnerTextField.error = emailValidation.message
                        return@checkWithActivity
                    }

                    if (passwordValidation is ValidationResult.Error) {
                        binding.passwordInnerTextField.error = passwordValidation.message
                        return@checkWithActivity
                    }

                    viewModel.onEvent(LoginContract.Event.LoginButtonClicked(email, password))
                }
            )
        }

        binding.registrationView.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = { viewModel.onEvent(LoginContract.Event.RegistrationClicked) }
            )
        }

        binding.forgotPasswordTextView.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = { viewModel.onEvent(LoginContract.Event.ForgotPasswordClicked) }
            )
        }
    }

    private fun observeEffects() {
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    LoginContract.Effect.NavigateToMain -> navigateToMain()
                    LoginContract.Effect.NavigateToRegistration -> navigateToRegistration()
                    LoginContract.Effect.NavigateToForgotPassword -> navigateToForgotPassword()
                    LoginContract.Effect.NavigateToSplash -> navigateToSplash()
                    is LoginContract.Effect.ShowError -> showError(effect.message)
                    LoginContract.Effect.NavigateToSuccess -> navigateToSuccess()
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        viewModel.onEvent(LoginContract.Event.BackPressed)
        super.onBackPressed()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun navigateToRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    private fun navigateToForgotPassword() {
        Toast.makeText(this, "Функция в разработке", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSuccess() {
        setContentView(bindingAlert.root)
        bindingAlert.successTitle.visibility = View.VISIBLE
        bindingAlert.successLayout.visibility = View.VISIBLE
        bindingAlert.buttonNext.visibility = View.VISIBLE
        bindingAlert.buttonNext.text = getString(R.string.next_button)

        bindingAlert.errorTitle.visibility = View.GONE
        bindingAlert.errorLayout.visibility = View.GONE
    }

    private fun navigateToSplash() {
        startActivity(Intent(this, SplashActivity::class.java).apply {
            putExtra("SHOW_BUTTONS", true)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}