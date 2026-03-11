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

import java.util.Locale;

public class QuickPowActivity extends AppCompatActivity {

    private TextInputEditText edtA, edtB, edtN;
    private TableLayout tableResult;
    private TextView tvFinalResult;
    private MaterialCardView cardResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_pow);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lũy thừa nhanh");
        }

        edtA = findViewById(R.id.edtA);
        edtB = findViewById(R.id.edtB);
        edtN = findViewById(R.id.edtN);
        tableResult = findViewById(R.id.tableResult);
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
            if (edtA.getText() == null || edtB.getText() == null || edtN.getText() == null) return;
            
            String aStr = edtA.getText().toString().trim();
            String bStr = edtB.getText().toString().trim();
            String nStr = edtN.getText().toString().trim();

            if (aStr.isEmpty() || bStr.isEmpty() || nStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ a, b, n", Toast.LENGTH_SHORT).show();
                return;
            }

            long a = Long.parseLong(aStr);
            long b = Long.parseLong(bStr);
            long n = Long.parseLong(nStr);

            cardResult.setVisibility(View.VISIBLE);
            tableResult.removeAllViews();
            
            // Add Header
            addTableRow("B.lặp", "Số mũ", "Kết quả", "Cơ số", true);

            long r = 1;
            int step = 0;
            addTableRow("K.tạo", String.valueOf(b), String.valueOf(r), String.valueOf(a), false);
            step++;

            long tempB = b;
            long tempA = a;
            while (tempB > 0) {
                if (tempB % 2 == 1) r = (r * tempA) % n;
                tempA = (tempA * tempA) % n;
                addTableRow(String.valueOf(step), String.valueOf(tempB), String.valueOf(r), String.valueOf(tempA), false);

                tempB /= 2;
                step++;
            }

            tvFinalResult.setText(String.format(Locale.getDefault(), "Kết quả: %d^%d mod %d = %d", a, b, n, r));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lỗi định dạng số", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTableRow(String c1, String c2, String c3, String c4, boolean isHeader) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        
        row.addView(createTextView(c1, isHeader, 0.7f));
        row.addView(createTextView(c2, isHeader, 0.8f));
        row.addView(createTextView(c3, isHeader, 1.3f));
        row.addView(createTextView(c4, isHeader, 1.1f));
        
        tableResult.addView(row);
    }

    private TextView createTextView(String text, boolean isHeader, float weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        tv.setPadding(6, 14, 6, 14);
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
