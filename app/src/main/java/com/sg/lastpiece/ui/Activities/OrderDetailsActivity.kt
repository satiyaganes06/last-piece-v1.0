package com.sg.lastpiece.ui.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Order
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.adapters.CartItemListAdapter
import com.sg.lastpiece.utils.Constants
import kotlinx.android.synthetic.main.activity_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class OrderDetailsActivity : BaseActivity() {
    private var order_id : String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_order_details)

        setupActionBar()
        showProgressDialog((resources.getString(R.string.please_wait)))
        var myOrderDetails: Order = Order()


        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails =
                intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
            order_id = intent.getStringExtra(Constants.ORDER_ID)!!
        }

        orderID()

        setupUI(myOrderDetails)
    }

    //A function for actionBar Setup.
    private fun setupActionBar() {

        setSupportActionBar(toolbar_my_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    @SuppressLint("SetTextI18n")
    private fun setupUI(orderDetails: Order) {

        tv_order_details_id.text = orderDetails.book_name

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        tv_order_details_date.text = orderDateTime


        // Get the difference between the order date time and current date time in hours.
        // If the difference in hours is 1 or less then the order status will be PENDING.
        // If the difference in hours is 2 or greater then 1 then the order status will be PROCESSING.
        // And, if the difference in hours is 3 or greater then the order status will be DELIVERED.
        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")


        tv_order_status.text = orderDetails.order_Status


        if(tv_order_status.text.equals("Pending")){
            tv_order_status.setTextColor(
                    ContextCompat.getColor(
                            this@OrderDetailsActivity,
                            R.color.colorAccent
                    )
            )
        }else if(tv_order_status.text.equals("In Process")){
            tv_order_status.setTextColor(
                    ContextCompat.getColor(
                            this@OrderDetailsActivity,
                            R.color.colorOrderStatusInProcess
                    )
            )
        }else{
            tv_order_status.setTextColor(
                    ContextCompat.getColor(
                            this@OrderDetailsActivity,
                            R.color.colorOrderStatusDelivered
                    )
            )

        }


        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter =
            CartItemListAdapter(this@OrderDetailsActivity, orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text = "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        } else {
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = "RM " + orderDetails.sub_total_amount + 0
        tv_order_details_shipping_charge.text = "RM " + orderDetails.shipping_charge
        tv_order_details_total_amount.text = "RM " + orderDetails.total_amount + 0
        tv_payment_method.text = orderDetails.payment_Method
    }

    private fun orderID(){

        val orderHashMap = HashMap<String, Any>()


        FirestoreClass().addOrderID(
                this@OrderDetailsActivity,
                orderHashMap,
                order_id
        )

    }

    fun addOrderIDSuccess() {

        // Hide progress dialog
        hideProgressDialog()

        setResult(RESULT_OK)

    }


}