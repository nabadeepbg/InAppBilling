package com.nabadeepbg.inappbilling.sample.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.nabadeepbg.inappbilling.InAppBilling;
import com.nabadeepbg.inappbilling.InAppSubscription;
import com.nabadeepbg.inappbilling.PaymentListener;
import com.nabadeepbg.inappbilling.SubscriptionListener;
import com.nabadeepbg.inappbilling.sample.R;

public class PaymentActivity extends AppCompatActivity {

    public final String TAG = PaymentActivity.class.getName();

    Activity activity;
    Context context;

    InAppBilling inAppBilling;
    InAppSubscription inAppSubscription;

    Button btnDownload, buyProduct, buySubscribe;

    public final String product_id = "test_product";
    public final String subsection_id = "test_subsection";

    public boolean subscribe = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);
        context = this;
        activity = this;

        btnDownload = findViewById(R.id.btnDownload);
        buyProduct = findViewById(R.id.buyProduct);
        buySubscribe = findViewById(R.id.buySubscribe);

        downloadEnable(false);

        inAppBilling = new InAppBilling(context, product_id, new PaymentListener() {
            @Override
            public void onPurchased() {

                runOnUiThread(() -> {

                    Log.i(TAG,"PaymentActivity : onPurchased");
                    Toast.makeText(context, "onPurchased", Toast.LENGTH_SHORT).show();
                    downloadEnable(true);
                    paymentEnable(false);
                });


            }

            @Override
            public void onAlreadyPurchased() {

                runOnUiThread(() -> {

                    Log.i(TAG,"onAlreadyPurchased");
                    Toast.makeText(context, "onAlreadyPurchased", Toast.LENGTH_SHORT).show();
                    downloadEnable(true);
                    paymentEnable(false);
                });

            }

            @Override
            public void onPurchaseCanceled() {

                runOnUiThread(() -> {
                    Log.i(TAG,"onCanceled");
                    Toast.makeText(context, "onCanceled", Toast.LENGTH_SHORT).show();
                });


            }

            @Override
            public void onPurchasePending() {

                runOnUiThread(() -> {
                    Log.i(TAG,"onPending");
                    Toast.makeText(context, "onPending", Toast.LENGTH_SHORT).show();
                });


            }

            @Override
            public void onPurchaseUnspecified() {

                runOnUiThread(() -> {
                    Log.i(TAG,"onUnspecified");
                    Toast.makeText(context, "onUnspecified", Toast.LENGTH_SHORT).show();
                });


            }

            @Override
            public void onPurchaseError() {

                runOnUiThread(() -> {
                    Log.i(TAG,"onError");
                    Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();
                });


            }
        });

        inAppSubscription = new InAppSubscription(context, subsection_id, new SubscriptionListener() {
            @Override
            public void onSubscribed() {

                runOnUiThread(() -> {
                    Log.i(TAG,"onSubscribed");
                    Toast.makeText(context, "onSubscribed", Toast.LENGTH_SHORT).show();
                    downloadEnable(true);
                    subscribeEnable(false);
                    subscribe = true;
                });

            }

            @Override
            public void onAlreadySubscribed() {

                runOnUiThread(() -> {
                    Log.i(TAG,"onAlreadySubscribed");
                    Toast.makeText(context, "onAlreadySubscribed", Toast.LENGTH_SHORT).show();
                    downloadEnable(true);
                    subscribeEnable(false);
                    subscribe = true;
                });

            }

            @Override
            public void onSubscribeCanceled() {
                runOnUiThread(() -> {
                    Log.i(TAG,"onCanceled");
                    Toast.makeText(context, "onCanceled", Toast.LENGTH_SHORT).show();
                });


            }

            @Override
            public void onSubscribePending() {
                runOnUiThread(() -> {
                    Log.i(TAG,"onPending");
                    Toast.makeText(context, "onPending", Toast.LENGTH_SHORT).show();
                });


            }

            @Override
            public void onSubscribeUnspecified() {
                runOnUiThread(() -> {
                    Log.i(TAG,"onUnspecified");
                    Toast.makeText(context, "onUnspecified", Toast.LENGTH_SHORT).show();
                });


            }

            @Override
            public void onSubscribeError() {
                runOnUiThread(() -> {
                    Log.i(TAG,"onError");
                    Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();
                });


            }
        });

        buyProduct.setOnClickListener(view -> inAppBilling.purchase());

        buySubscribe.setOnClickListener(view -> inAppSubscription.subscribe());

        btnDownload.setOnClickListener(view -> {
            if (subscribe){
                Toast.makeText(context, "Download Item", Toast.LENGTH_SHORT).show();
            }else {
                inAppSubscription.subscribe();
            }
        });
    }
    @Override
    public void onBackPressed() {

        if (inAppBilling!=null){
            inAppBilling.endConnection();
        }

        if (inAppSubscription !=null){
            inAppSubscription.endConnection();
        }

        super.onBackPressed();
    }


    void downloadEnable(boolean enable){
        if (enable){
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setEnabled(true);
        }else {
            btnDownload.setVisibility(View.GONE);
            btnDownload.setEnabled(false);
        }
    }

    void paymentEnable(boolean enable){
        if (enable){
            buyProduct.setVisibility(View.VISIBLE);
            buyProduct.setEnabled(true);
        }else {
            buyProduct.setVisibility(View.GONE);
            buyProduct.setEnabled(false);
        }
    }

    void subscribeEnable(boolean enable){
        if (enable){
            buySubscribe.setVisibility(View.VISIBLE);
            buySubscribe.setEnabled(true);
        }else {
            buySubscribe.setVisibility(View.GONE);
            buySubscribe.setEnabled(false);
        }
    }

}
