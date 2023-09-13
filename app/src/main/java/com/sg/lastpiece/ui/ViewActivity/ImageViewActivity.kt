package com.sg.lastpiece.ui.ViewActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sg.lastpiece.R
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_ebook_details.*
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.activity_image_view.tv_title

class ImageViewActivity : AppCompatActivity() {

    private var mBookImage : String = "";
    private var mBookTitle : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        if (intent.hasExtra(Constants.EXTRA_BOOK_IMAGE)) {
            mBookImage = intent.getStringExtra(Constants.EXTRA_BOOK_IMAGE)!!
        }

        if (intent.hasExtra(Constants.EXTRA_BOOK_TITLE)) {
            mBookTitle = intent.getStringExtra(Constants.EXTRA_BOOK_TITLE)!!
        }

        GlideLoader(this@ImageViewActivity).loadBookPicture(
            mBookImage,
            image_view_book
        )

        setupActionBar()

        tv_title.setText(mBookTitle)


    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_book_image_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_book_image_activity.setNavigationOnClickListener { onBackPressed() }
    }



}