package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

 // A data model class for Order item with required fields.
@Parcelize
data class Order(
    val user_id: String = "",
    val items: ArrayList<Cart_Details> = ArrayList(),
    val address: Address = Address(),
    val book_name: String = "",
    val book_image: String = "",
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val order_datetime: Long = 0L,
    var order_Status : String = "",
    var payment_Method : String = "",
    var order_id: String = ""
) : Parcelable
