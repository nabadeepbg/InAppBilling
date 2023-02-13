package com.nabadeepbg.inappbilling.sample.products;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.nabadeepbg.inappbilling.InAppBilling;
import com.nabadeepbg.inappbilling.PaymentListener;
import com.nabadeepbg.inappbilling.sample.R;
import com.nabadeepbg.inappbilling.validation.CallBackResponse;
import com.nabadeepbg.inappbilling.validation.PurchaseValidation;
import com.nabadeepbg.inappbilling.validation.Receipt;
import com.nabadeepbg.inappbilling.validation.Types;

public class ProductActivity extends AppCompatActivity implements PaymentListener {

    public final String TAG = ProductActivity.class.getName();

    public InAppBilling inAppBilling;

    Button buyProduct, btnDownload;

    Context context;


    PurchaseValidation validation;

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

        buyProduct.setOnClickListener(view -> {

            if (inAppBilling!=null){
                inAppBilling.purchase();
            }
        });

        btnDownload.setOnClickListener(view -> Log.i(TAG,"Download Product"));


        validation = new PurchaseValidation(context,  new CallBackResponse() {
            @Override
            public void onSuccess(Receipt receipt) {
                Log.i(TAG,"PurchaseValidation  : onSuccess");

                if (receipt.getType().equals(Types.Products)){
                    Log.i(TAG,"PurchaseValidation  : Products - onSuccess");

                    Log.i(TAG,"onPurchased 2");

                    runOnUiThread(() -> {

                        Toast.makeText(context, "onPurchased", Toast.LENGTH_SHORT).show();

                        buyProduct.setVisibility(View.GONE);
                        btnDownload.setVisibility(View.VISIBLE);
                    });
                }
            }

            @Override
            public void onFailed() {
                Log.i(TAG,"PurchaseValidation  : onFailed");
            }

            @Override
            public void onError() {
                Log.i(TAG,"PurchaseValidation  : onError");
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
    public void onPurchased(final String packageName, final String productId, final String token) {

        runOnUiThread(() -> {
            Log.i(TAG,"onPurchaseData  packageName : "+packageName);
            Log.i(TAG,"onPurchaseData  productId : "+productId);
            Log.i(TAG,"onPurchaseData  token : "+token);

            validation.request(getResources().getString(R.string.server_url),new Receipt(Types.Products,packageName,productId,token));

        });


    }

    @Override
    public void onAlreadyPurchased() {

        runOnUiThread(() -> {
            Log.i(TAG,"onAlreadyPurchased");
            Toast.makeText(context, "onAlreadyPurchased", Toast.LENGTH_SHORT).show();

            buyProduct.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
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
        Log.i(TAG,"onUnspecified");
        Toast.makeText(context, "onUnspecified", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPurchaseError() {
        Log.i(TAG,"onError");
        Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();

    }
}
