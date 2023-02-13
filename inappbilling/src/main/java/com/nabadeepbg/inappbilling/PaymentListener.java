package com.nabadeepbg.inappbilling;

public interface PaymentListener{
    void onPurchased(String packageName , String productId , String token);
    void onAlreadyPurchased();
    void onPurchaseCanceled();
    void onPurchasePending();
    void onPurchaseUnspecified();
    void onPurchaseError();
}
