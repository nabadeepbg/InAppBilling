package com.nabadeepbg.inappbilling.sample.subsection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.nabadeepbg.inappbilling.InAppSubscription;
import com.nabadeepbg.inappbilling.SubscriptionListener;
import com.nabadeepbg.inappbilling.sample.R;

public class SubsectionActivity extends AppCompatActivity implements SubscriptionListener {

    public final String TAG = SubsectionActivity.class.getName();

    public InAppSubscription inAppSubscription;

    Button buyProduct, btnDownload;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsection);
        Log.i(TAG,"SubsectionActivity - Open");
        context = this;

        buyProduct = findViewById(R.id.buySubscribe);
        btnDownload = findViewById(R.id.btnDownload);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){

            String subscription_id = bundle.getString("subscription_id","");

            if (subscription_id!=null && !subscription_id.isEmpty()){

                inAppSubscription = new InAppSubscription(context ,subscription_id, this);

            }
        }

        buyProduct.setOnClickListener(view -> {

            if (inAppSubscription !=null){
                inAppSubscription.subscribe();
            }
        });

        btnDownload.setOnClickListener(view -> Log.i(TAG,"Download Product"));

    }


    @Override
    public void onBackPressed() {

        if (inAppSubscription !=null){
            inAppSubscription.endConnection();
        }

        super.onBackPressed();
    }

    @Override
    public void onSubscribed() {

        runOnUiThread(() -> {
            Log.i(TAG,"onSubscribed");
            Toast.makeText(context, "onSubscribed", Toast.LENGTH_SHORT).show();

            buyProduct.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void onAlreadySubscribed() {

        runOnUiThread(() -> {
            Log.i(TAG,"onAlreadySubscribed");
            Toast.makeText(context, "onAlreadySubscribed", Toast.LENGTH_SHORT).show();


            buyProduct.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
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
        Log.i(TAG,"onUnspecified");
        Toast.makeText(context, "onUnspecified", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSubscribeError() {
        Log.i(TAG,"onError");
        Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();

    }
}
