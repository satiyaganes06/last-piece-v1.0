package com.sg.lastpiece.FirebasePayment

import android.app.Application
import com.sg.lastpiece.BuildConfig
import com.sg.lastpiece.utils.Constants
import com.stripe.android.PaymentConfiguration

class FirebaseMobilePaymentsApp : Application(){
    override fun onCreate() {
        super.onCreate()

        //Public Key
        PaymentConfiguration.init(applicationContext, Constants.PUBLISHABLE_KEY)
    }
}