package com.example.ly_thuyet_mat_ma;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrimitiveRootActivity extends AppCompatActivity {

    private TextInputEditText edtP;
    private TableLayout tableResult;
    private TextView tvSteps, tvFinalResult;
    private MaterialCardView cardResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primitive_root);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tìm phần tử sinh g");
        }

        edtP = findViewById(R.id.edtP);
        tableResult = findViewById(R.id.tableResult);
        tvSteps = findViewById(R.id.tvSteps);
        tvFinalResult = findViewById(R.id.tvFinalResult);
        cardResult = findViewById(R.id.cardResult);

        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculate());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculate() {
        try {
            String pStr = edtP.getText().toString().trim();
            if (pStr.isEmpty()) return;

            long p = Long.parseLong(pStr);
            if (!isPrime(p)) {
                Toast.makeText(this, "Vui lòng nhập số nguyên tố p", Toast.LENGTH_SHORT).show();
                return;
            }

            cardResult.setVisibility(View.VISIBLE);
            tableResult.removeAllViews();

            // 1. Phân tích p-1
            long phi = p - 1;
            List<Long> factors = getPrimeFactors(phi);
            StringBuilder steps = new StringBuilder();
            steps.append("1. Tính φ(p) = ").append(phi).append("\n");
            steps.append("2. Phân tích thừa số nguyên tố của ").append(phi).append(": ");
            for (int i = 0; i < factors.size(); i++) {
                steps.append(factors.get(i)).append(i == factors.size() - 1 ? "" : ", ");
            }
            steps.append("\n3. Các số mũ cần kiểm tra (phi/q_i): ");
            List<Long> exponents = new ArrayList<>();
            for (long q : factors) {
                exponents.add(phi / q);
                steps.append(phi / q).append(" ");
            }
            tvSteps.setText(steps.toString());

            // 2. Tạo bảng kiểm tra cho g chạy từ 2
            addTableRow(true, "g", "Kiểm tra g^(phi/q_i) mod p", "Kết luận");

            List<Long> primitiveRoots = new ArrayList<>();
            for (long g = 2; g < p && primitiveRoots.size() < 5; g++) { // Tìm tối đa 5 số đầu tiên
                StringBuilder checkStr = new StringBuilder();
                boolean isPrimitive = true;
                for (long exp : exponents) {
                    long res = modPow(g, exp, p);
                    checkStr.append(g).append("^").append(exp).append(" ≡ ").append(res).append("\n");
                    if (res == 1) {
                        isPrimitive = false;
                    }
                }
                
                String conclusion = isPrimitive ? "LÀ PTS" : "KHÔNG";
                addTableRow(false, String.valueOf(g), checkStr.toString().trim(), conclusion);
                
                if (isPrimitive) {
                    primitiveRoots.add(g);
                }
            }

            if (!primitiveRoots.isEmpty()) {
                tvFinalResult.setText("Kết quả: Các phần tử sinh g đầu tiên là: " + primitiveRoots.toString());
            } else {
                tvFinalResult.setText("Không tìm thấy phần tử sinh.");
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tính toán", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPrime(long n) {
        if (n < 2) return false;
        for (long i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private List<Long> getPrimeFactors(long n) {
        List<Long> factors = new ArrayList<>();
        for (long i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                factors.add(i);
                while (n % i == 0) n /= i;
            }
        }
        if (n > 1) factors.add(n);
        return factors;
    }

    private long modPow(long base, long exp, long mod) {
        long res = 1;
        base %= mod;
        while (exp > 0) {
            if (exp % 2 == 1) res = (res * base) % mod;
            base = (base * base) % mod;
            exp /= 2;
        }
        return res;
    }

    private void addTableRow(boolean isHeader, String c1, String c2, String c3) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        
        row.addView(createTextView(c1, isHeader, 0.4f));
        row.addView(createTextView(c2, isHeader, 1.8f));
        row.addView(createTextView(c3, isHeader, 0.8f));
        
        tableResult.addView(row);
    }

    private TextView createTextView(String text, boolean isHeader, float weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        tv.setPadding(8, 16, 8, 16);
        tv.setGravity(Gravity.CENTER);
        if (isHeader) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundResource(R.drawable.table_header_bg);
            tv.setTextSize(12);
        } else {
            tv.setBackgroundResource(R.drawable.table_border);
            tv.setTextSize(11);
        }
        tv.setTextColor(Color.BLACK);
        return tv;
    }
}
