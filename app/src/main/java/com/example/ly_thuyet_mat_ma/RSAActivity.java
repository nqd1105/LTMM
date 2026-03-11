package com.example.ly_thuyet_mat_ma;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RSAActivity extends AppCompatActivity {

    private EditText edtP, edtQ, edtE, edtInput;
    private TextView tvNPhi, tvD, tvResult, tvTableEuclidTitle, tvTablePowTitle;
    private LinearLayout llEuclidContainer, llPowContainer;
    private long n, phi, e, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa);

        // Enable Back button in Header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.rsa_title);
        }

        edtP = findViewById(R.id.edtP);
        edtQ = findViewById(R.id.edtQ);
        edtE = findViewById(R.id.edtE);
        edtInput = findViewById(R.id.edtInput);
        tvNPhi = findViewById(R.id.tvNPhi);
        tvD = findViewById(R.id.tvD);
        tvResult = findViewById(R.id.tvResult);
        tvTableEuclidTitle = findViewById(R.id.tvTableEuclidTitle);
        tvTablePowTitle = findViewById(R.id.tvTablePowTitle);
        
        llEuclidContainer = findViewById(R.id.llEuclidContainer);
        llPowContainer = findViewById(R.id.llPowContainer);

        findViewById(R.id.btnCalcPhi).setOnClickListener(v -> createKeys());

        findViewById(R.id.btnEncrypt).setOnClickListener(v -> performOperation(true));
        findViewById(R.id.btnDecrypt).setOnClickListener(v -> performOperation(false));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createKeys() {
        try {
            String pStr = edtP.getText().toString().trim();
            String qStr = edtQ.getText().toString().trim();
            String eStr = edtE.getText().toString().trim();
            
            if (pStr.isEmpty() || qStr.isEmpty() || eStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ p, q, e", Toast.LENGTH_SHORT).show();
                return;
            }
            
            long pVal = Long.parseLong(pStr);
            long qVal = Long.parseLong(qStr);
            e = Long.parseLong(eStr);
            
            n = pVal * qVal;
            phi = (pVal - 1) * (qVal - 1);
            tvNPhi.setText(getString(R.string.n_phi_format, pVal, qVal, n, pVal-1, qVal-1, phi));
            tvNPhi.setVisibility(View.VISIBLE);

            tvTableEuclidTitle.setVisibility(View.VISIBLE);
            llEuclidContainer.setVisibility(View.VISIBLE);
            llEuclidContainer.removeAllViews();
            
            TableLayout table = createNewTable();
            addTableRowToTable(table, true, "B.lặp", "r", "q", "y", "y0", "y1", "m", "a");
            d = modInverseWithTable(table, e, phi);
            llEuclidContainer.addView(table);
            
            tvD.setText(getString(R.string.d_format, d) + "\n\n" + getString(R.string.rsa_key_summary, n, e, d));
            tvD.setVisibility(View.VISIBLE);
            
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Lỗi định dạng số", Toast.LENGTH_SHORT).show();
        }
    }

    private void performOperation(boolean isEncrypt) {
        try {
            if (n <= 0 || d <= 0) {
                createKeys();
                if (n <= 0 || d <= 0) return;
            }

            String inputText = edtInput.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(this, R.string.error_input, Toast.LENGTH_SHORT).show();
                return;
            }
            long input = Long.parseLong(inputText);
            long exponent = isEncrypt ? e : d;
            
            llPowContainer.setVisibility(View.VISIBLE);
            llPowContainer.removeAllViews();
            
            if (isEncrypt) {
                addHeaderLabel(llPowContainer, "1. Tính C = M^e mod n");
            } else {
                addHeaderLabel(llPowContainer, "1. Tính M = C^d mod n");
            }
            
            tvTablePowTitle.setVisibility(View.VISIBLE);
            TableLayout table = createNewTable();
            addTableRowToTable(table, true, "B.lặp", "Số mũ", "Kết quả", "Cơ số");
            long result = modPowTable(table, input, exponent, n);
            llPowContainer.addView(table);
            
            if (isEncrypt) {
                tvResult.setText(getString(R.string.rsa_enc_result, input, exponent, n, result));
            } else {
                tvResult.setText(getString(R.string.rsa_dec_result, input, exponent, n, result));
            }
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.error_input, Toast.LENGTH_SHORT).show();
        }
    }

    private TableLayout createNewTable() {
        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        table.setStretchAllColumns(true);
        return table;
    }

    private void addHeaderLabel(LinearLayout container, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(16, 12, 16, 12);
        tv.setGravity(Gravity.START);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setBackgroundResource(R.drawable.table_header_bg);
        tv.setTextSize(12);
        tv.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 16;
        params.bottomMargin = 8;
        container.addView(tv, params);
    }

    private long modPowTable(TableLayout table, long a, long eVal, long mod) {
        long r = 1; int step = 0;
        addTableRowToTable(table, false, "K.tạo", String.valueOf(eVal), String.valueOf(r), String.valueOf(a));
        step++;
        long tempE = eVal; long tempA = a;
        while (tempE > 0) {
            if (tempE % 2 == 1) r = (r * tempA) % mod;
            tempA = (tempA * tempA) % mod;
            addTableRowToTable(table, false, String.valueOf(step), String.valueOf(tempE), String.valueOf(r), String.valueOf(tempA));
            tempE /= 2; step++;
        }
        return r;
    }

    private long modInverseWithTable(TableLayout table, long aIn, long mIn) {
        long aLocal = aIn; long mLocal = mIn; long y0 = 0, y1 = 1; int step = 0;
        addTableRowToTable(table, false, "K.tạo", "-", "-", "-", String.valueOf(y0), String.valueOf(y1), String.valueOf(mLocal), String.valueOf(aLocal));
        step++;
        while (aLocal > 1) {
            long r = mLocal % aLocal; 
            long q = mLocal / aLocal; long y = y0 - q * y1;
            long next_y0 = y1, next_y1 = y, next_m = aLocal, next_a = r;
            addTableRowToTable(table, false, String.valueOf(step), String.valueOf(r), String.valueOf(q), String.valueOf(y), String.valueOf(next_y0), String.valueOf(next_y1), String.valueOf(next_m), String.valueOf(next_a));
            y0 = next_y0; y1 = next_y1; mLocal = next_m; aLocal = next_a; step++;
            if (r == 0) break;
        }
        long inverse = y1; if (inverse < 0) inverse += mIn;
        return inverse;
    }

    private void addTableRowToTable(TableLayout table, boolean isHeader, String... cells) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < cells.length; i++) {
            row.addView(createTextView(cells[i], isHeader, i == 0, cells.length, i));
        }
        table.addView(row);
    }

    private TextView createTextView(String text, boolean isHeader, boolean isFirstColumn, int totalCols, int index) {
        TextView tv = new TextView(this);
        tv.setText(text);
        
        float weight = 1.0f;
        if (totalCols > 6) {
            weight = isFirstColumn ? 0.8f : 1.0f;
        } else if (totalCols == 4) {
            if (text.contains("mod") || text.contains("M =") || text.contains("c2 =")) weight = 2.2f;
            else if (text.equals("=>")) weight = 0.4f;
            else if (index == 0) weight = 0.8f;
            else weight = 1.0f;
        }
        
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        
        int paddingSide = (totalCols > 6) ? 2 : 6;
        tv.setPadding(paddingSide, 14, paddingSide, 14);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(false);

        int textSize = 11;
        if (totalCols > 7) textSize = 8;
        else if (totalCols > 5) textSize = 9;
        
        if (isHeader) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundResource(R.drawable.table_header_bg);
            tv.setTextSize(textSize + 1);
        } else {
            tv.setBackgroundResource(R.drawable.table_border);
            tv.setTextSize(textSize);
        }
        tv.setTextColor(Color.BLACK);
        return tv;
    }
}
