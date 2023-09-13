package com.sg.lastpiece.ui.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sg.lastpiece.R
import com.sg.lastpiece.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create an instance of Android SharedPreferences
        val sharedPreferences =
            this.getSharedPreferences(Constants.LASTPIECE_PREFERENCES, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

        boldTextView.text = "$username"
    }

    fun method(){
        val intent = Intent(this@MainActivity, MainActivity::class.java)
        startActivity(intent)
    }
}