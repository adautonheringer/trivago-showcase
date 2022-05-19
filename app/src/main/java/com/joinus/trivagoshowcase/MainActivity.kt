package com.joinus.trivagoshowcase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.joinus.trivagoshowcase.databinding.ActivityMainBinding
import com.joinus.trivagoshowcase.extensions.setStatusBarTransparent
import com.joinus.trivagoshowcase.features.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val view = binding.root
        setContentView(view)
        binding.viewModel = viewModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    when {
                        it.isLoading -> Log.d("firstLog", "isLoading")
                        it.isError -> Log.d("firstLog", "isError")
                    }
                }
            }
        }

        setStatusBarTransparent(binding.root, R.color.transparent)
        launchMapFragment()
        launchBusinessesFragment()
    }

    private fun launchMapFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map_container, MapFragment(), "mapFragment")
            .commit()
    }

    private fun launchBusinessesFragment() {

    }
}
