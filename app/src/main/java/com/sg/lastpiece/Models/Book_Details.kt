package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Book_Details (
        val book_name : String = "",
        val book_price : Float = 0F,
        val book_quantity : String = "",
        val book_desc : String = "",
        val book_category : String = "",
        val book_condition : String = "",
        val book_faculty : String = "",
        val book_author : String = "",
        val book_image : String = "",
        val user_id: String = "",
        val user_name: String = "",
        val user_contact : Long = 0,
        var book_id : String = "",
): Parcelable

