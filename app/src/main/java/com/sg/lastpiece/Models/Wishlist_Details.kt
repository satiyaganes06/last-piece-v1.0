package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wishlist_Details (
        val user_id: String = "",
        val ebook_id: String = "",
        val ebook_title: String = "",
        val ebook_image: String = "",
        val ebook_category: String = "",
        var wishlish_id: String = "",
): Parcelable