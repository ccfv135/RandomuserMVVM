package io.github.christianfajardo.randomuser.presentation

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.christianfajardo.randomuser.R

fun ImageView.loadImage(url: String?) {

    val options = RequestOptions()
        .error(R.drawable.ic_image_not_found)
        .placeholder(R.drawable.ic_image_downloading)


    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}