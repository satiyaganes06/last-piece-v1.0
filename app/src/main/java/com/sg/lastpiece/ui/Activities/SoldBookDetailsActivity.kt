package com.sg.lastpiece.ui.Activities

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Order
import com.sg.lastpiece.Models.SoldBooks_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_sold_book_details.*
import kotlinx.android.synthetic.main.activity_sold_book_details.tv_order_status
import java.text.SimpleDateFormat
import java.util.*

class SoldBookDetailsActivity : BaseActivity() {


    private lateinit var option_Order_Status : Spinner
    private lateinit var mOrder_Status : String

    private var booksDetails: SoldBooks_Details = SoldBooks_Details()


    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sold_book_details)



        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            booksDetails =
                    intent.getParcelableExtra<SoldBooks_Details>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }

        setupActionBar()
        setupUI(booksDetails)
        dropdownList()

        btn_Update.setOnClickListener {
            order_Status_Update()
        }


    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(booksDetails: SoldBooks_Details) {

        tv_sold_product_details_id.text = booksDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = booksDetails.order_date
        tv_sold_product_details_date.text = formatter.format(calendar.time)

        GlideLoader(this@SoldBookDetailsActivity).loadBookPicture(
                booksDetails.book_image,
                iv_product_item_image
        )
        tv_product_item_name.text = booksDetails.book_name
        tv_product_item_price.text ="RM ${booksDetails.book_price}0"
        tv_sold_product_quantity.text = booksDetails.sold_book_quantity

        tv_sold_details_address_type.text = booksDetails.address.type
        tv_sold_details_full_name.text = booksDetails.address.name
        tv_sold_details_address.text =
                "${booksDetails.address.address}, ${booksDetails.address.zipCode}"
        tv_sold_details_additional_note.text = booksDetails.address.additionalNote

        if (booksDetails.address.otherDetails.isNotEmpty()) {
            tv_sold_details_other_details.visibility = View.VISIBLE
            tv_sold_details_other_details.text = booksDetails.address.otherDetails
        } else {
            tv_sold_details_other_details.visibility = View.GONE
        }
        tv_sold_details_mobile_number.text = booksDetails.address.mobileNumber

        tv_sold_product_sub_total.text ="RM "  + booksDetails.sub_total_amount + "0"
        tv_sold_product_shipping_charge.text = "RM " + booksDetails.shipping_charge + "0"
        tv_sold_product_total_amount.text = "RM " + booksDetails.total_amount + "0"
        tv_payment_methodm.text = booksDetails.payment_Method


        tv_order_status_view.text = booksDetails.order_Status

        if(tv_order_status_view.text.equals("Pending")){
            tv_order_status_view.setTextColor(
                ContextCompat.getColor(
                    this@SoldBookDetailsActivity,
                    R.color.colorAccent
                )
            )
        }else if(tv_order_status_view.text.equals("In Process")){
            tv_order_status_view.setTextColor(
                ContextCompat.getColor(
                    this@SoldBookDetailsActivity,
                    R.color.colorOrderStatusInProcess
                )
            )
        }else{
            tv_order_status_view.setTextColor(
                ContextCompat.getColor(
                    this@SoldBookDetailsActivity,
                    R.color.colorOrderStatusDelivered
                )
            )
        }
    }

    private fun dropdownList(){

        option_Order_Status = findViewById(R.id.tv_order_status) as Spinner

        val options = arrayOf("Pending", "In Process", "Delivered")


        option_Order_Status.adapter = ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,options)

        option_Order_Status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mOrder_Status = options.get(position)
            }
        }

    }

    private fun order_Status_Update(){

        showProgressDialog((resources.getString(R.string.please_wait)))

        val orderHashMap = HashMap<String, Any>()

        FirestoreClass().updateOrderStatus(
                this@SoldBookDetailsActivity,
                orderHashMap,
                booksDetails!!.soldbook_id, mOrder_Status, booksDetails!!.order_pk
        )

    }


    fun addUpdateOrderStatusSuccess() {

        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (booksDetails != null && booksDetails!!.soldbook_id.isNotEmpty()) {
            resources.getString(R.string.msg_order_status_updated_successfully)
        } else {
            resources.getString(R.string.error)
        }

        val toast: Toast = Toast.makeText(
                this@SoldBookDetailsActivity,
                notifySuccessMessage,
                Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()


        setResult(RESULT_OK)
        finish()
    }

}