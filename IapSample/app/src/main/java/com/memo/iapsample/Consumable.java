package com.memo.iapsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.List;

public class Consumable extends AppCompatActivity {

    private final String PRODUCT_PREMIUM = "lifetime";
    private final String NoAds = "NoAds";
    
    private ArrayList<String> purchaseItemIDs = new ArrayList<String>() {{
        add(PRODUCT_PREMIUM);
        add(NoAds);
    }};

    private String TAG = "iapSample";

    private BillingClient billingClient;

    Button btn_premium, btn_restore;
    TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumable);


        /* billing 클라이언트를 이용하여 결제를 시도 했을때, setListener() if/else문 안에 정의된 코드가 실행됩니다. */
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {

                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {

                                    Log.d(TAG, "Response is OK");
                                    handlePurchase(purchase);
                                }
                            } else {

                                Log.d(TAG, "Response NOT OK");
                            }
                        }
                ).build();

        /* billing 클라이언트 초기화후 연결 시도 */ 
        establishConnection();
        init();
    }

    void init() {
        btn_premium = this.findViewById(R.id.btn_premium);
        btn_restore = this.findViewById(R.id.btn_restore);
        tv_status = this.findViewById(R.id.tv_status);

        btn_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 버튼을 누를시 자세한 결제할 상품을 정보를 가지고 오고 결제 시도 */ 
                GetSingleInAppDetail();
            }
        });

        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 결제 복구 */ 
                restorePurchases();
            }
        });
    }


    void establishConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    /* Billing 클라이언트가 준비되었습니다. 여기서 구매할 품목들을 쿼리 및 가져올 수 있습니다. */ 

                    /* 성공적인 연결에 대한 세부 정보를 얻으려면 아래의 함수 중 하나를 사용하면됩니다 */

                    /* 
                    * GetSingleInAppDetail();
                    * GetListsInAppDetail(); 
                    */ 

                    Log.d(TAG, "Billing Client연결에 성공하였습니다!!");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                /* 구글 플레이에 다시 연결을 시도하기 위해 startConnection() 메소드를 호출합니다. */
                Log.d(TAG, "연결이 성립되지 않았습니다");
                establishConnection();
            }
        });
    }

    /*
     *
     * 공식 예제에서는 어떤 이유로 쿼리를 작성하기 위해 ImmutableList를 사용합니다.
     * 하지만 실제로 사용할 필요는 없습니다.
     * setProductList 메소드는 List<Product>를 입력으로 사용하며 ImmutableList는 필요로 하지 않습니다.
     *
     * */

    /*
     * 만약 API < 24라면, ArrayList를 대신 써서 사용해도 됩니다.
     * */

    /* 인앱 결제의 상품 1개의 세부 정보를 가져옵니다 */ 
    void GetSingleInAppDetail() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        /* 자신의 인앱 제품 ID를 setProductId()에 작성 */
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_PREMIUM)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {

                // 요청한 제품 세부정보로 원하는 작업을 수행하세요

                // 제품이 확인되면 구매 동작을 시작할 수 있도록 여기에서 이 함수를 호출합니다.
                // 이 세부 정보를 별도의 변수나 목록에 저장하여 다른 위치에서 호출할 수 있습니다.
                // 연결 설정 성공 상태에서 이 함수를 호출하려면 다른 함수를 생성하세요.
                LaunchPurchaseFlow(list.get(0));
            }
        });
    }

    /* 인앱 결제의 상품 여러개(리스트)의 세부 정보를 가져옵니다 */ 
    void GetListsInAppDetail() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        /* 자신의 인앱 제품 ID를 setProductId()에 작성 */
        for (String ids : purchaseItemIDs) {
            productList.add(
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(ids)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build());
        }

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {

                for (ProductDetails li : list) {
                    Log.d(TAG, "인앱 아이템 가격" + li.getOneTimePurchaseOfferDetails().getFormattedPrice());
                }
                
                // 요청한 제품 세부정보로 원하는 작업을 수행하세요
            }
        });
    }

    // 이 함수는 소비성 구매의 성공후 handlepurchase() 메소드 안에서 실행됩니다
    void ConsumePurchase(Purchase purchase) {
        ConsumeParams params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.consumeAsync(params, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {

                Log.d("TAG", "소비성 구매 성공!: "+s);
                tv_status.setText("제품이 소비되었습니다");
            }
        });
    }

    void LaunchPurchaseFlow(ProductDetails productDetails) {
        ArrayList<BillingFlowParams.ProductDetailsParams> productList = new ArrayList<>();

        productList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productList)
                .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    void handlePurchase(Purchase purchases) {
        if (!purchases.isAcknowledged()) {
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (String pur : purchases.getProducts()) {
                        if (pur.equalsIgnoreCase(PRODUCT_PREMIUM)) {
                            Log.d("TAG", "구매가 성공적으로 완료되었습니다");
                            tv_status.setText("야호! 구매되었습니다");

                            // 현재의 소비 제품을 소비하기 위해 ConsumePurchase 메소드를 호출합니다.
                            // 이렇게 하면 유저가 같은 제품을 다시 구매할 수 있습니다.
                            ConsumePurchase(purchases);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void restorePurchases() {

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (list.size() > 0) {

                                        Log.d("TAG", "인앱 복원 성공: " + list);
                                        for (int i = 0; i < list.size(); i++) {

                                            if (list.get(i).getProducts().contains(PRODUCT_PREMIUM)) {
                                                tv_status.setText("프리미엄 복원 완료");
                                                Log.d("TAG", "제품 id "+PRODUCT_PREMIUM+"가 여기서 복원되었습니다.");
                                            }

                                        }
                                    } else {
                                        tv_status.setText("복원할 제품을 찾지 못했습니다.");
                                        Log.d("TAG", "복원할 인앱 제품을 찾지 못했습니다.");
                                    }
                                }
                            });
                }
            }
        });
    }
}