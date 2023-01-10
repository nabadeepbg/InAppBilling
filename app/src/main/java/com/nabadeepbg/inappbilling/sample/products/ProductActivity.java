package com.nabadeepbg.inappbilling.sample.products;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.nabadeepbg.inappbilling.InAppBilling;
import com.nabadeepbg.inappbilling.PaymentListener;
import com.nabadeepbg.inappbilling.sample.R;

public class ProductActivity extends AppCompatActivity implements PaymentListener {

    public final String TAG = ProductActivity.class.getName();

    public InAppBilling inAppBilling;

    Button buyProduct, btnDownload;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"ProductActivity - Open");
        setContentView(R.layout.activity_product);
        context = this;

        buyProduct = findViewById(R.id.buyProduct);
        btnDownload = findViewById(R.id.btnDownload);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){

            String product_id = bundle.getString("product_id","");

            if (product_id!=null && !product_id.isEmpty()){

                inAppBilling = new InAppBilling(context ,product_id, this);

            }
        }

        buyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inAppBilling!=null){
                    inAppBilling.purchase();
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

        if (inAppBilling!=null){
            inAppBilling.endConnection();
        }

        super.onBackPressed();
    }

    @Override
    public void onPurchased() {
        Log.i(TAG,"onPurchased");

        buyProduct.setVisibility(View.GONE);
        btnDownload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAlreadyPurchased() {
        Log.i(TAG,"onAlreadyPurchased");

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
