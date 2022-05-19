package com.joinus.trivagoshowcase.features.map

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.joinus.trivagoshowcase.MainViewModel
import com.joinus.trivagoshowcase.R
import com.joinus.trivagoshowcase.databinding.FragmentMapBinding
import com.joinus.trivagoshowcase.helpers.extensions.getNavigationBarHeight
import dagger.hilt.android.AndroidEntryPoint

import services.mappers.Business

@AndroidEntryPoint
class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var mapOverlay: FrameLayout
    private lateinit var googleMap: GoogleMap
    private var businesses: List<Business> = emptyList()
    val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.map
        mapOverlay = binding.mapOverlay
        initGoogleMap(savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.viewState.collect {
                populateMap(it.businesses)
                highlightView(it.highlightedBusinessId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey")
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap.apply {
                setPadding(0, 0, 0, requireActivity().getNavigationBarHeight())
                setOnCameraMoveListener {
                    mapOverlay
                        .children
                        .forEach { setViewPosition(it) }
                }
            }
        }
    }


    private fun highlightView(businessId: String?) {
        businessId?.let { id ->
            mapOverlay
                .children
                .forEach { view ->
                    if (view.id == id.hashCode()) {
                        view.findViewById<View>(R.id.active_item).visibility = View.VISIBLE
                        setScale(listOf(view))
                    } else {
                        view.findViewById<View>(R.id.active_item).visibility = View.INVISIBLE
                        setScale(listOf(view), 1f)
                    }
                }
        }
        animateMap(businesses.filter { it.id == businessId }.firstOrNull())
    }

    private fun setScale(views: List<View>, scale: Float = 1.1f) {
        views.forEach {
            val animatorScaleX = ObjectAnimator.ofFloat(it, View.SCALE_X, scale)
            val animatorScaleY = ObjectAnimator.ofFloat(it, View.SCALE_Y, scale)
            val animatorElevation = ObjectAnimator.ofFloat(it, View.TRANSLATION_Z, scale)
            val animatorSet = AnimatorSet()
            animatorSet.apply {
                interpolator = OvershootInterpolator()
                playTogether(animatorScaleX, animatorScaleY, animatorElevation)
                duration = 50
            }.start()
        }
    }

    private fun populateMap(businesses: List<Business>) {
        if (this.businesses != businesses) {
            this.businesses = businesses
            businesses.forEach {
                setViewPosition(addViewToMapOverlay(it.id, it.latLng, it.latLng.latitude))
            }
            animateMap(businesses)
        }
    }

    private fun animateMap(businesses: List<Business>) {
        if (businesses.isEmpty()) return
        val latLngs: List<LatLng> = businesses.map { it.latLng }
        val latLngBounds = LatLngBounds.builder().apply {
            latLngs.forEach {
                include(it)
            }
        }.build()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))
    }

    private fun animateMap(business: Business?) {
        business?.let {
            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        it.latLng,
                        googleMap.cameraPosition.zoom
                    )
                )
            )
        }
    }

    private fun addViewToMapOverlay(businessId: String, latLng: LatLng, price: Double): View? {
        var view = mapOverlay.findViewById<View>(businessId.hashCode())
        if (view != null) return null
        view = View.inflate(context, R.layout.custom_marker_view, null)
        view.apply {
            id = businessId.hashCode()
            setTag(R.id.latLng, latLng)
            findViewById<TextView>(R.id.price_value).text = price.toString().takeLast(3)
            setOnClickListener {
                viewModel.onMapOverlayViewClicked(businessId)
            }
        }
        mapOverlay
            .addView(
                view,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            )

        return view
    }

    private fun setViewPosition(view: View?) {
        view?.let {
            val latLng = it.getTag(R.id.latLng) as LatLng
            val point = googleMap.projection.toScreenLocation(latLng)
            it.x = point.x.minus(it.width.div(2)).toFloat()
            it.y = point.y.minus(it.height.div(2)).toFloat()
        }
    }

}