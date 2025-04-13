package com.example.drivenextmobile.ui.registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenextmobile.R
import com.example.drivenextmobile.app.repository.UserRepositoryImpl
import com.example.drivenextmobile.app.usecase.RegistrationUseCase
import com.example.drivenextmobile.app.utils.Supabase
import com.example.drivenextmobile.databinding.RegistrationScreenBinding
import com.example.drivenextmobile.ui.login.LoginActivity
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: RegistrationScreenBinding
    private val viewModel: RegistrationViewModel<Any?> by viewModels {
        RegistrationViewModelFactory(
            RegistrationUseCase(UserRepositoryImpl(Supabase))
        )
    }

    private var currentPhotoType: RegistrationContract.PhotoType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeState()
        observeEffects()
    }

    private fun setupClickListeners() {
        binding.buttonNext.setOnClickListener {
            viewModel.onEvent(RegistrationContract.Event.NextClicked)
        }

        binding.arrowBack.setOnClickListener {
            viewModel.onEvent(RegistrationContract.Event.BackClicked)
        }

        binding.emailInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.EmailChanged(
                        binding.emailInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.passwordInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.PasswordChanged(
                        binding.passwordInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.passwordRetryInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.ConfirmPasswordChanged(
                        binding.passwordRetryInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onEvent(RegistrationContract.Event.AgreementChanged(isChecked))
        }

        binding.surnameInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.LastNameChanged(
                        binding.surnameInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.nameInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.FirstNameChanged(
                        binding.nameInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.lastnameInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.MiddleNameChanged(
                        binding.lastnameInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val gender = when (checkedId) {
                R.id.maleRadioButton -> "male"
                R.id.femaleRadioButton -> "female"
                else -> null
            }
            gender?.let {
                viewModel.onEvent(RegistrationContract.Event.GenderChanged(it))
            }
        }

        binding.birthdayInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.BirthDateChanged(
                        binding.birthdayInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.driverLicenseInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.LicenseChanged(
                        binding.driverLicenseInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.driverLicenseDateInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.onEvent(
                    RegistrationContract.Event.LicenseDateChanged(
                        binding.driverLicenseDateInput.editText?.text.toString()
                    )
                )
            }
        }

        binding.partOfProfilePhoto.setOnClickListener {
            currentPhotoType = RegistrationContract.PhotoType.PROFILE
            showImagePickerDialog()
        }

        binding.driverLicensePhotoIcon.setOnClickListener {
            currentPhotoType = RegistrationContract.PhotoType.DRIVER_LICENSE
            showImagePickerDialog()
        }

        binding.passportPhotoIcon.setOnClickListener {
            currentPhotoType = RegistrationContract.PhotoType.PASSPORT
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        // Реализация выбора изображения
        Toast.makeText(this, "Выберите изображение", Toast.LENGTH_SHORT).show()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                updateUI(state)
            }
        }
    }

    private fun observeEffects() {
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    RegistrationContract.Effect.NavigateToNextStep -> {
                        viewModel.onEvent(RegistrationContract.Event.NextClicked)
                    }
                    RegistrationContract.Effect.NavigateToPreviousStep -> {
                        viewModel.onEvent(RegistrationContract.Event.BackClicked)
                    }
                    RegistrationContract.Effect.NavigateToLogin -> {
                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                        finish()
                    }
                    RegistrationContract.Effect.NavigateToSuccess -> {
                        startActivity(Intent(this@RegistrationActivity, SuccessFragment::class.java))
                        finish()
                    }
                    is RegistrationContract.Effect.ShowError -> {
                        showError(effect.message)
                    }
                    is RegistrationContract.Effect.UpdatePhotoView -> {
                        updatePhotoView(effect.type, effect.uri)
                    }
                }
            }
        }
    }

    private fun updateUI(state: RegistrationContract.State) {
        binding.step1.visibility = if (state.currentStep == 1) View.VISIBLE else View.GONE
        binding.step2.visibility = if (state.currentStep == 2) View.VISIBLE else View.GONE
        binding.step3.visibility = if (state.currentStep == 3) View.VISIBLE else View.GONE

        binding.buttonNext.text = when (state.currentStep) {
            3 -> getString(R.string.register_button)
            else -> getString(R.string.next_button)
        }

        // Обновление ошибок
        binding.emailInput.error = state.emailError
        binding.passwordInput.error = state.passwordError
        binding.passwordRetryInput.error = state.confirmPasswordError
        binding.termsCheckBox.error = state.agreementError
        binding.surnameInput.error = state.lastNameError
        binding.nameInput.error = state.firstNameError
        binding.lastnameInput.error = state.middleNameError
        binding.genderErrorTextView.text = state.genderError
        binding.genderErrorTextView.visibility = if (state.genderError != null) View.VISIBLE else View.GONE
        binding.birthdayInput.error = state.birthDateError
        binding.driverLicenseInput.error = state.licenseError
        binding.driverLicenseDateInput.error = state.licenseDateError
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updatePhotoView(type: RegistrationContract.PhotoType, uri: Uri?) {
        when (type) {
            RegistrationContract.PhotoType.PROFILE -> {
                binding.profilePhoto.setImageURI(uri)
                if (uri != null) {
                    binding.partOfProfilePhoto.visibility = View.GONE
                }
            }
            RegistrationContract.PhotoType.DRIVER_LICENSE -> {
                binding.driverLicensePhotoIcon.setImageURI(uri)
                binding.driverLicensePhotoText.text = if (uri != null) {
                    getString(R.string.photo_selected)
                } else {
                    getString(R.string.upload_photo)
                }
            }
            RegistrationContract.PhotoType.PASSPORT -> {
                binding.passportPhotoIcon.setImageURI(uri)
                binding.passportPhotoText.text = if (uri != null) {
                    getString(R.string.photo_selected)
                } else {
                    getString(R.string.upload_photo)
                }
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onEvent(RegistrationContract.Event.BackClicked)
        super.onBackPressed()
    }
}