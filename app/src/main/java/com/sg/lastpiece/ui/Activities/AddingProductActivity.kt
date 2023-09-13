package com.sg.lastpiece.ui.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.Models.User_Credentials
import com.sg.lastpiece.R
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_adding_product.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.toolbar_register_activity
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.*

class AddingProductActivity : BaseActivity(), View.OnClickListener {
    

    // A global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for uploaded product image URL.
    private var mBookImageURL: String = ""

    private lateinit var option_Book_categories : Spinner
    private lateinit var mBook_category : String
    private lateinit var option_Book_condition : Spinner
    private lateinit var mBook_condition : String
    private lateinit var option_Book_faculty : Spinner
    private lateinit var mBook_faculty : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_product)

        setupActionBar()
        iv_add_update_product.setOnClickListener(this)
        btn_Submit.setOnClickListener(this)
        dropdownList()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_adding_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_adding_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddingProductActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_Submit -> {
                    if (validateBookDetails()) {

                        //Show Progress
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.BOOK_IMAGE,mSelectedImageFileUri,"")

                    }
                }

            }
        }
    }

    private fun dropdownList(){

        option_Book_categories = findViewById(R.id.til_book_category) as Spinner
        option_Book_condition = findViewById(R.id.til_book_condition) as Spinner
        option_Book_faculty = findViewById(R.id.til_book_faculty) as Spinner

        val options = arrayOf("--Choose Book Category--","Education", "Action and Adventure", "Thriller", "Fantasy", "Historical", "Romance", "Science Fiction (Sci-Fi)","Biographies", "Poetry")
        val options2 = arrayOf("--Choose Book Condition--","Used Book", "New Book")
        val options3 = arrayOf("--Choose Book Faculty--","FKOM", "FTKKP", "FTKEE", "FTKMA", "FTKA", "FTKPM", "PSM", "FIST", "FIM" )

        option_Book_categories.adapter = ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,options)
        option_Book_condition.adapter = ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,options2)
        option_Book_faculty.adapter = ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,options3)

        option_Book_categories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBook_category = options.get(position)
            }
        }

        option_Book_condition.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBook_condition = options2.get(position)
            }
        }

        option_Book_faculty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBook_faculty = options3.get(position)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Choose a image to set as a profile picture
                Constants.showImageChooser(this)

            } else {
                //Displaying another toast if permission is not granted
                val toast: Toast = Toast.makeText(
                        this,
                        resources.getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG
                )

                val view = toast.view
                view?.background?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                toast.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    // Replace the add icon with edit icon once the image is selected.
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_edit))

                    // The uri of selection image from phone storage.
                    mSelectedImageFileUri = data.data!!

                    try {
                        // Load the product image in the ImageView.
                        GlideLoader(this@AddingProductActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_book_image
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }


            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

   // A function to validate the product details.
    private fun validateBookDetails(): Boolean {

       return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_book_image), true)
                false
            }

            TextUtils.isEmpty(et_book_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_book_title), true)
                false
            }

            TextUtils.isEmpty(et_book_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_book_price), true)
                false
            }

            TextUtils.isEmpty(et_book_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_book_description),
                        true
                )
                false
            }

            TextUtils.isEmpty(et_book_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_book_quantity),
                        true
                )
                false
            }

            TextUtils.equals(mBook_category, "--Choose Book Category--") -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_book_category),
                        true
                )
                false
            }

            TextUtils.equals(mBook_condition, "--Choose Book Condition--") -> {
               showErrorSnackBar(
                   resources.getString(R.string.err_msg_enter_book_condition),
                   true
               )
               false
           }

           TextUtils.equals(mBook_faculty, "--Choose Book Faculty--") -> {
               showErrorSnackBar(
                       resources.getString(R.string.err_msg_enter_book_condition),
                       true
               )
               false
           }

            TextUtils.isEmpty(et_book_author.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_book_author),
                        true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    // A function to get the successful result of product image upload.
    fun imageUploadSuccess(imageURL: String) {
        // Initialize the global image url variable.
        mBookImageURL = imageURL

        uploadBookDetails()
    }

    fun uploadBookDetails(){

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
                this.getSharedPreferences(Constants.LASTPIECE_PREFERENCES, Context.MODE_PRIVATE)
                        .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Get the logged in phone number from the SharedPreferences that we have stored at a time of login.
        val phoneNum =
                this.getSharedPreferences(Constants.LASTPIECE_PREFERENCES, Context.MODE_PRIVATE)
                        .getString(Constants.LOGGED_IN_PHONE_NUMBER, "")!!

        // Here we get the text from editText and trim the space
        val book = Book_Details(
                et_book_title.text.toString().trim { it <= ' ' },
                (et_book_price.text.toString().trim { it <= ' ' }).toFloat(),
                et_book_quantity.text.toString().trim { it <= ' ' },
                et_book_description.text.toString().trim { it <= ' ' },
                mBook_category,
                mBook_condition,
                mBook_faculty,
                et_book_author.text.toString().trim { it <= ' ' },
                mBookImageURL,
                FirestoreClass().getCurrentUserID(),
                username,
                (phoneNum).toLong()
        )

        FirestoreClass().uploadBookDetails(this, book)
    }

    public fun bookUploadSuccess(){
        // Hide the progress dialog & Loading Animation [Ends]
        hideProgressDialog()


        val toast:Toast = Toast.makeText(
                this@AddingProductActivity,
                resources.getString(R.string.book_uploaded_success_message),
                Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        finish()
    }

}