package com.nabadeepbg.inappbilling;

public interface SubscriptionListener {
    void onSubscribed();
    void onAlreadySubscribed();
    void onSubscribeCanceled();
    void onSubscribePending();
    void onSubscribeUnspecified();
    void onSubscribeError();
}
