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

public class InverseModActivity extends AppCompatActivity {

    private TextInputEditText edtA, edtM;
    private TableLayout tableResult;
    private TextView tvFinalResult;
    private MaterialCardView cardResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inverse_mod);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nghịch đảo Modulo");
        }

        edtA = findViewById(R.id.edtA);
        edtM = findViewById(R.id.edtM);
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
            if (edtA.getText() == null || edtM.getText() == null) return;
            
            String aStr = edtA.getText().toString().trim();
            String mStr = edtM.getText().toString().trim();

            if (aStr.isEmpty() || mStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ a và m", Toast.LENGTH_SHORT).show();
                return;
            }

            long a = Long.parseLong(aStr);
            long m = Long.parseLong(mStr);

            cardResult.setVisibility(View.VISIBLE);
            tableResult.removeAllViews();

            // Header cho Euclid mở rộng
            addTableRow(true, "B.lặp", "r", "q", "y", "y0", "y1", "m", "a");

            long aLocal = a;
            long mLocal = m;
            long y0 = 0, y1 = 1;
            int step = 0;

            addTableRow(false, "K.tạo", "-", "-", "-", String.valueOf(y0), String.valueOf(y1), String.valueOf(mLocal), String.valueOf(aLocal));
            step++;

            while (aLocal > 1) {
                long r = mLocal % aLocal;
                long q = mLocal / aLocal;
                long y = y0 - q * y1;
                
                long next_y0 = y1;
                long next_y1 = y;
                long next_m = aLocal;
                long next_a = r;

                addTableRow(false, String.valueOf(step), String.valueOf(r), String.valueOf(q), String.valueOf(y), 
                            String.valueOf(next_y0), String.valueOf(next_y1), String.valueOf(next_m), String.valueOf(next_a));
                
                y0 = next_y0;
                y1 = next_y1;
                mLocal = next_m;
                aLocal = next_a;
                step++;
                
                if (r == 0) break;
            }

            if (aLocal == 1) {
                long res = y1 < 0 ? y1 + m : y1;
                tvFinalResult.setText(String.format(Locale.getDefault(), "Kết quả: %d^-1 mod %d = %d", a, m, res));
            } else {
                tvFinalResult.setText("Không tồn tại nghịch đảo modulo (UCLN != 1)");
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lỗi định dạng số", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTableRow(boolean isHeader, String... cells) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        
        for (String cell : cells) {
            row.addView(createTextView(cell, isHeader));
        }
        tableResult.addView(row);
    }

    private TextView createTextView(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        tv.setPadding(2, 14, 2, 14);
        tv.setGravity(Gravity.CENTER);
        if (isHeader) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundResource(R.drawable.table_header_bg);
            tv.setTextSize(10);
        } else {
            tv.setBackgroundResource(R.drawable.table_border);
            tv.setTextSize(10);
        }
        tv.setTextColor(Color.BLACK);
        return tv;
    }
}
