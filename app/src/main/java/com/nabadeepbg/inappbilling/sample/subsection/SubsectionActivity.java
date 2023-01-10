package com.nabadeepbg.inappbilling.sample.subsection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

        buyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inAppSubscription !=null){
                    inAppSubscription.purchase();
                }
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Download Product");
            }
        });

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
        Log.i(TAG,"onSubscribed");

        buyProduct.setVisibility(View.GONE);
        btnDownload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNotSubscribed() {
        Log.i(TAG,"onNotSubscribed");
    }

    @Override
    public void onAlreadySubscribed() {
        Log.i(TAG,"onAlreadySubscribed");

        buyProduct.setVisibility(View.GONE);
        btnDownload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCanceled() {
        Log.i(TAG,"onCanceled");
    }

    @Override
    public void onPending() {
        Log.i(TAG,"onPending");
    }

    @Override
    public void onUnspecified() {
        Log.i(TAG,"onUnspecified");
    }

    @Override
    public void onError() {
        Log.i(TAG,"onError");
    }
}
