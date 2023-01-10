package com.nabadeepbg.inappbilling;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nabadeepbg.inappbilling.sample.R;
import com.nabadeepbg.inappbilling.sample.products.ProductActivity;
import com.nabadeepbg.inappbilling.sample.subsection.SubsectionActivity;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getName();
    public final String product_id = "android.test.purchased";
    public final String subsection_id = "android.test.purchased";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"MainActivity - Open");
        setContentView(R.layout.activity_main);

        findViewById(R.id.openProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("product_id", product_id);
                startActivity(intent);
            }
        });

        findViewById(R.id.openSubsection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SubsectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("subscription_id", subsection_id);
                startActivity(intent);
            }
        });
    }
}