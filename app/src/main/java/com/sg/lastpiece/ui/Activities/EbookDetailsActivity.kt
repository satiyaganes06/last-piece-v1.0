package com.sg.lastpiece.ui.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Wishlist_Details
import com.sg.lastpiece.Models.eBook_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.ViewActivity.ImageViewActivity
import com.sg.lastpiece.ui.ViewActivity.ViewDocumentEbook
import com.sg.lastpiece.ui.ui.Fragments.EbookDashboardFragment
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_book_details.*
import kotlinx.android.synthetic.main.activity_ebook_details.*

class EbookDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mEbookId : String = " "
    private lateinit var mEbook_Details : eBook_Details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ebook_details)

        if (intent.hasExtra(Constants.EXTRA_EBOOK_ID)) {
            mEbookId = intent.getStringExtra(Constants.EXTRA_EBOOK_ID)!!
        }

        ft_before_wish.setOnClickListener ( this )
        ft_after_wish.setOnClickListener ( this )
        flbtn_delete.setOnClickListener ( this )
        btn_read.setOnClickListener(this)

        getEbookDetails()
        setupActionBar()

        if(FirestoreClass().getCurrentUserID() != Constants.SELECTED_USER_ID){
            flbtn_delete.isVisible = false
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_ebook_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_ebook_details_activity.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.btn_read -> {

                    val intent = Intent(this@EbookDetailsActivity, ViewDocumentEbook::class.java)
                    intent.putExtra(Constants.EXTRA_VIEW_PDF, mEbook_Details.ebook_pdf_document)
                    intent.putExtra(Constants.EBOOK_TITLE, mEbook_Details.ebook_name)
                    startActivity(intent)

                }
                R.id.ft_before_wish->{

                    addToWishlist()
                }
                R.id.ft_after_wish->{
                    val intent = Intent(this@EbookDetailsActivity, WishlistActivity::class.java)
                    startActivity(intent)
                }
                R.id.flbtn_delete -> {
                    FirestoreClass().deleteEbooks(this, mEbookId)
                }
            }
        }
    }

    private fun getEbookDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getEbookDetails(this, mEbookId)
    }

    fun ebookDetailsSuccess(ebook: eBook_Details) {

        iv_ebook_detail_image.setOnClickListener {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra(Constants.EXTRA_BOOK_IMAGE, ebook.ebook_image)
            intent.putExtra(Constants.EXTRA_BOOK_TITLE, ebook.ebook_name)
            startActivity(intent)
        }


        mEbook_Details = ebook
        // Populate the product details in the UI.
        GlideLoader(this@EbookDetailsActivity).loadBookPicture(
                ebook.ebook_image,
                iv_ebook_detail_image
        )

        tv_ebook_details_title.text =  ebook.ebook_name
        tv_ebook_details_description.text = ebook.ebook_desc
        tv_ebook_details_category_desc.text = ": "+ ebook.ebook_category
        tv_ebook_details_author_desc.text = ": "+ ebook.ebook_author

        FirestoreClass().checkIfeBookExistInWishlist(this@EbookDetailsActivity, mEbookId)
            // Hide Progress dialog.
        hideProgressDialog()


    }

    private fun addToWishlist(){
        val wishlist_Item = Wishlist_Details(
                FirestoreClass().getCurrentUserID(),
                mEbookId,
                mEbook_Details.ebook_name,
                mEbook_Details.ebook_image,
                mEbook_Details.ebook_category,
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addWishlistItems(this@EbookDetailsActivity, wishlist_Item)
    }


    fun addToWishlistSuccess(){
        hideProgressDialog()

        val toast: Toast = Toast.makeText(
                this@EbookDetailsActivity,
                resources.getString(R.string.success_message_item_added_to_wishlist),
                Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        ft_before_wish.isVisible = false
        ft_after_wish.isVisible = true
    }



    @SuppressLint("RestrictedApi")
    fun eBookExistsInCart(){
        hideProgressDialog()
        ft_before_wish.isVisible = false
        ft_after_wish.isVisible = true
    }


    fun ebookDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        val toast:Toast = Toast.makeText(
            this,
            resources.getString(R.string.ebook_deleted_success_message),
            Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        finish()


    }





}