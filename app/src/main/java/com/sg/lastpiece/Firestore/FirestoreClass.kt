package com.sg.lastpiece.Firestore

import android.accounts.Account
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sg.lastpiece.Models.*
import com.sg.lastpiece.ui.Activities.*
import com.sg.lastpiece.ui.ui.Fragments.*
import com.sg.lastpiece.utils.Constants


class FirestoreClass {
    //Store

    //Create a Firestore object so we can use it multiple times in this project
    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    // A function to make an entry of the registered user in the FireStore database.


    fun registerUser(activity: RegisterActivity, userInfo: User_Credentials) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    //Retrieve
    // A function to get the user id of current logged user.
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
            return currentUserID
        }else{
            val noUser = "no user"
            return noUser
        }


    }

    // A function to get the logged user details from FireStore Database.
    //For activity
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User_Credentials::class.java)!!

                // Store some of the user credential in the user phone.

                //Create an instance of the Android SharedPreferences.
                val sharedPreferences =
                        activity.getSharedPreferences(
                                Constants.LASTPIECE_PREFERENCES,
                                Context.MODE_PRIVATE
                        )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                        //Key : logged_in_username
                        // Value : Satiya Ganes CC19066
                        Constants.LOGGED_IN_USERNAME,
                        "${user.first_Name} ${user.last_Name}"

                )
                editor.putString(
                        Constants.LOGGED_IN_PHONE_NUMBER,
                        "${user.phone_Number}"
                )
                editor.apply()


                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    //For Fragment
    fun getUserDetailsFragment(activity: Fragment) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val user = document.toObject(User_Credentials::class.java)!!

                    when (activity) {
                        is AccountFragment -> {
                            // Call a function of Account Fragment for transferring the result to it.
                            activity.userDetailsSuccess(user)
                        }
                    }

                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is AccountFragment -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting user details.",
                            e
                    )
                }
    }

    // Update data
    //A function to update the user profile data into the database.
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {
                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    // A function to upload the image to the cloud storage.
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, constant : String, pdfFileURI: Uri?, constant1: String) {

        //getting the storage reference
        val sRef: StorageReference
        if(constant == "book_image"){
            sRef= FirebaseStorage.getInstance().reference.child(
                    "Book Image/${constant + System.currentTimeMillis() + "." +
                            Constants.getFileExtension(
                                    activity,
                                    imageFileURI)}"
            )
        }else if(constant == "ebook_image"){
            sRef = FirebaseStorage.getInstance().reference.child(
                    "eBook Image/${constant + System.currentTimeMillis() + "." +
                            Constants.getFileExtension(
                                    activity,
                                    imageFileURI)}"
            )
        }else{
            sRef = FirebaseStorage.getInstance().reference.child(
                    "User Image/${constant + System.currentTimeMillis() + "." +
                            Constants.getFileExtension(
                                    activity,
                                    imageFileURI)}"
            )
        }

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                            "Firebase Image URL",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                Log.e("Downloadable Image URL", uri.toString())

                                // Here call a function of base activity for transferring the result to it.
                                when (activity) {
                                    is UserProfileActivity -> {
                                        activity.imageUploadSuccess(uri.toString())
                                    }

                                    is AddingProductActivity -> {
                                        activity.imageUploadSuccess(uri.toString())
                                    }

                                    is Admin_AddEbookActivity -> {
                                        //activity.imageUploadSuccess(uri.toString())
                                        uploadPDFDocumentToCloudStorage(activity, pdfFileURI, constant1, uri.toString())
                                    }
                                }

                            }
                }
                .addOnFailureListener { exception ->

                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }

                        is AddingProductActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            exception.message,
                            exception
                    )
                }
    }



    //Book Details
    fun uploadBookDetails(activity: AddingProductActivity, bookInfo: Book_Details) {

        // The "Books" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.BOOKS)
            // Document ID for book fields. Here the document it is the User ID.
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(bookInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.bookUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                        activity.javaClass.simpleName,
                        "Error while uploading the book details.",
                        e
                )
            }
    }

    // My Book list
    fun getBooksList(fragment: Fragment) {
        // The collection name for BOOKS
        mFireStore.collection(Constants.BOOKS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    Log.e("Books List", document.documents.toString())


                    val booksList: ArrayList<Book_Details> = ArrayList()


                    for (i in document.documents) {

                        val book = i.toObject(Book_Details::class.java)
                        book!!.book_id = i.id

                        booksList.add(book)
                    }

                    when (fragment) {
                        is ProductFragment -> {
                            fragment.successBooksListFromFireStore(booksList)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is ProductFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }

    // Overall Book list
    fun getDashboardBooksList(fragment: DashboardFragment){
        mFireStore.collection(Constants.BOOKS)
        .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->


                    Log.e("Books List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val booksList: ArrayList<Book_Details> = ArrayList()


                    for (i in document.documents) {

                        val book = i.toObject(Book_Details::class.java)
                        book!!.book_id = i.id

                        booksList.add(book)
                    }

                    fragment.successBooksListFromFireStore(booksList)

                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is DashboardFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }

    //Sorting Book based on Faculty
    fun getDashboardBooksListBySort(fragment: DashboardFragment,  dataName : String, finding_Data : String){
        mFireStore.collection(Constants.BOOKS)
                .whereEqualTo(dataName, finding_Data)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->


                    Log.e("Books List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val booksList: ArrayList<Book_Details> = ArrayList()


                    for (i in document.documents) {

                        val book = i.toObject(Book_Details::class.java)
                        book!!.book_id = i.id

                        booksList.add(book)
                    }

                    fragment.successBooksListFromFireStore(booksList)

                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is DashboardFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }

    //Search Book based on Book Name
    fun getDashboardBooksListBySearch(fragment: DashboardFragment, search_Data : String){
        mFireStore.collection(Constants.BOOKS)
                .orderBy(Constants.BOOK_NAME)
                .startAt(search_Data)
                .endAt("$search_Data\uf8ff")
                .get()
                .addOnSuccessListener { document ->


                    Log.e("Books List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val booksList: ArrayList<Book_Details> = ArrayList()


                    for (i in document.documents) {

                        val book = i.toObject(Book_Details::class.java)
                        book!!.book_id = i.id

                        booksList.add(book)
                    }

                    fragment.successBooksListFromFireStore(booksList)

                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is DashboardFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }


    //Related Data Based on book faculty
    fun getBooksListInBookDetails(activity: BookDetailsActivity, related_Data : String, mBookID : String){
        mFireStore.collection(Constants.BOOKS)
                .whereEqualTo(Constants.BOOK_FACULTY, related_Data)
                .limit(4)
                .get()
                .addOnSuccessListener { document ->

                    Log.e("Books List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val booksList: ArrayList<Book_Details> = ArrayList()


                    for (i in document.documents) {

                        val book = i.toObject(Book_Details::class.java)
                        book!!.book_id = i.id

                        booksList.add(book)
                    }

                    activity.successBooksListInBookDetailsFromFireStore(booksList)

                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (activity) {
                        is BookDetailsActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }


    fun deleteBooks(fragment: ProductFragment, bookID: String) {

        mFireStore.collection(Constants.BOOKS)
            .document(bookID)
            .delete() // firebase comment for delete
            .addOnSuccessListener {

                fragment.bookDeleteSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    // A function to get the book details based on the book id.
    fun getBookDetails(activity: BookDetailsActivity, book_id: String) {

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.BOOKS)
                .document(book_id)
                .get() // Will get the document snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the product details in the form of document.
                    Log.e(activity.javaClass.simpleName, document.toString())

                    // Convert the snapshot to the object of Product data model class.
                    val book = document.toObject(Book_Details::class.java)!!
                    val user = document.toObject(User_Credentials::class.java)!!

                    if(book != null){
                        activity.bookDetailsSuccess(book)
                    }

                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
                }
    }

    fun addCartItems(activity: BookDetailsActivity, addToCart: Cart_Details) {

        mFireStore.collection(Constants.CART_ITEMS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating the document for cart item.",
                            e
                    )
                }
    }

    // A function to check whether the item already exist in the cart or not.
    fun checkIfItemExistInCart(activity: BookDetailsActivity, book_id: String) {

        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.BOOK_ID, book_id)
                .get()
                .addOnSuccessListener { document ->

                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // If the document size is greater than 1 it means the product is already added to the cart.
                    if (document.documents.size > 0) {
                        activity.bookExistsInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while checking the existing cart list.",
                            e
                    )
                }
    }

    //A function to get the cart items list from the cloud firestore.
    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of cart items in the form of documents.
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Cart Items ArrayList.
                    val list: ArrayList<Cart_Details> = ArrayList()

                    // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                    for (i in document.documents) {

                        val cartItem = i.toObject(Cart_Details::class.java)!!
                        cartItem.cart_id = i.id

                        list.add(cartItem)
                    }

                    when (activity) {
                        is ShopCartActivity -> {
                            activity.successCartItemsList(list)
                        }
                        is CheckoutActivity -> {
                            activity.successCartItemsList(list)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error based on the activity instance.
                    when (activity) {
                        is ShopCartActivity -> {
                            activity.hideProgressDialog()
                        }

                        is CheckoutActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
                }
    }

    // A function to get all the product list from the cloud firestore.
    fun getAllBooksList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.BOOKS)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e("Book List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Book_Details> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val book = i.toObject(Book_Details::class.java)
                        book!!.book_id = i.id

                        productsList.add(book)
                    }

                    when (activity) {
                        is ShopCartActivity -> {
                            activity.successProductsListFromFireStore(productsList)
                        }
                        is CheckoutActivity -> {
                            activity.successProductsListFromFireStore(productsList)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (activity) {
                        is ShopCartActivity -> {
                            activity.hideProgressDialog()
                        }
                        is CheckoutActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting all product list.", e)
                }
    }

    // A function to remove the cart item from the cloud firestore.

    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id) // cart id
                .delete()
                .addOnSuccessListener {

                    // Notify the success result of the removed cart item from the list to the base class.
                    when (context) {
                        is ShopCartActivity -> {
                            context.itemRemovedSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is ShopCartActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(
                            context.javaClass.simpleName,
                            "Error while removing the item from the cart list.",
                            e
                    )
                }
    }


    //A function to update the cart item in the cloud firestore.
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id) // cart id
                .update(itemHashMap) // A HashMap of fields which are to be updated.
                .addOnSuccessListener {

                    // Notify the success result of the updated cart items list to the base class.
                    when (context) {
                        is ShopCartActivity -> {
                            context.itemUpdateSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is ShopCartActivity -> {
                            context.hideProgressDialog()
                        }
                    }

                    Log.e(
                            context.javaClass.simpleName,
                            "Error while updating the cart item.",
                            e
                    )
                }
    }


    /*
    eBook Details
     */
    fun uploadEbookDetails(activity: Admin_AddEbookActivity, ebookInfo: eBook_Details) {
        mFireStore.collection(Constants.EBOOKS)
            // Document ID for book fields. Here the document it is the User ID.
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(ebookInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.eBookUploadSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the book details.",
                    e
                )
            }
    }

    // Overall eBook list
    fun getEbookDashboardBooksList(fragment: EbookDashboardFragment){
        mFireStore.collection(Constants.EBOOKS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                Log.e("eBooks List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val ebooksList: ArrayList<eBook_Details> = ArrayList()


                for (i in document.documents) {

                    val ebook = i.toObject(eBook_Details::class.java)
                    ebook!!.ebook_id = i.id

                    ebooksList.add(ebook)
                }

                fragment.successEbooksListFromFireStore(ebooksList)

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is EbookDashboardFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e("Get ebook List", "Error while getting ebook list.", e)
            }
    }

    fun uploadPDFDocumentToCloudStorage(activity: Activity, pdfFileURI: Uri?, constant: String, imageURL: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "eBook PDF/${constant + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                pdfFileURI
            )}"
        )

        //adding the file to reference
        sRef.putFile(pdfFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {


                            is Admin_AddEbookActivity -> {
                                activity.imageUploadSuccess(uri.toString(), imageURL)

                            }
                        }

                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddingProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    // A function to get the ebook details based on the book id.
    fun getEbookDetails(activity: EbookDetailsActivity, ebook_id: String) {

        // The collection name for ebook
        mFireStore.collection(Constants.EBOOKS)
                .document(ebook_id)
                .get() // Will get the document snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the product details in the form of document.
                    Log.e(activity.javaClass.simpleName, document.toString())

                    // Convert the snapshot to the object of Product data model class.
                    val ebook = document.toObject(eBook_Details::class.java)!!

                    if(ebook != null){
                        activity.ebookDetailsSuccess(ebook)
                    }

                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while getting the ebook details.", e)
                }
    }

    fun addWishlistItems(activity: EbookDetailsActivity, addToWishlist: Wishlist_Details) {

        mFireStore.collection(Constants.WISHLIST_ITEMS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(addToWishlist, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.addToWishlistSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating the document for cart item.",
                            e
                    )
                }
    }

    //Sorting Book based on Faculty
    fun geteBooksDashboardListBySort(fragment: EbookDashboardFragment,  dataName : String, finding_Data : String){
        mFireStore.collection(Constants.EBOOKS)
                .whereEqualTo(dataName, finding_Data)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    Log.e("eBooks List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val ebooksList: ArrayList<eBook_Details> = ArrayList()


                    for (i in document.documents) {

                        val ebook = i.toObject(eBook_Details::class.java)
                        ebook!!.ebook_id = i.id

                        ebooksList.add(ebook)
                    }

                    fragment.successEbooksListFromFireStore(ebooksList)

                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is EbookDashboardFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }

                    Log.e("Get ebook List", "Error while getting ebook list.", e)
                }
    }

    fun deleteEbooks(activity: EbookDetailsActivity, ebookID: String) {
        mFireStore.collection(Constants.EBOOKS)
            .document(ebookID)
            .delete() // firebase comment for delete
            .addOnSuccessListener {

                activity.ebookDeleteSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    // A function to check whether the ebook already exist in the wishlist or not.
    fun checkIfeBookExistInWishlist(activity:EbookDetailsActivity, ebook_id: String) {

        mFireStore.collection(Constants.WISHLIST_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.EBOOK_ID, ebook_id)
                .get()
                .addOnSuccessListener { document ->

                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // If the document size is greater than 1 it means the product is already added to the cart.
                    if (document.documents.size > 0) {
                        activity.eBookExistsInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while checking the existing wishlist.",
                            e
                    )
                }
    }


    fun getWishList(activity: WishlistActivity) {
        mFireStore.collection(Constants.WISHLIST_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    // Here we get the list of sold products in the form of documents.
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Sold Products ArrayList.
                    val list: ArrayList<Wishlist_Details> = ArrayList()

                    // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                    for (i in document.documents) {

                        val ebook = i.toObject(Wishlist_Details::class.java)!!
                        ebook.wishlish_id= i.id

                        list.add(ebook)
                    }

                    activity.successWishItemsList(list)
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error.
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting the list of sold products.",
                            e
                    )
                }
    }

    // A function to remove the wishlist item from the cloud firestore.

    fun removeItemFromWishlist(context: Context, wishlist_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.WISHLIST_ITEMS)
                .document(wishlist_id)
                .delete()
                .addOnSuccessListener {

                    // Notify the success result of the removed cart item from the list to the base class.
                    when (context) {
                        is WishlistActivity -> {
                            context.itemRemovedForWishSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is ShopCartActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(
                            context.javaClass.simpleName,
                            "Error while removing the item from the cart list.",
                            e
                    )
                }
    }


    //A function to add address to the cloud firestore.
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    //A function to get the list of address from the cloud firestore.
    fun getAddressesList(activity: AddressActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }
    }

    //A function to delete the existing address from the cloud firestore.
    fun deleteAddress(activity: AddressActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }

    //A function to update the existing address to the cloud firestore.
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }


    //A function to place an order of the user in the cloud firestore.
    fun placeOrder(activity: Activity, order: Order, mRandomFBID : String) {
        mFireStore.collection(Constants.ORDERS)
            .document(mRandomFBID)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener { documentReference ->
                // Here call a function of base activity for transferring the result to it.

                mFireStore.collection(Constants.ORDERS)
                Log.d("payment", "DocumentSnapshot added with ID: ${documentReference}")


                when (activity) {
                    is CheckoutActivity -> {
                        activity.orderPlacedSuccess()
                    }
                }


            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (activity) {
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }

    //A function to get the list of orders from cloud firestore.
    fun getMyOrdersList(fragment: ordersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.order_id = i.id

                    list.add(orderItem)
                }

                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }


    //A function to get the list of sold products from the cloud firestore.
    fun getSoldProductsList(activity: SoldBookActivity) {
        // The collection name for SOLD PRODUCTS
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.SELLER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold products in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Sold Products ArrayList.
                val list: ArrayList<SoldBooks_Details> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                for (i in document.documents) {

                    val soldBook = i.toObject(SoldBooks_Details::class.java)!!
                    soldBook.soldbook_id= i.id

                    list.add(soldBook)
                }

                activity.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }

    //A function to update all the required details in the cloud firestore after placing the order successfully.
    /*fun updateAllDetails(activity: Activity, cartList: ArrayList<Cart_Details>, order: Order) {

        val writeBatch = mFireStore.batch()

        // Prepare the sold product details
        for (cart in cartList) {

            val soldBook = SoldBooks_Details(
                order.order_Status,
                cart.seller_id,
                cart.book_title,
                cart.book_price,
                cart.cart_quantity,
                cart.book_image,
                order.book_name,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address,
                order.payment_Method,
                order.order_id,
            )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldBook)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.book_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.BOOKS)
                .document(cart.book_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.cart_id)
            writeBatch.delete(documentReference)

        }

        writeBatch.commit().addOnSuccessListener {

            when (activity) {
                is CheckoutActivity -> {
                    activity.allDetailsUpdatedSuccessfully()
                }
            }

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            when (activity) {
                is CheckoutActivity -> {
                    activity.hideProgressDialog()
                }
            }

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }*/

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<Cart_Details>, order: Order, mRandomFBID : String) {

        val writeBatch = mFireStore.batch()

        // Prepare the sold product details
        for (cart in cartList) {

            val soldProduct = SoldBooks_Details(
                    order.order_Status,
                    cart.seller_id,
                    cart.book_title,
                    cart.book_price,
                    cart.cart_quantity,
                    cart.book_image,
                    order.book_name,
                    order.order_datetime,
                    order.sub_total_amount,
                    order.shipping_charge,
                    order.total_amount,
                    order.address,
                    order.payment_Method,
                    mRandomFBID,
            )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                    .document()
            writeBatch.set(documentReference, soldProduct)
        }

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.BOOK_QUANTITY] =
                    (cart.book_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.BOOKS)
                    .document(cart.book_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                    .document(cart.cart_id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating all the details after order placed.",
                    e
            )
        }
    }

    //A function to update the existing order status to the cloud firestore.
    fun addOrderID(activity: OrderDetailsActivity, orderHashMap: HashMap<String, Any>, order_id: String) {
        orderHashMap["order_id"] = order_id
        mFireStore.collection(Constants.ORDERS).document(order_id)
                .update(orderHashMap)
                .addOnSuccessListener {

                    activity.addOrderIDSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the Address.",
                            e
                    )
                }
    }

    //A function to update the existing order status to the cloud firestore.
    fun updateOrderStatus(activity: SoldBookDetailsActivity, orderHashMap: HashMap<String, Any>, soldBookID: String, mOrder_Status: String, order_pk : String) {
        orderHashMap["order_Status"] = mOrder_Status
        mFireStore.collection(Constants.SOLD_PRODUCTS).document(soldBookID)
                .update(orderHashMap)
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                   // activity.updateInOrderDocument()
                    updateOrderStatusForOrderDocument(activity, orderHashMap, mOrder_Status, order_pk)
                    //ctivity.updateOrderStatusInOrder()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the order_Status in Sold Product.",
                            e
                    )
                }
    }


    fun updateOrderStatusForOrderDocument(activity: SoldBookDetailsActivity, orderHashMap: HashMap<String, Any>, mOrder_Status: String, order_pk : String) {
        orderHashMap["order_Status"] = mOrder_Status
        mFireStore.collection(Constants.ORDERS).document(order_pk)
                .update(orderHashMap)
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.addUpdateOrderStatusSuccess()
                    //ctivity.updateOrderStatusInOrder()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the order status in books.",
                            e
                    )
                }
    }




}