package com.example.drivenextmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenextmobile.R
import com.example.drivenextmobile.databinding.MainScreenBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainScreenBinding.inflate(layoutInflater)
        setContentView(R.layout.main_screen)
        // перезапуск-Todo
        startActivity(intent)
        finish()
    }
}