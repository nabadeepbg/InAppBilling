package com.nabadeepbg.inappbilling;

public interface SubscriptionListener {
    void onSubscribed(String packageName , String productId , String token);
    void onAlreadySubscribed();
    void onSubscribeCanceled();
    void onSubscribePending();
    void onSubscribeUnspecified();
    void onSubscribeError();
}
