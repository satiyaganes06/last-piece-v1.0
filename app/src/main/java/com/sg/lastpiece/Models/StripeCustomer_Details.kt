package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StripeCustomer_Details(
        var cust_Status : String = ""
) : Parcelable