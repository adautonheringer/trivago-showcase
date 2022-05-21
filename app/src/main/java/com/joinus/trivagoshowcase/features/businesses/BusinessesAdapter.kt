package com.joinus.trivagoshowcase.features.businesses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joinus.trivagoshowcase.R
import services.mappers.Business

class BusinessesAdapter(private val onClick: (Business, List<View>) -> Unit) :
    RecyclerView.Adapter<BusinessesAdapter.BusinessViewHolder>() {

    private var businesses = listOf<Business>()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.business_cardview, parent, false)
        setLayoutParams(view, parent.context)

        return BusinessViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        businesses[position].let { holder.bindView(it) }
    }

    fun getBusinessByPosition(position: Int): Business {
        return businesses[position]
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int = businesses.size

    override fun getItemId(position: Int): Long {
        return businesses[position].id.hashCode().toLong()
    }

    private fun setLayoutParams(view: View, context: Context) {
        val cardView = view.findViewById<View>(R.id.root)
        val params = cardView.layoutParams
        params.height = MATCH_PARENT
        params.width = context.resources?.displayMetrics?.widthPixels?.times(0.71)?.toInt() ?: 0
        cardView.layoutParams = params
    }

    fun updateList(businesses: List<Business>) {
        if (this.businesses != businesses) {
            this.businesses = businesses
            notifyDataSetChanged()
        }
    }

    inner class BusinessViewHolder(business: View) :
        RecyclerView.ViewHolder(business) {

        fun bindView(business: Business) {
            val root = itemView.rootView
            val imageView = itemView.findViewById<ImageView>(R.id.image)
            val title = itemView.findViewById<TextView>(R.id.title)
            val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar)
            val ratingWritten = itemView.findViewById<TextView>(R.id.rating_written)
            val ratingNumber = itemView.findViewById<TextView>(R.id.rating_number)
            val price = itemView.findViewById<TextView>(R.id.price)
            val origin = itemView.findViewById<TextView>(R.id.origin)
            val ratingContainer = itemView.findViewById<ConstraintLayout>(R.id.rating_container)
            val sharedViews = listOf<View>(root, imageView, title)
            sharedViews
                .forEach {
                    ViewCompat.setTransitionName(it, business.id + it.id)
                }

            Glide
                .with(context)
                .load(business.imageUrl)
                .into(imageView)
            title.text = business.name
            price.text = "$" + business.latLng.latitude.toString().takeLast(3)
            ratingNumber.text = business.rating.times(2).toString()
            origin.text = business.categories.last()
            when (business.price.length) {
                1 -> {
                    ratingBar.numStars = 5
                    ratingContainer.background.setTint(getColor(context, R.color.dark_green))
                    ratingWritten.apply {
                        setTextColor(getColor(context, R.color.dark_green))
                        text = "Excellent"
                    }
                }
                2 -> {
                    ratingBar.numStars = 4
                    ratingContainer.background.setTint(getColor(context, R.color.dark_green))
                    ratingWritten.apply {
                        setTextColor(getColor(context, R.color.dark_green))
                        text = "Very good"
                    }
                }
                3 -> {
                    ratingBar.numStars = 3
                    ratingContainer.background.setTint(getColor(context, R.color.green))
                    ratingWritten.apply {
                        setTextColor(getColor(context, R.color.green))
                        text = "Good"
                    }
                }
                else -> {
                    ratingBar.numStars = 2
                    ratingContainer.background.setTint(getColor(context, R.color.orange))
                    ratingWritten.apply {
                        setTextColor(getColor(context, R.color.orange))
                        text = "Fair"
                    }
                }
            }
            itemView.setOnClickListener { onClick(business, sharedViews) }
        }
    }
}