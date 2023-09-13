package com.sg.lastpiece.ui.Activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.Models.Cart_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.adapters.CartItemListAdapter
import com.sg.lastpiece.utils.Constants
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_shop_cart.*

class ShopCartActivity : BaseActivity() {

    // A global variable for the product list.
    private lateinit var mBookList: ArrayList<Book_Details>

    // A global variable for the cart list items.
    private lateinit var mCartListItems: ArrayList<Cart_Details>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_cart)

        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this@ShopCartActivity, AddressActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        getBookList()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_shop_cart_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_shop_cart_activity.setNavigationOnClickListener { onBackPressed() }
    }

    //A function to get product list to compare the current stock with the cart items.
    private fun getBookList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllBooksList(this@ShopCartActivity)
    }

    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@ShopCartActivity)
    }

    //A function to notify the success result of the cart items list from cloud firestore.
    fun successCartItemsList(cartList: ArrayList<Cart_Details>) {

        // Hide progress dialog.
        hideProgressDialog()

        for (product in mBookList) {
            for (cart in cartList) {
                if (product.book_id == cart.book_id) {

                    cart.book_quantity = product.book_quantity

                    if (product.book_quantity.toInt() == 0) {
                        cart.cart_quantity = product.book_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0) {

            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_book_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@ShopCartActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemListAdapter(this@ShopCartActivity, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {

                val availableQuantity = item.cart_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.book_price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            tv_sub_total.text = "RM ${subTotal}0"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            tv_shipping_charge.text = "RM 8.00"

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 8
                tv_total_amount.text = "RM ${total}0"
            } else {
                ll_checkout.visibility = View.GONE
            }

        } else {
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_book_found.visibility = View.VISIBLE
        }
    }

    // A function to get the success result of product list.
    fun successProductsListFromFireStore(booksList: ArrayList<Book_Details>) {

        mBookList = booksList

        getCartItemsList()
    }

    fun itemRemovedSuccess() {

        hideProgressDialog()

        val toast:Toast = Toast.makeText(
                this@ShopCartActivity,
                resources.getString(R.string.msg_item_removed_successfully),
                Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        getCartItemsList()
    }


    //A function to notify the user about the item quantity updated in the cart list.

    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }


}