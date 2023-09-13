package com.sg.lastpiece.ui.Activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Address
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.Models.Cart_Details
import com.sg.lastpiece.Models.Order
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.adapters.CartItemListAdapter
import com.sg.lastpiece.ui.ui.Fragments.DashboardFragment
import com.sg.lastpiece.utils.Constants
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.StripeIntent
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_stripe_debit_card_method.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference


class StripeDebitCardMethod : BaseActivity() {
/*
    // 10.0.2.2 is the Android emulator's alias to localhost
    //private val backendUrl = "http://10.0.2.2:4242/"
    private val backendUrl = "https://stripe-sg-lastpiece.herokuapp.com/"
    private val httpClient = OkHttpClient()
    private lateinit var paymentIntentClientSecret: String
    private lateinit var stripe: Stripe

    private var mAddressDetails: Address? = null
    private var mTotalAmount: Double = 0.0
    private lateinit var mProductsList: ArrayList<Book_Details>
    private lateinit var mCartItemsList: ArrayList<Cart_Details>
    private var mSubTotal: Double = 0.0
    private lateinit var mOrderDetails: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_debit_card_method)


        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
        stripe = Stripe(applicationContext, "pk_test_51IhgxBL2vyqdLfOM8zKw7tOtiC1codB7WrtZgbQdg9jRhTZx49APscD2odKeJMrd55z7YRvLG19fDdGTCikjOzv100B7V85MrS")

        mTotalAmount = intent.getDoubleExtra(Constants.EXTRA_AMOUNT, 0.0)

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        startCheckout()
        setupActionBar()

    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_strip_payment_debit_card_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_strip_payment_debit_card_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun displayAlert(

            activity: Activity,
            title: String,
            message: String,
            restartDemo: Boolean = false
    ) {
        runOnUiThread {
            val builder = AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
            builder.setPositiveButton("Ok", null)
            builder.create().show()
        }

    }


    private fun startCheckout() {

        val weakActivity = WeakReference<Activity>(this)

        // Create a PaymentIntent by calling your server's endpoint.
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val amount : Double = mTotalAmount*100

        val requestJson = """
                              {
                                  "currency":"myr",
                                  "amount" : $amount
                              }
                          """

        val body = requestJson.toRequestBody(mediaType)

        val request = Request.Builder()
                .url(backendUrl + "create-payment-intent")
                .post(body)
                .build()
        httpClient.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                        weakActivity.get()?.let { activity ->
                            displayAlert(activity, "Failed to load page", "Error: $e")
                        }

                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            weakActivity.get()?.let { activity ->
                                displayAlert(
                                        activity,
                                        "Failed to load page",
                                        "Error: $response"
                                )
                                showErrorSnackBar("Failed to load page" , true)

                            }

                        } else {

                            val responseData = response.body?.string()
                            val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()

                            // For added security, our sample app gets the publishable key
                            // from the server.
                            //Unique Code
                            paymentIntentClientSecret = responseJson.getString("clientSecret")
                        }
                    }
                })

        // Hook up the pay button to the card widget and stripe instance

        btn_pay.setOnClickListener {
            cardInputWidget.paymentMethodCreateParams?.let { params ->

                val confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)

                stripe.confirmPayment(this, confirmParams)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val weakActivity = WeakReference<Activity>(this)

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {

            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent
                val status = paymentIntent.status

                if (status == StripeIntent.Status.Succeeded) {
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    weakActivity.get()?.let { activity ->

                        /*displayAlert(
                                activity,
                                "Payment succeeded",
                                gson.toJson(paymentIntent)
                        )*/

                        val toast: Toast = Toast.makeText(
                                this@StripeDebitCardMethod,
                                resources.getString(R.string.payment_success_message),
                                Toast.LENGTH_LONG
                        )

                        val view = toast.view
                        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                        toast.show()

                        getBooksList()

                    }

                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                 //   Toast.makeText(this@StripeDebitCardMethod, "Payment failed", Toast.LENGTH_LONG).show()
                    weakActivity.get()?.let { activity ->
                        /*displayAlert(
                                activity,
                                "Payment failed",
                                paymentIntent.lastPaymentError?.message.orEmpty()
                        )*/
                        val toast: Toast = Toast.makeText(
                                this@StripeDebitCardMethod,
                                resources.getString(R.string.payment_fail_message),
                                Toast.LENGTH_LONG
                        )

                        val view = toast.view
                        view?.background?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                        toast.show()

                    }
                }

            }

            override fun onError(e: Exception) {

                weakActivity.get()?.let { activity ->

                    displayAlert(
                            activity,
                            "Payment failed",
                            e.toString()
                    )
                    showErrorSnackBar("Payment Failed" , true)
                }

            }

        })

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun getBooksList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllBooksList(this@StripeDebitCardMethod)
    }


    fun successProductsListFromFireStore(booksList: ArrayList<Book_Details>) {

        mProductsList = booksList

        getCartItemsList()
    }


    //A function to get the list of cart items in the activity.
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@StripeDebitCardMethod)
    }


    // A function to notify the success result of the cart items list from cloud firestore.
    fun successCartItemsList(cartList: ArrayList<Cart_Details>) {

        // Hide progress dialog.
        hideProgressDialog()

        for (book in mProductsList) {
            for (cart in cartList) {
                if (book.book_id == cart.book_id) {
                    cart.book_quantity = book.book_quantity
                    cart.seller_id = book.user_id
                }
            }
        }

        mCartItemsList = cartList
        placeAnOrder()
    }

    // A function to prepare the Order details to place an order.

    fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        mOrderDetails = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].book_image,
            mSubTotal.toString(),
            "8.00", // The Shipping Charge is fixed as RM 10 for now in our case.
            mTotalAmount.toString(),
            System.currentTimeMillis(),
            order_Status = "Pending",
            "Debit/Credit Card",

        )

        FirestoreClass().placeOrder(this@StripeDebitCardMethod, mOrderDetails)

    }

    //A function to notify the success result of the order placed.
    fun orderPlacedSuccess() {

        FirestoreClass().updateAllDetails(this@StripeDebitCardMethod, mCartItemsList, mOrderDetails)

    }


    fun allDetailsUpdatedSuccessfully() {

        // Hide the progress dialog.
        hideProgressDialog()

        val toast: Toast = Toast.makeText(
            this@StripeDebitCardMethod,
            resources.getString(R.string.checkout_success_message),
            Toast.LENGTH_LONG
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        val intent = Intent(this@StripeDebitCardMethod, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }*/

}

