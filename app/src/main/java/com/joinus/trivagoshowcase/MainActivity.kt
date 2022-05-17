package com.joinus.trivagoshowcase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: MainViewModel by viewModels()
        viewModel.getBusinessDetails("H4jJ7XB3CetIr1pg56CczQ")
    }
}