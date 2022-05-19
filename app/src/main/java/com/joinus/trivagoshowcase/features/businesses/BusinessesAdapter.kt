package com.joinus.trivagoshowcase.features.businesses

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joinus.trivagoshowcase.R
import services.mappers.Business

class BusinessesAdapter(private val onClick: (Business) -> Unit) :
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
        holder.bindView(position)
        holder.itemView.setOnClickListener {
            onClick(businesses[position])
        }
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


        fun bindView(position: Int) {
//            itemView.id = businesses[position].id.hashCode()
            val business = businesses[position]
            val image = itemView.findViewById<ImageView>(R.id.image)
            val title = itemView.findViewById<TextView>(R.id.title)
            val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar)
            val ratingWritten = itemView.findViewById<TextView>(R.id.rating_written)
            val ratingNumber = itemView.findViewById<TextView>(R.id.rating_number)
            val price = itemView.findViewById<TextView>(R.id.price)
            val origin = itemView.findViewById<TextView>(R.id.origin)
            val ratingContainer = itemView.findViewById<ConstraintLayout>(R.id.rating_container)

            Glide
                .with(context)
                .load(business.imageUrl)
                .into(image)

            title.text = business.name
            ratingNumber.text = business.rating.times(2).toString()
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
            price.text = "$" + business.latLng.latitude.toString().takeLast(3)
            origin.text = business.categories.last()
        }
    }
}