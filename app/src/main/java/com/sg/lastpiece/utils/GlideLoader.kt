package com.sg.lastpiece.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sg.lastpiece.R
import android.content.Context
import androidx.fragment.app.Fragment
import java.io.IOException

class GlideLoader(val context: Context) {

    //A function to load image from URI for the user profile picture.
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.img_default_user) // A default place holder if image is failed to load.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadBookPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                    .with(context)
                    .load(image) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.book_placeholder) // A default place holder if image is failed to load.
                    .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

class GlideLoaderFragment(val context: Fragment) {

    //A function to load image from URI for the user profile picture.
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                    .with(context)
                    .load(image) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.img_default_user) // A default place holder if image is failed to load.
                    .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}