package com.sg.lastpiece.ui.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.User_Credentials
import com.sg.lastpiece.R
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.et_email
import kotlinx.android.synthetic.main.activity_register.et_first_name
import kotlinx.android.synthetic.main.activity_register.et_last_name
import kotlinx.android.synthetic.main.activity_register.et_matric_id
import kotlinx.android.synthetic.main.activity_shop_cart.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    // Create a instance of User data model class.
    private lateinit var mUserDetails: User_Credentials

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if(intent.hasExtra(Constants.MORE_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.MORE_USER_DETAILS)!!
        }


        if(mUserDetails.profile_Completed == 0){
            //Set the user details in user profile page
            et_first_name.setText(mUserDetails.first_Name)
            et_first_name.isEnabled = false
            et_last_name.setText(mUserDetails.last_Name)
            et_last_name.isEnabled = false
            et_matric_id.setText(mUserDetails.matric_ID)
            et_matric_id.isEnabled = false
            et_email.setText(mUserDetails.email_Address)
            et_email.isEnabled = false

        }else{
            setupActionBar()
            //Set the user details in user profile page
            et_first_name.setText(mUserDetails.first_Name)
            et_first_name.isEnabled = true
            et_last_name.setText(mUserDetails.last_Name)
            et_last_name.isEnabled = true

            //et_mobile_number.setText(mUserDetails)
            //So the user cant change email and matric id
            et_matric_id.setText(mUserDetails.matric_ID)
            et_matric_id.isEnabled = false
            et_email.setText(mUserDetails.email_Address)
            et_email.isEnabled = false
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)

            if(mUserDetails.phone_Number !=0L){
                et_mobile_number.setText((mUserDetails.phone_Number).toString())
            }

            if(mUserDetails.gender == Constants.MALE){
                rb_male.isChecked = true
            }else{
                rb_female.isChecked = true
            }
        }

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    // Here will check if the permission is already allowed or we need to request for it.
                    // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we will request for the same.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        //Choose a image to set as a profile picture
                        Constants.showImageChooser(this)
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
                //Update the data in the profile page and the data will change in Firebase too
                R.id.btn_submit ->{

                    if(validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(mSelectedImageFileUri != null){
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE, mSelectedImageFileUri,"")
                        }else{
                            updateUserProfileDetails()
                        }

                    }
                }

            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        // Here we get the text from editText and trim the space
        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        val lastName = et_last_name.text.toString().trim{ it <= ' '}
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.phone_Number.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (firstName.isNotEmpty()) {
            userHashMap[Constants.FIRSTNAME] = firstName.toString()
        }

        if (lastName.isNotEmpty()) {
            userHashMap[Constants.LASTNAME] = lastName.toString()
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.PORFILE_COMPLETE] = 1
        userHashMap[Constants.NUMBER_BOUGHT_BOOK] = 0

        // call the registerUser function of FireStore class to make an entry in the database.
        FirestoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }

    /* This function will identify the result of runtime permission after the
     user allows or deny permission based on the unique code. */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Choose a image to set as a profile picture
                Constants.showImageChooser(this)

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage. URI data is like path (C:/this pc/satiya)
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }


    private fun validateUserProfileDetails(): Boolean {
        return when {

            // We have kept the user profile picture is optional.
            // The Matric Id and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number, firstname, lastname is not empty as it is mandatory to enter.
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    //A function to notify the success result and proceed further accordingly after updating the user details.
    fun userProfileUpdateSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        val toast: Toast = Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.msg_profile_update_success),
                Toast.LENGTH_SHORT
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()


        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

   // A function to notify the success result of image upload to the Cloud Storage.
    fun imageUploadSuccess(imageURL: String) {

       mUserProfileImageURL = imageURL
       updateUserProfileDetails()
    }
}
