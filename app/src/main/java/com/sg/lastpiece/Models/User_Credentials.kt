package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
//It is a data class not a normal class
data class User_Credentials (
    val id: String = "",
    val first_Name: String = "",
    val last_Name: String = "",
    val matric_ID: String = "",
    val email_Address: String = "",
    val gender: String = "",
    val phone_Number: Long = 0,
    val image: String = "",
    val profile_Completed: Int = 0,
    val number_bought_Books : Int = 0): Parcelable