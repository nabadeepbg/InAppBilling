package com.nabadeepbg.inappbilling.validation;

public interface CallBackResponse{
    void onSuccess(Receipt receipt);
    void onFailed();
    void onError();

}