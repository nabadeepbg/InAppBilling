package com.nabadeepbg.inappbilling;

public interface PaymentListener{
    void onPurchased();
    void onAlreadyPurchased();
    void onPurchaseCanceled();
    void onPurchasePending();
    void onPurchaseUnspecified();
    void onPurchaseError();
}
