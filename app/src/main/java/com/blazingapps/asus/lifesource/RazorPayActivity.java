package com.blazingapps.asus.lifesource;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "TAGZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);
        Checkout.preload(getApplicationContext());
    }

    @Override
    public void onPaymentSuccess(String s) {

    }

    @Override
    public void onPaymentError(int i, String s) {
        switch (i){
            case Checkout.NETWORK_ERROR:
                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                break;
            case Checkout.INVALID_OPTIONS:
                Toast.makeText(getApplicationContext(),"Invalid Options",Toast.LENGTH_LONG).show();
                break;
            case Checkout.PAYMENT_CANCELED:
                Toast.makeText(getApplicationContext(),"Payment Cancelled",Toast.LENGTH_LONG).show();
                break;
            case Checkout.TLS_ERROR:
                Toast.makeText(getApplicationContext(),"TLS Error",Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(),"Unknown Error",Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.blooddrop);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "REIED");

            /**
             * Description can be anything
             * eg: Order #123123
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Order #123456789");

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", "500");

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    public void payButton(View view) {
        startPayment();
    }
}

