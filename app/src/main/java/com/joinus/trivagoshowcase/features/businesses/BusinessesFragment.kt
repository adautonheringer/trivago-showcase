package com.joinus.trivagoshowcase.features.businesses

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.joinus.trivagoshowcase.BusinessesViewState
import com.joinus.trivagoshowcase.MainViewModel
import com.joinus.trivagoshowcase.databinding.FragmentBusinessesBinding
import com.joinus.trivagoshowcase.helpers.extensions.attachSnapHelperWithListener
import com.joinus.trivagoshowcase.helpers.extensions.getNavigationBarHeight
import com.joinus.trivagoshowcase.helpers.extensions.toPx
import com.joinus.trivagoshowcase.helpers.snap.OnSnapPositionChangeListener
import dagger.hilt.android.AndroidEntryPoint
import services.mappers.Business

@AndroidEntryPoint
class BusinessesFragment : Fragment() {

    private lateinit var businessesAdapter: BusinessesAdapter
    private lateinit var businessesRecyclerView: RecyclerView
    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var snapHelper: SnapHelper
    private lateinit var binding: FragmentBusinessesBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusinessesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        businessesRecyclerView = binding.recyclerView
        businessesAdapter =
            BusinessesAdapter { business, sharedViews -> onBusinessClick(business, sharedViews) }
        myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        snapHelper = LinearSnapHelper()
        setRecyclerViewLayoutParams(businessesRecyclerView)
        setShimmerViewLayoutParams(binding.shimmer)
        businessesRecyclerView.let {
            it.layoutManager = myLayoutManager
            it.adapter = businessesAdapter
            snapHelper.attachToRecyclerView(it)
            it.attachSnapHelperWithListener(
                snapHelper = snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        viewModel.onSnapView(
                            businessesAdapter.getBusinessByPosition(
                                position
                            ).id
                        )
                        val snapedView = myLayoutManager.findViewByPosition(position)
                        val leftView = myLayoutManager.findViewByPosition(position - 1)
                        val rightView = myLayoutManager.findViewByPosition(position + 1)
                        snapedView?.let { view -> animateHighlight(view) }
                        leftView?.let { view -> animateReverseHighlight(view) }
                        rightView?.let { view -> animateReverseHighlight(view) }
                    }
                },
            )
        }

        lifecycleScope.launchWhenResumed {
            viewModel.businessesViewState.collect {
                handleLoading(it)
                handleError(it)
                handleBusinesses(it)
                handleScrollToPosition(it.mapViewPosition)
            }
        }
    }

    private fun handleScrollToPosition(position: Int?) {
        position?.let { businessesRecyclerView.smoothScrollToPosition(it) }
    }

    private fun handleBusinesses(state: BusinessesViewState) {
        businessesAdapter.updateList(state.businesses)
    }

    private fun handleError(state: BusinessesViewState) {

    }

    private fun handleLoading(state: BusinessesViewState) {
        businessesRecyclerView.visibility = if (state.isLoading) View.INVISIBLE else View.VISIBLE
        binding.shimmer.visibility = if (state.isLoading) View.VISIBLE else View.GONE
    }

    private fun onBusinessClick(business: Business, sharedViews: List<View>) {
        viewModel.getBusinessDetails(business, sharedViews)
    }

    private fun setRecyclerViewLayoutParams(view: View) {
        val navBarHeight = requireActivity().getNavigationBarHeight()
        view.setPadding(16.toPx(), 8.toPx(), 16.toPx(), navBarHeight)
    }

    private fun setShimmerViewLayoutParams(view: View) {
        val navBarHeight = requireActivity().getNavigationBarHeight()
        view.setPadding(0, 0, 0, navBarHeight)
    }

    private fun animateHighlight(view: View) {
        val animatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.10f)
        val animatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.10f)
        val animatorTranslationY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -10f)
        val animatorElevation = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, 12f)

        val animatorSet = AnimatorSet()
        animatorSet.apply {
            interpolator = OvershootInterpolator()
            duration = 300
            playTogether(
                animatorScaleX,
                animatorScaleY,
                animatorTranslationY,
                animatorElevation,
            )
        }.start()
    }

    private fun animateReverseHighlight(view: View) {
        val animatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f)
        val animatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f)
        val animatorTranslationY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f)
        val animatorElevation = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, 0f)
        val animatorSet = AnimatorSet()

        animatorSet.apply {
            interpolator = LinearOutSlowInInterpolator()
            duration = 300
            playTogether(
                animatorScaleX,
                animatorScaleY,
                animatorTranslationY,
                animatorElevation,
            )
        }.start()
    }


}