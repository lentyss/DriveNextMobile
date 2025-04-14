package com.example.drivenextmobile.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenextmobile.R
import com.example.drivenextmobile.app.manager.InternetCheckManager
import com.example.drivenextmobile.databinding.AlertFragmentBinding

class NoInternetActivity : AppCompatActivity() {
    private lateinit var binding: AlertFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Настройка элементов интерфейса
        setContentView(binding.root)

        binding.errorLayout.visibility = View.VISIBLE
        binding.errorTitle.visibility = View.VISIBLE
        binding.buttonNext.visibility = View.VISIBLE
        binding.buttonNext.text = getString(R.string.error_button)

        binding.successTitle.visibility = View.GONE
        binding.successLayout.visibility = View.GONE

        // Обработчик кнопки "Повторить"
        binding.buttonNext.setOnClickListener {
            if (InternetCheckManager.hasInternetConnection(this)) {
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonNext.setOnClickListener {
            // Проверяем соединение и закрываем Activity, если интернет есть
            checkInternetAndFinish()
        }
    }

    private fun checkInternetAndFinish() {
        if (InternetCheckManager.hasInternetConnection(this)) {
            finish()
        } else {
            // Можно показать Toast или анимировать кнопку
        }
    }
}