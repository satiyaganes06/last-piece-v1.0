package com.sg.lastpiece.ui.Activities


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.Models.Cart_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.ViewActivity.ImageViewActivity
import com.sg.lastpiece.ui.adapters.BookDetailsSuggestedAdapter
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_book_details.*
import kotlinx.android.synthetic.main.activity_book_details.rv_dashboard_items

class BookDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mBookId : String = " "
    private var mBookOwnerID : String = " "
    private lateinit var mBookDetails : Book_Details
    private var quantity :Int = 0
    private var price : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        btn_remove.isEnabled = false;

        if (intent.hasExtra(Constants.EXTRA_BOOK_ID)) {
            mBookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)!!
        }


        if (intent.hasExtra(Constants.EXTRA_BOOK_OWNER_ID)) {
            mBookOwnerID = intent.getStringExtra(Constants.EXTRA_BOOK_OWNER_ID)!!
        }

        // Cannot Add to card owner's own book product
        if (FirestoreClass().getCurrentUserID() == mBookOwnerID) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
            btn_add.visibility = View.GONE
            btn_remove.visibility = View.GONE
            btn_contact.visibility = View.GONE
            tv_suggest_Title.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
            btn_remove.visibility = View.VISIBLE
            btn_contact.visibility = View.VISIBLE
        }


        btn_add_to_cart.setOnClickListener ( this )
        btn_go_to_cart.setOnClickListener ( this )
        btn_contact.setOnClickListener( this )

        getBookDetails()
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_book_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_book_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getBookDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBookDetails(this, mBookId)
    }

    @SuppressLint("SetTextI18n")
    fun bookDetailsSuccess(book: Book_Details) {
        mBookDetails = book

        if(FirestoreClass().getCurrentUserID() != mBookOwnerID){
            FirestoreClass().getBooksListInBookDetails(this, mBookDetails.book_faculty, mBookId)
        }
        // Populate the product details in the UI.
        GlideLoader(this@BookDetailsActivity).loadBookPicture(
                book.book_image,
                iv_book_detail_image
        )

        iv_book_detail_image.setOnClickListener {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra(Constants.EXTRA_BOOK_IMAGE, book.book_image)
            intent.putExtra(Constants.EXTRA_BOOK_TITLE, book.book_name)
            startActivity(intent)
        }

        tv_book_details_title.text = book.book_name
        et_item_quantity.hint = book.book_quantity
        tv_book_details_price.text = "RM ${book.book_price}0"
        tv_book_details_description.text = book.book_desc
        tv_book_details_condition_desc.text = book.book_condition
        tv_book_details_category_desc.text = book.book_category
        tv_book_details_faculty_desc.text = book.book_faculty
        tv_book_details_author_desc.text = book.book_author
        tv_book_details_seller_desc.text = book.user_name
        tv_book_details_seller_contact_desc.text = (book.user_contact).toString()


        btn_add.setOnClickListener {
            if(quantity < Integer.parseInt(book.book_quantity)){
                et_item_quantity.isEnabled = true
                quantity += 1
                et_item_quantity.setText(quantity.toString())

                price += book.book_price;
                tv_book_details_price.text = "RM " + price.toString() + "0";

                btn_add_to_cart.isEnabled = true
                btn_remove.isEnabled = true;
            }else {
                et_item_quantity.isEnabled = false
                btn_add_to_cart.isEnabled = false
                showErrorSnackBar(resources.getString(R.string.msg_for_available_stock), true)
            }
        }

        btn_remove.setOnClickListener {
            if(quantity != 1){
                et_item_quantity.isEnabled = true
                quantity -= 1
                et_item_quantity.setText(quantity.toString())

                price -= book.book_price;
                tv_book_details_price.text = "RM " + price.toString() + "0";

                btn_add_to_cart.isEnabled = true
            }else {
                et_item_quantity.isEnabled = false
                showErrorSnackBar(resources.getString(R.string.err_msg_to_mininum), true)
            }
        }

        //Update the UI if the stock quantity is 0.
        if(book.book_quantity.toInt() == 0){

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            btn_add_to_cart.visibility = View.GONE

            et_item_quantity.setText(resources.getString(R.string.lbl_out_of_stock))
            et_item_quantity.isEnabled = false


            et_item_quantity.setTextColor(
                    ContextCompat.getColor(
                            this@BookDetailsActivity,
                            R.color.colorSnackBarError
                    )
            )
        }else{

            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FirestoreClass().getCurrentUserID() == book.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@BookDetailsActivity, mBookId)
            }
        }

    }

    private fun addToCard(){

        if(quantity == 0){
            showErrorSnackBar(resources.getString(R.string.err_msg_to_choose), true)
        }else{

            val cart_Item = Cart_Details(
                    FirestoreClass().getCurrentUserID(),
                    mBookId,
                    mBookDetails.book_name,
                    (mBookDetails.book_price).toString(),
                    mBookDetails.book_image,
                    quantity.toString()
            )
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().addCartItems(this@BookDetailsActivity, cart_Item)
        }


    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.btn_add_to_cart -> {
                    addToCard()
                }
                R.id.btn_go_to_cart->{
                    val intent = Intent(this@BookDetailsActivity, ShopCartActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_contact ->{

                    val phoneNumber = "60"+ mBookDetails.user_contact.toString()
                    val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                    try {
                        packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    } catch (e: PackageManager.NameNotFoundException) {
                        Toast.makeText(this, "Whatsapp is not installed in your phone.", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    fun bookExistsInCart(){
        hideProgressDialog()
        btn_go_to_cart.visibility = View.VISIBLE
        btn_add_to_cart.visibility = View.GONE
    }

    fun addToCartSuccess(){
        hideProgressDialog()

        val toast: Toast = Toast.makeText(
                this@BookDetailsActivity,
                resources.getString(R.string.success_message_item_added_to_cart),
                Toast.LENGTH_SHORT
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }



    fun successBooksListInBookDetailsFromFireStore(dashboardBooksList: ArrayList<Book_Details>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (dashboardBooksList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE

            // Dashboard layout design
            rv_dashboard_items.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = BookDetailsSuggestedAdapter(this, dashboardBooksList)
            rv_dashboard_items.adapter = adapter


            adapter.setOnClickListener(object :
                    BookDetailsSuggestedAdapter.OnClickListener {
                override fun onClick(position: Int, book: Book_Details) {
                    val intent = Intent(this@BookDetailsActivity, BookDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_BOOK_ID, book.book_id)
                    intent.putExtra(Constants.EXTRA_BOOK_OWNER_ID, book.user_id)
                    startActivity(intent)

                }
            })

        } else {
            rv_dashboard_items.visibility = View.GONE
        }


    }



}