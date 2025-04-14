package com.example.drivenextmobile.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.drivenextmobile.databinding.RegistrationScreenBinding
import com.example.drivenextmobile.ui.Screen
import com.example.drivenextmobile.ui.SharedViewModel

class RegistrationFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: RegistrationScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegistrationScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            sharedViewModel.navigateTo(Screen.SUCCESS)
        }
    }

    // Очистка binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}