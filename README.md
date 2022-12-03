# InAppBilling
Simple InAppBilling android library to implement google in-app purchases.

Step 1. Add the JitPack repository to your build file

```gradle
maven { url 'https://www.jitpack.io' }
```

Step 2. Add the dependency

```gradle
implementation 'com.github.nabadeepbg:InAppBilling:1.0.0'
```

Example Code :

Initialize InAppBilling in Activity :

```java

InAppBilling inAppBilling = new InAppBilling(context, product_id, new PaymentListener() {
                    @Override
                    public void onPurchased() {
                        
                    }

                    @Override
                    public void onAlreadyPurchased() {

                    }

                    @Override
                    public void onCanceled() {

                    }

                    @Override
                    public void onPending() {

                    }

                    @Override
                    public void onUnspecified() {

                    }

                    @Override
                    public void onError() {

                    }
                });

```

Request to purchase :

```java

buyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inAppBilling!=null){
                    inAppBilling.purchase();
                }
            }
        });

```

Before stopping the activity make sure to end the connection of purchase. :

```java

  @Override
    public void onBackPressed() {

        if (inAppBilling!=null){
            inAppBilling.endConnection();
        }

        super.onBackPressed();
    }
```
