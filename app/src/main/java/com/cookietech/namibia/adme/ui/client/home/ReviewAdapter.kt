package com.cookietech.namibia.adme.ui.client.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.helper.TimeHelper
import com.cookietech.namibia.adme.models.ReviewPOJO
import com.cookietech.namibia.adme.utils.UiHelper
import de.hdodenhof.circleimageview.CircleImageView

class ReviewAdapter(val context : Context) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    var reviewList = ArrayList<ReviewPOJO>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_client_photo : CircleImageView = itemView.findViewById(R.id.tv_client_photo)
        var tv_client_name : TextView = itemView.findViewById(R.id.tv_client_name)
        var rating_bar : RatingBar = itemView.findViewById(R.id.rating_bar)
        var tv_service_time : TextView = itemView.findViewById(R.id.tv_review_time)
        var tv_review_text : TextView = itemView.findViewById(R.id.tv_review_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]

        Glide.with(context)
            .asBitmap()
            .load(review.client_profile_pic)
            .placeholder(R.drawable.default_user_photo)
            .into(holder.tv_client_photo)

        holder.tv_client_name.text = review.client_name
        holder.rating_bar.rating = review.rating
        holder.tv_service_time.text = UiHelper.getDate(review.review_time?.toLong() ?:0,"dd MMM yyyy")
        holder.tv_review_text.text = review.review

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}