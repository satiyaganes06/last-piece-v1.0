package com.sg.lastpiece.ui.ui.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.User_Credentials
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.*
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoaderFragment
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import java.util.*

class AccountFragment : BaseFragment(), View.OnClickListener  {

    // A variable for user details which will be initialized later on.
    private lateinit var mUserDetails: User_Credentials
    lateinit var mView: View
    private var sG_company_email ="sgdevelopercompany@gmail.com"
    private var app_name = "lastPiece"


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //  homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        mView = inflater.inflate(R.layout.fragment_account, container, false)

        mView.btn_account_info.setOnClickListener(this@AccountFragment)
        mView.btn_address.setOnClickListener(this@AccountFragment)
        mView.btn_sold_book.setOnClickListener(this@AccountFragment)
        mView.btn_contact_us.setOnClickListener(this@AccountFragment)
        mView.btn_logout.setOnClickListener(this@AccountFragment)


      return mView
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_account_info -> {
                    val intent = Intent(activity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.MORE_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_sold_book -> {
                    val intent = Intent(activity, SoldBookActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_address -> {

                    val intent = Intent(activity, AddressActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_contact_us -> {

                    val subject : String = "Hello, "
                    val message: String = "How can we make $app_name work for you ? "

                    sendEmail(sG_company_email, message, subject)

                }

                R.id.btn_logout -> {

                    showAlertDialogToLogOut()
                }
            }
        }
    }


    override fun onResume() {
      super.onResume()

      getUserDetails()
    }

    private fun getUserDetails() {

      // Show the progress dialog
      showProgressDialog(resources.getString(R.string.please_wait))

      // Call the function of Firestore class to get the user details from firestore which is already created.
      FirestoreClass().getUserDetailsFragment(this@AccountFragment)
    }

    //A function to receive the user details and populate it in the UI.
    @SuppressLint("SetTextI18n")
    fun userDetailsSuccess(user: User_Credentials) {

          mUserDetails = user

          // Hide the progress dialog
          hideProgressDialog()

          // Load the image using the Glide Loader class.
          GlideLoaderFragment(this@AccountFragment).loadUserPicture(mUserDetails.image, iv_user_photo)

          tv_name.setText("Hi, "+ (mUserDetails.first_Name).capitalize(Locale.ROOT) + " " + (mUserDetails.last_Name).capitalize(Locale.ROOT))

    }

    //A function to show the alert dialog for the confirmation of log out.
    private fun showAlertDialogToLogOut() {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.log_out_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.log_out_dialog_message))
        builder.setIcon(R.drawable.icon_log_out)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            //finish()

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
           // Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }



}
