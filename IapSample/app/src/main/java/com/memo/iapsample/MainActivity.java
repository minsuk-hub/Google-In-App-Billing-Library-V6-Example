package com.memo.iapsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_nonconsumeable,btn_consumeable,btn_subscription;
    Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        
        btn_nonconsumeable = this.findViewById(R.id.btn_nonconsumeable);
        btn_consumeable = this.findViewById(R.id.btn_consumeable);
        btn_subscription = this.findViewById(R.id.btn_subscription);


        // 한번 구매하면 끝나는 상품 (예. 광고 제거)
        btn_nonconsumeable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, NonConsumable.class);
                startActivity(intent);
            }
        });


        // 계속 구매 가능한 상품 (예. 코인, 다이아몬드)
        btn_consumeable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(MainActivity.this, Consumable.class);
                startActivity(intent);
            }
        });


        // 정기 결제 화면
        btn_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(MainActivity.this, Subscription.class);
                startActivity(intent);
            }
        });
    }
}