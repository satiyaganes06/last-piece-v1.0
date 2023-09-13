package com.sg.lastpiece.ui.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.eBook_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_adding_product.*
import kotlinx.android.synthetic.main.activity_admin__add_ebook.*
import kotlinx.android.synthetic.main.activity_admin__add_ebook.btn_Submit
import java.io.IOException

class Admin_AddEbookActivity : BaseActivity(), View.OnClickListener {

    // A global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for URI of a selected document from phone storage.
    private var mSelectedDocumentFileUri: Uri? = null

    // A global variable for uploaded product image URL.
    private var mEbookImageURL: String = ""
    private var mEbookPDFURL: String = ""

    private lateinit var option_eBook_categories : Spinner
    private lateinit var mEbook_category : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin__add_ebook)

        setupActionBar()
        iv_add_update_ebook.setOnClickListener(this)
        imbtn_ebook_document.setOnClickListener(this)
        btn_Submit.setOnClickListener(this)
        dropdownList()
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_ebook_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_add_ebook_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun dropdownList(){

        option_eBook_categories = findViewById(R.id.til_ebook_category) as Spinner

        val options = arrayOf("eBook Category","Education", "Action and Adventure", "Thriller", "Fantasy", "Historical", "Romance", "Science Fiction (Sci-Fi)","Biographies", "Poetry")

        option_eBook_categories.adapter = ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,options)

        option_eBook_categories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mEbook_category = options.get(position)
            }
        }


    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_ebook -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@Admin_AddEbookActivity)
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

                R.id.imbtn_ebook_document ->{
                    if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showDocumentChooser(this@Admin_AddEbookActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE_FOR_PDF
                        )
                    }
                }

                R.id.btn_Submit -> {
                    if (validateEbookDetails()) {

                        //Show Progress
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.EBOOK_IMAGE, mSelectedDocumentFileUri, Constants.EBOOK_PDF_DOCUMENT)

                    }
                }

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

            }
        }else if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE_FOR_PDF){
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showDocumentChooser(this)

            }
        }else{
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    // Replace the add icon with edit icon once the image is selected.
                    iv_add_update_ebook.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_edit))

                    // The uri of selection image from phone storage.
                    mSelectedImageFileUri = data.data!!

                    try {
                        // Load the product image in the ImageView.
                        GlideLoader(this@Admin_AddEbookActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_ebook_image
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            }else if (requestCode == Constants.PICK_DOCUMENT_REQUEST_CODE) {
                if (data != null) {
                    // Replace the add icon with edit icon once the image is selected.
                    imbtn_ebook_document.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.icon_edit
                        )
                    )

                    // The uri of selection image from phone storage.
                    mSelectedDocumentFileUri = data.data!!
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }




    // A function to validate the product details.
    private fun validateEbookDetails(): Boolean {

        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_ebook_image), true)
                false
            }

            mSelectedDocumentFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_ebook_pdf_document), true)
                false
            }

            TextUtils.isEmpty(et_ebook_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_ebook_title), true)
                false
            }

            TextUtils.isEmpty(et_ebook_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_ebook_description),
                    true
                )
                false
            }

            TextUtils.equals(mEbook_category, "eBook Category") -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_ebook_category),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_ebook_author.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_ebook_author),
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
    fun imageUploadSuccess(pdfURL: String, imageURL: String) {
        // Initialize the global image url variable.
        mEbookImageURL = imageURL
        mEbookPDFURL = pdfURL

        uploadEbookDetails()
    }

    fun uploadEbookDetails(){

        // Here we get the text from editText and trim the space
        val ebook = eBook_Details(
            et_ebook_title.text.toString().trim { it <= ' ' },
            et_ebook_description.text.toString().trim { it <= ' ' },
            mEbook_category,
            et_ebook_author.text.toString().trim { it <= ' ' },
            mEbookImageURL,
            mEbookPDFURL,
        )

        FirestoreClass().uploadEbookDetails(this, ebook)
    }

    public fun eBookUploadSuccess(){
        // Hide the progress dialog & Loading Animation [Ends]
        hideProgressDialog()

        val toast:Toast = Toast.makeText(
            this@Admin_AddEbookActivity,
            resources.getString(R.string.ebook_uploaded_success_message),
            Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        finish()
    }

}