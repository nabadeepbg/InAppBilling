package com.nabadeepbg.inappbilling;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class InAppBilling{

    private static final String TAG = InAppBilling.class.getName();
    private BillingClient billingClient;
    private final Context context;
    private final String productId;
    private final PaymentListener paymentListener;
    private final PurchasesUpdatedListener purchasesUpdatedListener;
    private final ProductDetailsResponseListener productDetailsResponseListener;
    private final AcknowledgePurchaseResponseListener ackPurchase;

    public InAppBilling(final Context context,final String productId,final PaymentListener paymentListener) {
        this.context = context;
        this.productId = productId;
        this.paymentListener = paymentListener;

        productDetailsResponseListener = (billingResult, list) -> {

            if (billingResult.getResponseCode() == OK) {
                if (list.size()!=0){
                    for (ProductDetails  details : list){

                        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(details)
                                                .build()
                                );

                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build();

                        billingClient.launchBillingFlow((Activity) context, billingFlowParams);

                        Log.i(TAG,"InAppBilling : launchBillingFlow");

                    }
                }

            }
        };

        purchasesUpdatedListener = (billingResult, purchases) -> {

            Log.i(TAG, "billingClient - Purchases Updated : "+billingResult.getResponseCode());
            int responseCode = billingResult.getResponseCode();
            switch (responseCode) {
                case BillingClient.BillingResponseCode.OK:
                    if (purchases != null){
                        handlePurchases(purchases);
                    }
                    break;
                case BillingClient.BillingResponseCode.USER_CANCELED:
                    Log.i(TAG, "onPurchasesUpdated: User canceled the purchase");
                    if (paymentListener!=null){
                        paymentListener.onPurchaseCanceled();
                        Log.i(TAG,"InAppBilling - canceled");
                    }
                    break;
                case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    Log.i(TAG, "onPurchasesUpdated: User item already owned");
                    if (paymentListener!=null){
                        paymentListener.onAlreadyPurchased();
                        Log.i(TAG,"InAppBilling - ITEM_ALREADY_OWNED");
                    }
                    break;
                default:
                    Log.i(TAG, "onPurchasesUpdated: Default");
                    if (paymentListener!=null){
                        paymentListener.onPurchaseCanceled();
                        Log.i(TAG,"InAppBilling - canceled");
                    }
            }
        };

        ackPurchase = billingResult -> {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                if (paymentListener!=null){
                    paymentListener.onPurchased();
                    Log.i(TAG,"InAppBilling - success");
                }
            }
        };

        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(purchasesUpdatedListener).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

                Log.i(TAG, "BillingClient - Disconnection");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                            (result, purchases) -> {
                                Log.i(TAG, "queryPurchasesAsync : result");
                                handlePurchases(purchases);
                            }
                    );
                }

            }
        });

        Log.i(TAG, "BillingClient - Connection");
    }

    public void purchase() {
        if (billingClient.isReady()) {
            initiatePurchase();
        }else {
            billingClient = BillingClient.newBuilder(context)
                    .enablePendingPurchases()
                    .setListener(purchasesUpdatedListener).build();

            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {

                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase();
                    }else {
                        if (paymentListener!=null){
                            paymentListener.onPurchaseError();
                        }
                    }

                }
            });

        }
    }

    public void endConnection(){
        billingClient.endConnection();
        Log.i(TAG,"InAppBilling : endConnection");
    }


/*-------  Private Functions ------------*/

    private void initiatePurchase() {
        Log.i(TAG, "BillingClient - set Product -"+productId);
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(productId)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build())).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, productDetailsResponseListener);
    }

    private void handlePurchases(List<Purchase> purchaseList){
        for (Purchase purchase : purchaseList) {

            if (checkProductId(purchase) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                Log.i(TAG,"Purchase.PurchaseState.PURCHASED");

                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }else {
                    if (paymentListener!=null){
                        paymentListener.onPurchased();
                        Log.i(TAG,"InAppBilling - success");
                    }
                }
            }else if (checkProductId(purchase) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING){
                Log.i(TAG,"Purchase.PurchaseState.PENDING");
                if (paymentListener!=null){
                    paymentListener.onPurchasePending();
                }
            } else if (checkProductId(purchase) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE){
                Log.i(TAG,"Purchase.PurchaseState.UNSPECIFIED_STATE");
                if (paymentListener!=null){
                    paymentListener.onPurchaseUnspecified();
                }
            }else {
                Log.i(TAG,"PurchaseState : "+purchase.getPurchaseState());
            }
        }


    }

    private boolean checkProductId(Purchase purchase) {
        return productId.equals(purchase.getProducts().get(0));
    }

}