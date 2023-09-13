package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart_Details (
    val user_id: String = "",
    val book_id: String = "",
    val book_title: String = "",
    val book_price: String = "",
    val book_image: String = "",
    var cart_quantity: String = "",
    var book_quantity: String = "",
    var seller_id : String = "",
    var cart_id: String = "",
): Parcelable