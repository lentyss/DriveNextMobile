package com.example.drivenextmobile.ui.registration

import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.drivenextmobile.app.model.User
import com.example.drivenextmobile.app.usecase.RegistrationUseCase
import com.example.drivenextmobile.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel<Uri>(
    private val registrationUseCase: RegistrationUseCase
) : BaseViewModel<RegistrationContract.Event, RegistrationContract.State, RegistrationContract.Effect>() {

    private val _currentState = MutableStateFlow(RegistrationContract.State())
    val currentState: StateFlow<RegistrationContract.State> = _currentState.asStateFlow()

    private var userData = User(
        email = "",
        password_hash = "",
        first_name = "",
        last_name = "",
        gender = "",
        birth_date = "",
        driver_license_number = "",
        driver_license_issue_date = ""
    )

    private var profilePhotoUri: Uri? = null
    private var driverLicensePhotoUri: Uri? = null
    private var passportPhotoUri: Uri? = null

    override fun createInitialState(): RegistrationContract.State = RegistrationContract.State()

    private fun setState(update: RegistrationContract.State.() -> RegistrationContract.State) {
        _currentState.update(update)
    }

    override fun handleEvent(event: RegistrationContract.Event) {
        when (event) {
            is RegistrationContract.Event.EmailChanged -> {
                userData = userData.copy(email = event.email)
                setState { copy(emailError = null) }
            }
            is RegistrationContract.Event.PasswordChanged -> {
                userData = userData.copy(password_hash = hashPassword(event.password))
                setState { copy(passwordError = null) }
            }
            is RegistrationContract.Event.ConfirmPasswordChanged -> {
                val password = userData.password_hash
                val validation = registrationUseCase.validatePasswordConfirmation(
                    password = password,
                    confirmPassword = event.confirmPassword
                )
                setState {
                    copy(
                        confirmPasswordError = (validation as? RegistrationContract.ValidationResult.Error)?.message
                    )
                }
            }
            is RegistrationContract.Event.AgreementChanged -> {
                val validation = registrationUseCase.validateAgreement(event.agreed)
                setState {
                    copy(
                        agreementError = (validation as? RegistrationContract.ValidationResult.Error)?.message
                    )
                }
            }
            is RegistrationContract.Event.LastNameChanged -> {
                userData = userData.copy(last_name = event.lastName)
                setState { copy(lastNameError = null) }
            }
            is RegistrationContract.Event.FirstNameChanged -> {
                userData = userData.copy(first_name = event.firstName)
                setState { copy(firstNameError = null) }
            }
            is RegistrationContract.Event.MiddleNameChanged -> {
                userData = userData.copy(middle_name = event.middleName)
                setState { copy(middleNameError = null) }
            }
            is RegistrationContract.Event.GenderChanged -> {
                userData = userData.copy(gender = event.gender)
                setState { copy(genderError = null) }
            }
            is RegistrationContract.Event.BirthDateChanged -> {
                userData = userData.copy(birth_date = event.date)
                setState { copy(birthDateError = null) }
            }
            is RegistrationContract.Event.LicenseChanged -> {
                userData = userData.copy(driver_license_number = event.license)
                setState { copy(licenseError = null) }
            }
            is RegistrationContract.Event.LicenseDateChanged -> {
                userData = userData.copy(driver_license_issue_date = event.date)
                setState { copy(licenseDateError = null) }
            }
            is RegistrationContract.Event.ProfilePhotoSelected -> {
                profilePhotoUri = event.uri
                viewModelScope.launch {
                    setEffect {
                        RegistrationContract.Effect.UpdatePhotoView(
                            RegistrationContract.PhotoType.PROFILE,
                            event.uri
                        )
                    }
                }
            }
            is RegistrationContract.Event.LicensePhotoSelected -> {
                driverLicensePhotoUri = event.uri
                viewModelScope.launch {
                    setEffect {
                        RegistrationContract.Effect.UpdatePhotoView(
                            RegistrationContract.PhotoType.DRIVER_LICENSE,
                            event.uri
                        )
                    }
                }
            }
            is RegistrationContract.Event.PassportPhotoSelected -> {
                passportPhotoUri = event.uri
                viewModelScope.launch {
                    setEffect {
                        RegistrationContract.Effect.UpdatePhotoView(
                            RegistrationContract.PhotoType.PASSPORT,
                            event.uri
                        )
                    }
                }
            }
            RegistrationContract.Event.NextClicked -> handleNext()
            RegistrationContract.Event.BackClicked -> handleBack()
            RegistrationContract.Event.RegisterClicked -> registerUser()
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    private fun handleNext() {
        when (currentState.value.currentStep) {
            1 -> validateStep1()
            2 -> validateStep2()
            3 -> validateStep3()
        }
    }

    private fun validateStep1() {
        val validation = registrationUseCase.validateStep1(
            email = userData.email,
            password = userData.password_hash
        )

        if (validation.isValid) {
            setState { copy(currentStep = 2) }
        } else {
            setState {
                copy(
                    emailError = validation.emailError,
                    passwordError = validation.passwordError
                )
            }
        }
    }

    private fun validateStep2() {
        val validation = registrationUseCase.validateStep2(
            lastName = userData.last_name,
            firstName = userData.first_name,
            middleName = userData.middle_name,
            gender = userData.gender,
            birthDate = userData.birth_date
        )

        if (validation.isValid) {
            setState { copy(currentStep = 3) }
        } else {
            setState {
                copy(
                    lastNameError = validation.lastNameError,
                    firstNameError = validation.firstNameError,
                    middleNameError = validation.middleNameError,
                    genderError = validation.genderError,
                    birthDateError = validation.birthDateError
                )
            }
        }
    }

    private fun validateStep3() {
        val validation = registrationUseCase.validateStep3(
            license = userData.driver_license_number,
            licenseDate = userData.driver_license_issue_date,
            driverLicensePhoto = driverLicensePhotoUri,
            passportPhoto = passportPhotoUri
        )

        if (validation.isValid) {
            registerUser()
        } else {
            setState {
                copy(
                    licenseError = validation.licenseError,
                    licenseDateError = validation.licenseDateError,
                    driverLicensePhotoError = validation.driverLicensePhotoError,
                    passportPhotoError = validation.passportPhotoError
                )
            }
        }
    }

    private fun handleBack() {
        if (currentState.value.currentStep == 1) {
            viewModelScope.launch {
                setEffect { RegistrationContract.Effect.NavigateToLogin }
            }
        } else {
            setState { copy(currentStep = currentState.value.currentStep - 1) }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            when (val result = registrationUseCase.registerUser(userData)) {
                is RegistrationUseCase.RegistrationResult.Success -> {
                    setEffect { RegistrationContract.Effect.NavigateToSuccess }
                }
                is RegistrationUseCase.RegistrationResult.Error -> {
                    setEffect { RegistrationContract.Effect.ShowError(result.message) }
                }
            }

            setState { copy(isLoading = false) }
        }
    }
}