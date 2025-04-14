package com.example.drivenextmobile.ui.registration

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.drivenextmobile.R
import com.example.drivenextmobile.app.validation.RegistrationValidator
import com.example.drivenextmobile.app.validation.ValidationResult
import com.example.drivenextmobile.databinding.AlertFragmentBinding
import com.example.drivenextmobile.databinding.RegistrationScreenBinding
import com.example.drivenextmobile.ui.splash.SplashActivity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import com.example.drivenextmobile.app.manager.InternetCheckManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var binding: RegistrationScreenBinding
    private lateinit var bindingAlert: AlertFragmentBinding

    private var currentPhotoType: PhotoType? = null
    private var currentPhotoUri: Uri? = null
    private var profilePhotoUri: Uri? = null
    private var driverLicensePhotoUri: Uri? = null
    private var passportPhotoUri: Uri? = null

    // Определение типа загружаемого фото
    private enum class PhotoType {
        PROFILE, DRIVER_LICENSE, PASSPORT
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            when (currentPhotoType) {
                PhotoType.PROFILE -> openGallery()
                PhotoType.DRIVER_LICENSE -> openGallery()
                PhotoType.PASSPORT -> openGallery()
                null -> {}
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    when (currentPhotoType) {
                        PhotoType.PROFILE -> {
                            binding.profilePhoto.setImageURI(uri)
                            profilePhotoUri = uri
                            binding.partOfProfilePhoto.visibility = View.GONE
                        }
                        PhotoType.DRIVER_LICENSE -> {
                            binding.driverLicensePhotoIcon.apply {
                                setImageURI(uri)
                                scaleType = ImageView.ScaleType.CENTER_INSIDE  // Изменено на CENTER_INSIDE
                                adjustViewBounds = true
                            }
                            driverLicensePhotoUri = uri
                            binding.driverLicensePhotoText.text = getString(R.string.photo_selected)
                        }
                        PhotoType.PASSPORT -> {
                            binding.passportPhotoIcon.apply {
                                setImageURI(uri)
                                scaleType = ImageView.ScaleType.CENTER_INSIDE  // Изменено на CENTER_INSIDE
                                adjustViewBounds = true
                            }
                            passportPhotoUri = uri
                            binding.passportPhotoText.text = getString(R.string.photo_selected)
                        }
                        null -> {}
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri ->
                try {
                    when (currentPhotoType) {
                        PhotoType.PROFILE -> {
                            binding.profilePhoto.setImageURI(uri)
                            profilePhotoUri = uri
                            binding.partOfProfilePhoto.visibility = View.GONE
                        }
                        PhotoType.DRIVER_LICENSE -> {
                            binding.driverLicensePhotoIcon.apply {
                                setImageURI(uri)
                                scaleType = ImageView.ScaleType.CENTER_INSIDE  // Изменено на CENTER_INSIDE
                                adjustViewBounds = true
                            }
                            driverLicensePhotoUri = uri
                            binding.driverLicensePhotoText.text = getString(R.string.photo_selected)
                        }
                        PhotoType.PASSPORT -> {
                            binding.passportPhotoIcon.apply {
                                setImageURI(uri)
                                scaleType = ImageView.ScaleType.CENTER_INSIDE  // Изменено на CENTER_INSIDE
                                adjustViewBounds = true
                            }
                            passportPhotoUri = uri
                            binding.passportPhotoText.text = getString(R.string.photo_selected)
                        }
                        null -> {}
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationScreenBinding.inflate(layoutInflater)
        bindingAlert = AlertFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImageViews()

        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        viewModel.success.observe(this) { success ->
            if (success) showSuccess()
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.currentStep.observe(this) { step ->
            updateUIForStep(step)
        }

        binding.buttonNext.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = {
                    when (viewModel.currentStep.value) {
                        1 -> validateStep1()
                        2 -> validateStep2()
                        3 -> validateStep3()
                    }
                }
            )
        }

        binding.arrowBack.setOnClickListener {
            InternetCheckManager.checkWithActivity(
                context = this,
                onSuccess = {
                    if (viewModel.currentStep.value == 1) {
                        val intent = Intent(this, SplashActivity::class.java).apply {
                            putExtra("SHOW_BUTTONS", true)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        viewModel.goToPreviousStep()
                    }
                }
            )
        }

        setupPhotoClickListeners()
    }

    private fun setupImageViews() {
        binding.profilePhoto.apply {
            background = ContextCompat.getDrawable(this@RegistrationActivity, R.drawable.rounded_corner)
            clipToOutline = true
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        binding.driverLicensePhotoIcon.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.passportPhotoIcon.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    // Обработка кликов по фото
    private fun setupPhotoClickListeners() {
        binding.partOfProfilePhoto.setOnClickListener {
            currentPhotoType = PhotoType.PROFILE
            showImagePickerDialog()
        }

        binding.driverLicensePhotoIcon.setOnClickListener {
            currentPhotoType = PhotoType.DRIVER_LICENSE
            showImagePickerDialog()
        }

        binding.passportPhotoIcon.setOnClickListener {
            currentPhotoType = PhotoType.PASSPORT
            showImagePickerDialog()
        }
    }

    // Диалог выбора источника фото
    private fun showImagePickerDialog() {
        val options = arrayOf("Камера", "Галерея")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    // Запрос разрешений и открытие галереи
    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                launchGalleryIntent()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchGalleryIntent()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    // Запрос разрешений и открытие камеры
    private fun openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCameraIntent()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCameraIntent() {
        val photoFile = createImageFile()
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        cameraLauncher.launch(cameraIntent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun validateStep1() {
        val email = binding.emailInputLayout.editText?.text.toString()
        val password = binding.passwordInputLayout.editText?.text.toString()
        val confirmPassword = binding.passwordRetryInputLayout.editText?.text.toString()
        val agreed = binding.termsCheckBox.isChecked

        val emailValidation = RegistrationValidator.validateEmail(email)
        val passwordValidation = RegistrationValidator.validatePassword(password)
        val confirmValidation = RegistrationValidator.validatePasswordConfirmation(password, confirmPassword)
        val agreementValidation = RegistrationValidator.validateAgreement(agreed)

        binding.emailInputLayout.error = (emailValidation as? ValidationResult.Error)?.message
        binding.passwordInputLayout.error = (passwordValidation as? ValidationResult.Error)?.message
        binding.passwordRetryInputLayout.error = (confirmValidation as? ValidationResult.Error)?.message
        binding.termsCheckBox.error = (agreementValidation as? ValidationResult.Error)?.message

        if (emailValidation is ValidationResult.Success &&
            passwordValidation is ValidationResult.Success &&
            confirmValidation is ValidationResult.Success &&
            agreementValidation is ValidationResult.Success) {

            viewModel.email.value = email
            viewModel.password.value = password
            viewModel.goToNextStep()
        }
    }

    private fun validateStep2() {
        val lastName = binding.surnameInputLayout.editText?.text.toString()
        val firstName = binding.nameInputLayout.editText?.text.toString()
        val middleName = binding.lastnameInputLayout.editText?.text.toString()
        val gender = when (binding.genderRadioGroup.checkedRadioButtonId) {
            R.id.maleRadioButton -> "male"
            R.id.femaleRadioButton -> "female"
            else -> null
        }
        val birthDate = binding.birthdayInputLayout.editText?.text.toString()

        val lastNameValidation = RegistrationValidator.validateName(lastName, "Фамилия")
        val firstNameValidation = RegistrationValidator.validateName(firstName, "Имя")
        val middleNameValidation = if (middleName.isNotBlank()) {
            RegistrationValidator.validateName(middleName, "Отчество")
        } else {
            ValidationResult.Success
        }
        val genderValidation = RegistrationValidator.validateGender(gender)
        val birthDateValidation = RegistrationValidator.validateBirthDate(birthDate)

        binding.surnameInputLayout.error = (lastNameValidation as? ValidationResult.Error)?.message
        binding.nameInputLayout.error = (firstNameValidation as? ValidationResult.Error)?.message
        binding.lastnameInputLayout.error = (middleNameValidation as? ValidationResult.Error)?.message
        binding.genderErrorTextView.text = (genderValidation as? ValidationResult.Error)?.message
        binding.genderErrorTextView.visibility = if (genderValidation is ValidationResult.Error) View.VISIBLE else View.GONE
        binding.birthdayInputLayout.error = (birthDateValidation as? ValidationResult.Error)?.message

        if (lastNameValidation is ValidationResult.Success &&
            firstNameValidation is ValidationResult.Success &&
            middleNameValidation is ValidationResult.Success &&
            genderValidation is ValidationResult.Success &&
            birthDateValidation is ValidationResult.Success) {

            viewModel.lastName.value = lastName
            viewModel.firstName.value = firstName
            viewModel.middleName.value = middleName.takeIf { it.isNotBlank() }
            viewModel.gender.value = gender
            viewModel.dateOfBirth.value = birthDate
            viewModel.goToNextStep()
        }
    }

    private fun validateStep3() {
        val licenseNumber = binding.driverLicenseInputLayout.editText?.text.toString().replace("\\s".toRegex(), "")
        val licenseIssueDate = binding.driverLicenseDateInputLayout.editText?.text.toString()

        val licenseValidation = RegistrationValidator.validateDriverLicense(licenseNumber)
        val issueDateValidation = RegistrationValidator.validateLicenseIssueDate(licenseIssueDate)
        val driverLicensePhotoValidation = RegistrationValidator.validateDriverLicensePhoto(driverLicensePhotoUri)
        val passportPhotoValidation = RegistrationValidator.validatePassportPhoto(passportPhotoUri)

        binding.driverLicenseInputLayout.error = (licenseValidation as? ValidationResult.Error)?.message
        binding.driverLicenseDateInputLayout.error = (issueDateValidation as? ValidationResult.Error)?.message

        if (driverLicensePhotoValidation is ValidationResult.Error) {
            binding.driverLicensePhotoText.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.driverLicensePhotoText.text = driverLicensePhotoValidation.message
        }

        if (passportPhotoValidation is ValidationResult.Error) {
            binding.passportPhotoText.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.passportPhotoText.text = passportPhotoValidation.message
        }

        if (licenseValidation is ValidationResult.Success &&
            issueDateValidation is ValidationResult.Success &&
            driverLicensePhotoValidation is ValidationResult.Success &&
            passportPhotoValidation is ValidationResult.Success) {

            viewModel.licenseNumber.value = licenseNumber
            viewModel.licenseIssueDate.value = licenseIssueDate
            savePhotosToInternalStorage()
            finishRegistration()
        }
    }

    // Сохранение фото в память устройства
    private fun savePhotosToInternalStorage() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val userId = viewModel.email.value?.hashCode() ?: Random().nextInt(100000)

            driverLicensePhotoUri?.let { uri ->
                saveImageToInternalStorage(uri, "driver_license_${userId}_$timestamp.jpg")
            }

            passportPhotoUri?.let { uri ->
                saveImageToInternalStorage(uri, "passport_${userId}_$timestamp.jpg")
            }

            profilePhotoUri?.let { uri ->
                saveImageToInternalStorage(uri, "profile_${userId}_$timestamp.jpg")
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка сохранения фотографий", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun saveImageToInternalStorage(uri: Uri, fileName: String): File {
        val inputStream = contentResolver.openInputStream(uri) ?: throw IOException("Не удалось открыть файл")
        val outputDir = File(filesDir, "images")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFile = File(outputDir, fileName)
        FileOutputStream(outputFile).use { output ->
            inputStream.copyTo(output)
        }

        return outputFile
    }

    private fun updateUIForStep(step: Int) {
        binding.step1.visibility = if (step == 1) View.VISIBLE else View.GONE
        binding.step2.visibility = if (step == 2) View.VISIBLE else View.GONE
        binding.step3.visibility = if (step == 3) View.VISIBLE else View.GONE
        binding.buttonNext.text = getString(R.string.next_button)
    }

    private fun finishRegistration() {
        viewModel.completeRegistration()
    }

    private fun showSuccess() {
        setContentView(bindingAlert.root)
        bindingAlert.successTitle.visibility = View.VISIBLE
        bindingAlert.successLayout.visibility = View.VISIBLE
        bindingAlert.buttonNext.visibility = View.VISIBLE
        bindingAlert.buttonNext.text = getString(R.string.next_button)

        bindingAlert.errorTitle.visibility = View.GONE
        bindingAlert.errorLayout.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (viewModel.currentStep.value == 1) {
            super.onBackPressed()
        } else {
            viewModel.goToPreviousStep()
        }
    }
}