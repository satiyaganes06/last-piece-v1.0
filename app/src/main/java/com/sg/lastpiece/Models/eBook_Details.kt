package com.sg.lastpiece.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class eBook_Details (
    val ebook_name : String = "",
    val ebook_desc : String = "",
    val ebook_category : String = "",
    val ebook_author : String = "",
    val ebook_image : String = "",
    val ebook_pdf_document : String = "",
    var ebook_id : String = "",
): Parcelable