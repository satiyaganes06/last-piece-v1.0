package com.sg.lastpiece.ui.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sg.lastpiece.FirebasePayment.FirebaseEphemeralKeyProvider
import com.sg.lastpiece.FirebasePayment.FirebaseMobilePaymentsApp
import com.sg.lastpiece.FirebasePayment.PaymentFailActivity
import com.sg.lastpiece.FirebasePayment.PaymentSuccessActivity
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.*
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.adapters.CartItemListAdapter
import com.sg.lastpiece.utils.Constants
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.BillingAddressFields
import kotlinx.android.synthetic.main.activity_checkout.*
import java.lang.ref.WeakReference
import kotlin.math.min

// A CheckOut activity screen.
class CheckoutActivity : BaseActivity() {


    private var mAddressDetails: Address? = null

    private lateinit var mProductsList: ArrayList<Book_Details>

    private lateinit var mCartItemsList: ArrayList<Cart_Details>

    private var mSubTotal: Double = 0.0

    private var mTotalAmount: Double = 0.0

    private lateinit var mOrderDetails: Order

    private lateinit var mRandomFBID: String

   // private lateinit var option_Payment_Status :Spinner
    private var mPayment_Status : String = ""

    private var iMt = Constants.FIRST_PAYMENT_REQUEST_CODE

    //Stripe
    private lateinit var paymentSession: PaymentSession
    private lateinit var selectedPaymentMethod: PaymentMethod
    private val stripe: Stripe by lazy { Stripe(applicationContext, PaymentConfiguration.getInstance(applicationContext).publishableKey) }

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_checkout)
        FirebaseMobilePaymentsApp()
        selection_of_card.visibility = View.GONE


        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        setupActionBar()

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }


        btn_place_order.setOnClickListener {
            payment_Method()
        }

        getBooksList()
        dropdownList()

        paymentMethod.setOnClickListener {
            // Create the customer session and kick start the payment flow
            paymentSession.presentPaymentMethodSelection()
        }


        setupPaymentSession()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun getBooksList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllBooksList(this@CheckoutActivity)
    }


    fun successProductsListFromFireStore(booksList: ArrayList<Book_Details>) {

        mProductsList = booksList

        getCartItemsList()
    }


    //A function to get the list of cart items in the activity.
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
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

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter


        for (item in mCartItemsList) {

            val availableQuantity = item.book_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.book_price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "RM ${mSubTotal}0"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        tv_checkout_shipping_charge.text = "RM 0.00"
         tv_checkout_taxs_charge.text = "RM 0.00"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 0.0 + 0.0
            tv_checkout_total_amount.text = "RM ${mTotalAmount}0"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }

    }

     // A function to prepare the Order details to place an order.
     fun randomIDGenerator(isUnique: Boolean = false): String {
         val length: Int = 20
         if (0 == length) return ""
         val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') // Add your set here.

         if (isUnique) {
             val limit = min(length, alphabet.count())
             val set = mutableSetOf<Char>()
             do { set.add(alphabet.random()) } while (set.count() != limit)
             return set.joinToString("")
         }
         return List(length) { alphabet.random() }.joinToString("")
     }

    fun placeAnOrder() {

        mRandomFBID = randomIDGenerator(true)

        mOrderDetails = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].book_image,
            mSubTotal.toString(),
            "8.00", // The Shipping Charge is fixed as RM 8 for now in our case.
            mTotalAmount.toString(),
            System.currentTimeMillis(),
            order_Status = "Pending",
            mPayment_Status,
            mRandomFBID
            )

        FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails, mRandomFBID)

    }

    //A function to notify the success result of the order placed.
    fun orderPlacedSuccess() {

        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails, mRandomFBID)

    }


    fun allDetailsUpdatedSuccessfully() {

        // Hide the progress dialog.
        hideProgressDialog()

        val toast: Toast = Toast.makeText(
                this@CheckoutActivity,
                resources.getString(R.string.checkout_success_message),
                Toast.LENGTH_SHORT
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()

        val intent = Intent(this@CheckoutActivity, PaymentSuccessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun dropdownList(){

        rb_cash.setOnClickListener {
            rb_cash.isChecked  = true
            rb_debit.isChecked = false
            mPayment_Status = "Cash On Delivery"
            selection_of_card.visibility = View.GONE
        }

        rb_debit.setOnClickListener {
            rb_debit.isChecked  = true
            rb_cash.isChecked = false
            mPayment_Status = "Debit/Credit Card"
            selection_of_card.visibility = View.VISIBLE

        }
    }

    private fun payment_Method() {

       if(mPayment_Status == "Debit/Credit Card"){
            //First Try Method using Heroku
           /* val intent = Intent(this@CheckoutActivity, StripeDebitCardMethod::class.java)
            intent.putExtra(Constants.EXTRA_AMOUNT, mTotalAmount)
            intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, mAddressDetails)
            startActivity(intent)*/

            //Second Try using Firebase with Security
            if(paymentMethod.text != "Visa/Master"){
                showProgressDialog(resources.getString(R.string.please_wait))
                confirmPayment(selectedPaymentMethod.id!!)

            }else{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_select_card), true)
            }


        }else{
            showProgressDialog(resources.getString(R.string.please_wait))
            placeAnOrder()
        }


    }

    private fun setupPaymentSession () {
        // Setup Customer Session
        CustomerSession.initCustomerSession(this@CheckoutActivity, FirebaseEphemeralKeyProvider())
        // Setup a payment session
        paymentSession = PaymentSession(this@CheckoutActivity, PaymentSessionConfig.Builder()
                .setShippingInfoRequired(false)
                .setShippingMethodsRequired(false)
                .setBillingAddressFields(BillingAddressFields.None)
                .build())

        paymentSession.init(
                object: PaymentSession.PaymentSessionListener {
                    @SuppressLint("SetTextI18n")
                    override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                        Log.d("PaymentSession", "PaymentSession has changed: $data")
                        Log.d("PaymentSession", "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}")

                        if (data.isPaymentReadyToCharge) {
                            Log.d("PaymentSession", "Ready to charge");
                           // btn_place_order.isEnabled = true

                            data.paymentMethod?.let {
                                Log.d("PaymentSession", "PaymentMethod $it selected")
                                paymentMethod.text = "${it.card?.brand} card ends with ${it.card?.last4}"
                                selectedPaymentMethod = it
                            }

                        }
                    }

                    override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                        Log.d("PaymentSession",  "isCommunicating $isCommunicating")
                    }

                    override fun onError(errorCode: Int, errorMessage: String) {
                        Log.e("PaymentSession",  "onError: $errorCode, $errorMessage")
                    }
                }
        )

    }

    private fun confirmPayment(paymentMethodId: String) {

        val amount : Double = mTotalAmount*100

        val paymentCollection = Firebase.firestore
            .collection("stripe_customers").document(FirestoreClass().getCurrentUserID())
            .collection("payments")

        // Add a new document with a generated ID
        paymentCollection.add(hashMapOf(
            "amount" to amount,
            "currency" to "myr"
        ))
            .addOnSuccessListener { documentReference ->
                Log.d("payment", "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("payment", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("payment", "Current data: ${snapshot.data}")
                        val clientSecret = snapshot.data?.get("client_secret")
                        Log.d("payment", "Create paymentIntent returns $clientSecret")
                        clientSecret?.let {
                            stripe.confirmPayment(this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                paymentMethodId,
                                (it as String)))

                        }

                    } else {
                        Log.e("payment", "Current payment intent : null")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("payment", "Error adding document", e)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            paymentSession.handlePaymentData(requestCode, resultCode, data ?: Intent())

        val weakActivity = WeakReference<Activity>(this)

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {

            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent
                val status = paymentIntent.status

                if (status == StripeIntent.Status.Succeeded) {

                    weakActivity.get()?.let { activity ->

                        placeAnOrder()

                    }

                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                    //   Toast.makeText(this@StripeDebitCardMethod, "Payment failed", Toast.LENGTH_LONG).show()
                    weakActivity.get()?.let { activity ->
                        /*displayAlert(
                                activity,
                                "Payment failed",
                                paymentIntent.lastPaymentError?.message.orEmpty()
                        )*/

                        val intent = Intent(this@CheckoutActivity, PaymentFailActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    }
                }

            }

            override fun onError(e: Exception) {
                val intent = Intent(this@CheckoutActivity, PaymentFailActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }


        })

    }

}

