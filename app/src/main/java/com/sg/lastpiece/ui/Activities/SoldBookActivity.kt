package com.sg.lastpiece.ui.Activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.SoldBooks_Details
import com.sg.lastpiece.Models.User_Credentials
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.adapters.SoldProductsListAdapter
import kotlinx.android.synthetic.main.activity_sold_book.*

class SoldBookActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_book)

        setupActionBar()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sold_book_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_sold_book_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldBookActivity)
    }

    /**
     * A function to get the list of sold products.
     */
    fun successSoldProductsList(soldBooksList: ArrayList<SoldBooks_Details>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (soldBooksList.size > 0) {
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_book_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(this@SoldBookActivity)
            rv_sold_product_items.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(this@SoldBookActivity, soldBooksList)
            rv_sold_product_items.adapter = soldProductsListAdapter
        } else {
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_book_found.visibility = View.VISIBLE
        }
    }




}