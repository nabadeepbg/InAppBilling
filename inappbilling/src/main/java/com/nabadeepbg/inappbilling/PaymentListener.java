package com.nabadeepbg.inappbilling;

public interface PaymentListener{
    void onPurchased();
    void onAlreadyPurchased();
    void onCanceled();
    void onPending();
    void onUnspecified();
    void onError();
}
