package com.joinus.trivagoshowcase.features.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.joinus.trivagoshowcase.MainViewModel
import com.joinus.trivagoshowcase.R
import com.joinus.trivagoshowcase.databinding.FragmentDetailsBinding
import com.joinus.trivagoshowcase.helpers.extensions.getNavigationBarHeight
import com.joinus.trivagoshowcase.helpers.extensions.getStatusBarHeight
import services.mappers.Business

class DetailsFragment : Fragment() {

    val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var business: Business

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.custom_move)
        business = Gson().fromJson(arguments?.getString(BUSINESS), Business::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.transitionName = arguments?.getString(ROOT_TRANSITION_NAME)
        binding.image.transitionName = arguments?.getString(IMAGE_TRANSITION_NAME)
        binding.title.transitionName = arguments?.getString(TITLE_TRANSITION_NAME)

        Glide
            .with(this)
            .load(business.imageUrl)
            .into(binding.image)
        binding.title.text = business.name

        binding.previous.setOnClickListener {
            viewModel.onBackPressed()
        }

        lifecycleScope.launchWhenResumed {
            viewModel.detailsViewState
                .collect {
                    handleLoading(it.isLoading)
                    when {
                        it.isError -> {}
                        it.businessDetails != null -> {}
                    }
                }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {

        private const val BUSINESS = "business"
        private const val ROOT_TRANSITION_NAME = "rootTransitionName"
        private const val TITLE_TRANSITION_NAME = "titleTransitionName"
        private const val IMAGE_TRANSITION_NAME = "imageTransitionName"


        fun newInstance(business: Business, sharedViews: List<View>): DetailsFragment {
            val stringedBusiness = Gson().toJson(business)
            val root = sharedViews[0]
            val image = sharedViews[1]
            val title = sharedViews[2]
            val rootTransitionName = ViewCompat.getTransitionName(root).toString()
            val imageTransitionName = ViewCompat.getTransitionName(image).toString()
            val titleTransitionName = ViewCompat.getTransitionName(title).toString()

            return DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(BUSINESS, stringedBusiness)
                    putString(ROOT_TRANSITION_NAME, rootTransitionName)
                    putString(IMAGE_TRANSITION_NAME, imageTransitionName)
                    putString(TITLE_TRANSITION_NAME, titleTransitionName)
                }
            }
        }
    }


}