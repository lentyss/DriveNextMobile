package com.example.drivenextmobile.ui.registration

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.*
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.drivenextmobile.app.model.User
import com.example.drivenextmobile.app.repository.UserRepository
import com.example.drivenextmobile.app.repository.UserRepositoryImpl
import com.example.drivenextmobile.app.utils.Supabase
import kotlinx.coroutines.launch


class RegistrationViewModel(
    private val repository: UserRepository = UserRepositoryImpl(Supabase)
) : ViewModel() {
    private val _currentStep = MutableLiveData(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _photosValid = MutableLiveData<Boolean>()
    val photosValid: LiveData<Boolean> = _photosValid

    // Данные пользователя
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val middleName = MutableLiveData<String?>()
    val dateOfBirth = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val licenseNumber = MutableLiveData<String>()
    val licenseIssueDate = MutableLiveData<String>()

    fun validatePhotos(driverLicenseUri: Uri?, passportUri: Uri?) {
        val driverLicenseValid = driverLicenseUri != null
        val passportValid = passportUri != null

        _photosValid.value = driverLicenseValid && passportValid
    }

    fun completeRegistration() {
        viewModelScope.launch {
            // Проверка email
            repository.findUserByEmail(email.value!!)?.let {
                _error.value = "Пользователь с таким email уже существует"
                return@launch
            }

            // Проверка прав
            repository.findUserByLicense(licenseNumber.value!!)?.let {
                _error.value = "Пользователь с такими правами уже существует"
                return@launch
            }

            // Создание пользователя
            val user = User(
                email = email.value!!,
                password_hash = hashPassword(password.value!!),
                first_name = firstName.value!!,
                last_name = lastName.value!!,
                middle_name = middleName.value,
                gender = gender.value!!,
                birth_date = dateOfBirth.value!!,
                driver_license_number = licenseNumber.value!!,
                driver_license_issue_date = licenseIssueDate.value!!
            )

            if (repository.registerUser(user)) {
                _success.value = true
            } else {
                _error.value = "Ошибка при регистрации"
            }
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun goToNextStep() {
        _currentStep.value = _currentStep.value?.plus(1)
    }

    fun goToPreviousStep() {
        _currentStep.value = _currentStep.value?.minus(1)
    }
}