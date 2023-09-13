package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for Sold Book with required fields.
 */
@Parcelize
data class SoldBooks_Details(
    var order_Status : String = "",
    val seller_id: String = "",
    val book_name: String = "",
    val book_price: String = "",
    val sold_book_quantity: String = "",
    val book_image: String = "",
    val order_id: String = "",
    val order_date: Long = 0L,
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val address: Address = Address(),
    var payment_Method : String = "",
    var order_pk: String = "",
    var soldbook_id: String = "",
) : Parcelable