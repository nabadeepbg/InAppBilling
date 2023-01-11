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

public class InAppSubscription {

    private static final String TAG = InAppSubscription.class.getName();
    private BillingClient billingClient;
    private final Context context;
    private final String productId;
    private final SubscriptionListener subscriptionListener;
    private final PurchasesUpdatedListener purchasesUpdatedListener;
    private final ProductDetailsResponseListener productDetailsResponseListener;
    private final AcknowledgePurchaseResponseListener ackPurchase;

    public InAppSubscription(final Context context, final String productId, final SubscriptionListener subscriptionListener) {
        this.context = context;
        this.productId = productId;
        this.subscriptionListener = subscriptionListener;

        productDetailsResponseListener = (billingResult, list) -> {

            if (billingResult.getResponseCode() == OK) {
                if (list.size()!=0){
                    for (ProductDetails  details : list){

                        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = null;
                        if (details.getSubscriptionOfferDetails() != null) {
                            productDetailsParamsList = ImmutableList.of(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(details)
                                            .setOfferToken(details.getSubscriptionOfferDetails().get(0).getOfferToken())
                                            .build()
                            );

                            BillingFlowParams  billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(productDetailsParamsList)
                                    .build();

                            billingClient.launchBillingFlow((Activity) context, billingFlowParams);

                            Log.i(TAG,"InAppSubscription : launchBillingFlow");
                        }





                    }
                }

            }
        };

        purchasesUpdatedListener = (billingResult, purchases) -> {

            Log.i(TAG, "InAppSubscription - Purchases Updated : "+billingResult.getResponseCode());
            int responseCode = billingResult.getResponseCode();
            switch (responseCode) {
                case BillingClient.BillingResponseCode.OK:
                    if (purchases != null){
                        handlePurchases(purchases);
                    }
                    break;
                case BillingClient.BillingResponseCode.USER_CANCELED:
                    Log.i(TAG, "onPurchasesUpdated: User canceled the purchase");
                    if (subscriptionListener!=null){
                        subscriptionListener.onCanceled();
                        Log.i(TAG,"InAppSubscription - canceled");
                    }
                    break;
                case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                    Log.i(TAG, "onPurchasesUpdated: User item not owned");
                    if (subscriptionListener!=null){
                        subscriptionListener.onNotSubscribed();
                        Log.i(TAG,"InAppSubscription - ITEM_NOT_OWNED");
                    }
                    break;
                 case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    Log.i(TAG, "onPurchasesUpdated: User item already owned");
                    if (subscriptionListener!=null){
                        subscriptionListener.onAlreadySubscribed();
                        Log.i(TAG,"InAppSubscription - ITEM_ALREADY_OWNED");
                    }
                    break;
                default:
                    Log.i(TAG, "onPurchasesUpdated: Default");
                    if (subscriptionListener!=null){
                        subscriptionListener.onCanceled();
                        Log.i(TAG,"InAppSubscription - canceled");
                    }
            }
        };

        ackPurchase = billingResult -> {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                if (subscriptionListener!=null){
                    subscriptionListener.onSubscribed();
                    Log.i(TAG,"InAppSubscription - success");
                }
            }
        };

        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(purchasesUpdatedListener).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

                Log.i(TAG, "InAppSubscription - Disconnection");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                            (result, purchases) -> {
                                Log.i(TAG, "queryPurchasesAsync : result");
                                handlePurchases(purchases);
                            }
                    );
                }

            }
        });

        Log.i(TAG, "InAppSubscription - Connection");
    }

    public void subscribe() {
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
                        if (subscriptionListener !=null){
                            subscriptionListener.onError();
                        }
                    }

                }
            });

        }
    }

    public void endConnection(){
        billingClient.endConnection();
        Log.i(TAG,"InAppSubscription : endConnection");
    }


/*-------  Private Functions ------------*/

    private void initiatePurchase() {
        Log.i(TAG, "InAppSubscription - set Product -"+productId);
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(productId)
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build())).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, productDetailsResponseListener);

    }

    private void handlePurchases(List<Purchase> purchaseList){
        Log.i(TAG,"handlePurchases : start");
        for (Purchase purchase : purchaseList) {
            Log.i(TAG,"handlePurchases - : "+purchase.getPurchaseState());
            if (checkProductId(purchase) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                Log.i(TAG,"Purchase.PurchaseState.PURCHASED");
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }else {
                    if (subscriptionListener !=null){
                        subscriptionListener.onSubscribed();
                        Log.i(TAG,"InAppSubscription - success");
                    }
                }
            }else if (checkProductId(purchase) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING){
                Log.i(TAG,"Purchase.PurchaseState.PENDING");
                if (subscriptionListener !=null){
                    subscriptionListener.onPending();
                }
            } else if (checkProductId(purchase) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE){
                Log.i(TAG,"Purchase.PurchaseState.UNSPECIFIED_STATE");
                if (subscriptionListener !=null){
                    subscriptionListener.onUnspecified();
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