package com.example.drivenextmobile.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenextmobile.R
import com.example.drivenextmobile.databinding.OnboardingScreenBinding
import com.example.drivenextmobile.ui.splash.SplashActivity
import kotlinx.coroutines.launch


class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: OnboardingScreenBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeState()
        observeEffects()
    }

    private fun setupClickListeners() {
        binding.nextButton.setOnClickListener {
            viewModel.onEvent(OnboardingContract.Event.NextButtonClicked)
        }
        binding.topText.setOnClickListener {
            viewModel.onEvent(OnboardingContract.Event.SkipButtonClicked)
        }
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
                    OnboardingContract.Effect.NavigateToSplashWithButtons -> navigateToSplashWithButtons()
                }
            }
        }
    }

    private fun updateUI(state: OnboardingContract.State) {
        binding.imageView.setImageResource(state.imageRes)
        binding.indicators.setImageResource(state.progressRes)
        binding.textView.text = getString(state.titleRes)
        binding.textView1.text = getString(state.descriptionRes)

        binding.nextButton.text = if (state.isLastPage) {
            getString(R.string.go_button)
        } else {
            getString(R.string.next_button)
        }
    }

    private fun navigateToSplashWithButtons() {
        startActivity(
            Intent(this, SplashActivity::class.java).apply {
                putExtra("SHOW_BUTTONS", true)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
        finish()
    }
}