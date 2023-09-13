package com.sg.lastpiece.ui.Activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Wishlist_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.adapters.WishItemListAdapter
import kotlinx.android.synthetic.main.activity_wishlist.*

class WishlistActivity : BaseActivity() {

    // A global variable for the cart list items.
    private lateinit var mWishListItems: ArrayList<Wishlist_Details>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        setupActionBar()
    }

    override fun onResume() {
        super.onResume()
        getWishItemsList()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_wishlist_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_wishlist_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getWishItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getWishList(this@WishlistActivity)
    }

    //A function to notify the success result of the wishlist items list from cloud firestore.
    fun successWishItemsList(wishList: ArrayList<Wishlist_Details>) {

        // Hide progress dialog.
        hideProgressDialog()


        mWishListItems = wishList

        if (mWishListItems.size > 0) {

            rv_wish_items_list.visibility = View.VISIBLE
            tv_no_ebook_found.visibility = View.GONE

            rv_wish_items_list.layoutManager = LinearLayoutManager(this@WishlistActivity)
            rv_wish_items_list.setHasFixedSize(true)

            val wishListAdapter = WishItemListAdapter(this@WishlistActivity, mWishListItems)
            rv_wish_items_list.adapter = wishListAdapter


        } else {
            rv_wish_items_list.visibility = View.GONE
            tv_no_ebook_found.visibility = View.VISIBLE
        }
    }

    fun itemRemovedForWishSuccess() {

        hideProgressDialog()

        val toast: Toast = Toast.makeText(
                this@WishlistActivity,
                resources.getString(R.string.msg_wishlist_item_removed_successfully),
                Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        getWishItemsList()
    }


}