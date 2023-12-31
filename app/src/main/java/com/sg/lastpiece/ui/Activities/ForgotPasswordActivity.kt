package com.sg.lastpiece.ui.Activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sg.lastpiece.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

@Suppress("DEPRECATION")
class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_submit.setOnClickListener {
            emailVerification()
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun emailVerification(){
        //pass the the email address from the text view to this emailAddress variable
        val emailAddress = et_email.text.toString().trim { it <= ' ' }

        // Now, If the email entered in blank then show the error message or else continue with the implemented feature.
        if (emailAddress.isEmpty()) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
        } else {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // This piece of code is used to send the reset password link to the user's email id if the user is registered.
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            // Show the toast message and finish the forgot password activity to go back to the login screen.
                            val toast:Toast = Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    resources.getString(R.string.email_sent_success),
                                    Toast.LENGTH_LONG
                            )
                            val view = toast.view
                            view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                            toast.show()

                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }

    }
