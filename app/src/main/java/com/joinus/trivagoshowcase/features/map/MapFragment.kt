package com.joinus.trivagoshowcase.features.map

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.MapStyleOptions
import com.joinus.trivagoshowcase.MainViewModel
import com.joinus.trivagoshowcase.R
import com.joinus.trivagoshowcase.databinding.FragmentMapBinding
import com.joinus.trivagoshowcase.helpers.extensions.toDp
import dagger.hilt.android.AndroidEntryPoint
import services.mappers.Business
import kotlin.math.*

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
            viewModel.mapViewState
                .collect {
                    when {
                        it.isLoading -> removeViews()
                        it.onRefreshClicked -> getCurrentLatLng()
                        else -> {
                            if (it.businesses.isNotEmpty()) populateMap(it.businesses)
                            if (it.snapedViewId != null) highlightView(it.snapedViewId)
                        }
                    }
                }
        }
    }

    private fun removeViews() {
        mapOverlay.removeAllViews()
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
        val offSet = activity?.resources?.displayMetrics?.heightPixels?.times(0.30)?.toInt() ?: 0
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap.apply {
                setPadding(0, 0, 0, offSet)
                setOnCameraMoveListener {
                    mapOverlay
                        .children
                        .forEach { setViewPosition(it) }
                }
                setOnCameraChangeListener {
                    mapOverlay
                        .children
                        .forEach { setViewPosition(it) }
                }
                setOnCameraMoveStartedListener {
                    when (it) {
                        GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                            viewModel.refreshButton(true)
                        }
                        else -> {}
                    }
                }
                setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
            }
        }
    }

    private fun getCurrentLatLng() {
        val latLng = googleMap.cameraPosition.target
        viewModel.getBusinesses(latLng.latitude, latLng.longitude)
    }

    private fun highlightView(businessId: String) {
        businessId.let { id ->
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
        animateMap(businesses.firstOrNull { it.id == businessId }, businesses)
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
            Log.d("firstLog", "sera????")
            this.businesses = businesses
            businesses.forEach {
                setViewPosition(addViewToMapOverlay(it.id, it.latLng, it.latLng.latitude))
            }
            animateMap(businesses)
        }
    }

    private fun animateMap(businesses: List<Business>) {
        val mapHeight =
            activity?.resources?.displayMetrics?.heightPixels?.times(0.7)?.toInt()
                ?.toDp()?.toFloat()
                ?: 0f
        val mapWidth = mapView.measuredWidth.toDp().toFloat()
        if (businesses.isEmpty()) return
        val latLngs: List<LatLng> = businesses.map { it.latLng }
        val latLngBounds = LatLngBounds.builder().apply {
            latLngs.forEach {
                include(it)
            }
        }.build()
        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                getCameraPosition(
                    latLngBounds,
                    mapWidth,
                    mapHeight
                )
            )
        )
    }

    private fun animateMap(business: Business?, businesses: List<Business>) {
        val mapHeight =
            activity?.resources?.displayMetrics?.heightPixels?.times(0.5)?.toInt()
                ?.toDp()?.toFloat()
                ?: 0f
        val mapWidth = mapView.measuredWidth.toDp().toFloat()
        val latLngBounds = LatLngBounds.builder().apply {
            businesses
                .map { it.latLng }
                .forEach {
                    include(it)
                }
        }.build()
        business?.let {
            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    getCameraPosition(latLngBounds, mapWidth, mapHeight)
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
                viewModel.onMapOverlayViewClicked(businesses.indexOfFirst { it.id == businessId })
                viewModel.onMapOverlayViewClicked(null)
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

    private fun getCameraPosition(
        latLngBounds: LatLngBounds,
        mapWidth: Float,
        mapHeight: Float
    ): CameraPosition =
        CameraPosition
            .Builder()
            .target(latLngBounds.center)
            .zoom(getBoundsZoomLevel(latLngBounds, mapWidth, mapHeight))
            .build()

    private fun getBoundsZoomLevel(
        bounds: LatLngBounds,
        mapWidthPx: Float,
        mapHeightPx: Float
    ): Float {
        val ne = bounds.northeast
        val sw = bounds.southwest
        val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / PI
        val lngDiff = ne.longitude - sw.longitude
        val lngFraction = (if (lngDiff < 0) (lngDiff + 360) else lngDiff) / 360
        val latZoom = zoom(mapHeightPx * 0.8f, WORLD_DP_HEIGHT, latFraction)
        val lngZoom = zoom(mapWidthPx * 0.8f, WORLD_DP_WIDTH, lngFraction)
        val result = min(latZoom, lngZoom)
        return min(result, MAX_ZOOM).toFloat()
    }

    private fun latRad(lat: Double): Double {
        val sin = sin(lat * PI / 180)
        val radX2 = ln((1 + sin) / (1 - sin)) / 2
        return max(min(radX2, PI), -PI) / 2
    }

    private fun zoom(mapPx: Float, worldPx: Float, fraction: Double): Double {
        return log2(mapPx / worldPx / fraction)
    }

    companion object {
        private const val MAX_ZOOM: Double = 16.0
        private const val WORLD_DP_HEIGHT = 256f
        private const val WORLD_DP_WIDTH = 256f
    }
}