package com.joinus.trivagoshowcase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.joinus.trivagoshowcase.databinding.ActivityMainBinding
import com.joinus.trivagoshowcase.helpers.extensions.setStatusBarColor
import com.joinus.trivagoshowcase.features.businesses.BusinessesFragment
import com.joinus.trivagoshowcase.features.details.DetailsFragment
import com.joinus.trivagoshowcase.features.map.MapFragment
import com.joinus.trivagoshowcase.features.search.SearchFragment
import com.joinus.trivagoshowcase.helpers.extensions.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import services.mappers.Business

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setPadding(binding.detailsContainer)
        setPadding(binding.searchContainer)
        val view = binding.root
        setContentView(view)
        binding.viewModel = viewModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainViewState.collect {
                    when {
                        it.isGoingToBusinessDetails -> {
                            setStatusBarColor(binding.root, R.color.light_gray)
                            launchDetailsFragment(it.business!!, it.sharedViews)
                        }
                        it.isGoingBack -> {
                            supportFragmentManager.popBackStack()
                            setStatusBarColor(binding.root, R.color.transparent)
                        }
                    }
                }
            }
        }

        setStatusBarColor(binding.root, R.color.transparent)
        launchMapFragment()
        launchBusinessesFragment()
        launchSearchFragment()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private fun setPadding(view: View) {
        val statusBarHeight = this.getStatusBarHeight()
        view.setPadding(0, statusBarHeight, 0, 0)
    }

    private fun launchMapFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map_container, MapFragment(), "mapFragment")
            .commit()
    }

    private fun launchBusinessesFragment() {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .add(R.id.businesses_container, BusinessesFragment(), "businessesFragment")
            .commit()
    }

    private fun launchSearchFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.search_container, SearchFragment(), "searchFragment")
            .commit()
    }

    private fun launchDetailsFragment(business: Business, sharedViews: List<View>) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.fade_out,
                R.anim.slide_in_down,
                R.anim.slide_out_down
            )
            .setReorderingAllowed(true)
            .apply {
                sharedViews.forEach {
                    addSharedElement(it, ViewCompat.getTransitionName(it).toString())
                }
            }
            .add(
                R.id.details_container,
                DetailsFragment.newInstance(business, sharedViews),
                "detailsFragment"
            )
            .addToBackStack("detailsFragment")
            .commit()
    }

}
