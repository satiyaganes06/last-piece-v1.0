package com.sg.lastpiece.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS : String = "users"
    const val BOOKS : String = "books"
    const val EBOOKS : String = "ebooks"
    const val CART_ITEMS : String = "cart_items"
    const val WISHLIST_ITEMS : String = "wishlist_items"
    const val ADDRESSES :String = "addresses"
    const val ORDERS: String = "orders"
    const val SOLD_PRODUCTS: String = "sold_products"
    const val BOOK_QUANTITY: String = "book_quantity"
    const val SELECTED_USER_ID : String = "jWvGpb8NPANiGjyXAKvuRTKm6sg2"

    //Create a constant variables for Android SharedPreferences and Username Key.
    const val LASTPIECE_PREFERENCES: String = "lastPiecePrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOGGED_IN_PHONE_NUMBER: String = "logged_in_phone_number"
    const val MORE_USER_DETAILS : String = "more_user_details"

    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult in the Base Activity.
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
    const val READ_STORAGE_PERMISSION_CODE_FOR_PDF = 3

    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2

    // A unique code of document selection from Phone Storage.
    const val PICK_DOCUMENT_REQUEST_CODE = 3

    // Constant variables for Gender (RadioButton)
    const val MALE: String = "Male"
    const val FEMALE: String = "Female"


    const val ROOM: String = "Room"
    const val OTHER: String = "Other"

    // Firebase User database field names
    const val FIRSTNAME:String = "first_Name"
    const val LASTNAME:String = "last_Name"
    const val MOBILE: String = "phone_Number"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val PORFILE_COMPLETE: String = "profile_Completed"
    const val NUMBER_BOUGHT_BOOK: String = "number_bought_Books"
    const val USER_ID : String = "user_id"
    const val SELLER_ID: String = "seller_id"
    const val BOOK_ID : String = "book_id"
    const val EBOOK_ID : String = "ebook_id"
    const val ORDER_ID :String = "order_id"

    //Search and Sort using Book name and Falcuty name
    const val BOOK_FACULTY= "book_faculty"
    const val BOOK_NAME = "book_name"

    //Sort using Book name and Falcuty name
    const val EBOOK_CATEGORY= "ebook_category"

    // Firebase Book database field names
    const val BOOK_IMAGE: String = "book_image"
    const val EBOOK_IMAGE : String = "ebook_image"
    const val EBOOK_PDF_DOCUMENT : String = "ebook_pdf_document"
    const val EBOOK_TITLE : String = "ebook_title"


    // Intent extra constants
    const val EXTRA_BOOK_ID: String = "extra_book_id"
    const val EXTRA_EBOOK_ID: String = "extra_ebook_id"
    const val EXTRA_BOOK_OWNER_ID: String = "extra_book_owner_id"
    const val EXTRA_VIEW_PDF: String = "view_pdf"
    const val EXTRA_BOOK_IMAGE: String = "extra_book_image"
    const val EXTRA_BOOK_TITLE: String = "extra_book_title"
    const val EXTRA_EBOOK_IMAGE: String = "extra_ebook_image"
    const val EXTRA_EBOOK_TITLE: String = "extra_ebook_title"
    const val DEFAULT_CART_QUANTITY : String = "1"
    const val CART_QUANTITY: String = "cart_quantity"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_my_order_details"
    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"
    //const val EXTRA_EBOOK_DETAILS: String = "extra_sold_product_details"

    //Stripe Payment request Code
    const val PAYMENT_REQUEST_CODE = 2132
    const val FIRST_PAYMENT_REQUEST_CODE = 2213332

    // Stripe
    const val EXTRA_AMOUNT : String = "amount"
    const val PUBLISHABLE_KEY : String = "pk_test_51In0KiD3mAWixS5XzF5qt2klxTtWB4ktSjKXBJdQx76xQnRLI82fSuPLNr0aqTf0F3hNEf7fEKKv3qbkrJwXDQFI008N8Q1xNs"

    //A function for user profile image selection from phone storage.
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    fun showDocumentChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val documentIntent = Intent()
        documentIntent.setType("application/pdf")
        documentIntent.setAction(Intent.ACTION_GET_CONTENT)
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(documentIntent, PICK_DOCUMENT_REQUEST_CODE)
    }



    /* This function will get the image's format code like jpg or png
       c://home/download/satiya.jpg, So this function will get the last part which is jpg
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {

        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


}