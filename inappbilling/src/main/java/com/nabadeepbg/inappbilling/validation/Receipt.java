package com.nabadeepbg.inappbilling.validation;

public class Receipt {

    String type;
    String packageName;
    String productId;
    String purchaseToken;

    public Receipt(String type, String packageName, String productId, String purchaseToken) {
        this.type = type;
        this.packageName = packageName;
        this.productId = productId;
        this.purchaseToken = purchaseToken;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProductId() {
        return productId;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public String getType() {
        return type;
    }
}
