package com.nabadeepbg.inappbilling.validation;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class PurchaseValidation {

    public final String TAG = PurchaseValidation.class.getName();

    RequestQueue requestQueue;
   CallBackResponse callBackResponse;

    public PurchaseValidation(Context context, CallBackResponse callBackResponse){
        this.callBackResponse = callBackResponse;
        this.requestQueue = Volley.newRequestQueue(context);
        Log.i(TAG,"PurchaseValidation : Initialize");
    }

    public void request(final String serverUrl,final Receipt receipt){

        Log.i(TAG,"PurchaseValidation : request");

        if (serverUrl.startsWith("http")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("status")){
                        if (jsonObject.getBoolean("status")){
                            callBackResponse.onSuccess(receipt);
                        }else {
                            callBackResponse.onFailed();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> callBackResponse.onError()) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("type", receipt.getType());
                    params.put("packageName", receipt.getPackageName());
                    params.put("productId", receipt.getProductId());
                    params.put("purchaseToken", receipt.getPurchaseToken());
                    return params;
                }
            };

            requestQueue.add(stringRequest);

        }else {
            callBackResponse.onFailed();
        }


    }

}
