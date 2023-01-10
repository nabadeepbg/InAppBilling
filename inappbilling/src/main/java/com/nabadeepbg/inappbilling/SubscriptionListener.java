package com.nabadeepbg.inappbilling;

public interface SubscriptionListener {
    void onSubscribed();
    void onNotSubscribed();
    void onAlreadySubscribed();
    void onCanceled();
    void onPending();
    void onUnspecified();
    void onError();
}
