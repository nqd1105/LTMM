package com.example.ly_thuyet_mat_ma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide ActionBar only on Home Screen for better UI
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // RSA Card
        CardView cardRSA = findViewById(R.id.cardRSA);
        cardRSA.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RSAActivity.class);
            startActivity(intent);
        });

        // ElGamal Card
        CardView cardElGamal = findViewById(R.id.cardElGamal);
        cardElGamal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ElGamalActivity.class);
            startActivity(intent);
        });

        // Quick Pow Card
        CardView cardQuickPow = findViewById(R.id.cardQuickPow);
        cardQuickPow.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuickPowActivity.class);
            startActivity(intent);
        });

        // Inverse Mod Card
        CardView cardInverse = findViewById(R.id.cardInverse);
        cardInverse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InverseModActivity.class);
            startActivity(intent);
        });

        // Primitive Root Card
        CardView cardPrimitiveRoot = findViewById(R.id.cardPrimitiveRoot);
        cardPrimitiveRoot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PrimitiveRootActivity.class);
            startActivity(intent);
        });
    }
}
