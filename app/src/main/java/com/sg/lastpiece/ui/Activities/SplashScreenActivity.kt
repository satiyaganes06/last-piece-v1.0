package com.sg.lastpiece.ui.Activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.R

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //This Coding will Remove or transparent the status bar. So the user only can see the splash screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // This coding is set the timer of the splash screen
        Handler().postDelayed(
                {
                    val checkUser = FirestoreClass().getCurrentUserID()
                    if(checkUser == "no user"){
                        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                        finish()
                    }else{
                        startActivity(Intent(this@SplashScreenActivity, DashboardActivity::class.java))
                        finish()
                    }

                },
                2500
        )


    }

}