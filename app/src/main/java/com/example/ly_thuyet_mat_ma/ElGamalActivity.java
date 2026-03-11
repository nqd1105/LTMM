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

public class ElGamalActivity extends AppCompatActivity {

    private EditText edtP, edtG, edtX, edtM, edtZ, edtC1, edtC2;
    private TextView tvY, tvEncResult, tvDecResult, tvTableYTitle, tvTableEncTitle, tvTableDecTitle;
    private LinearLayout llYContainer, llEncContainer, llDecContainer;
    private long p, g, x, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elgamal);

        // Enable Back button in Header
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.elgamal_title);
        }

        edtP = findViewById(R.id.edtP);
        edtG = findViewById(R.id.edtG);
        edtX = findViewById(R.id.edtX);
        edtM = findViewById(R.id.edtM);
        edtZ = findViewById(R.id.edtK); 
        edtC1 = findViewById(R.id.edtA); 
        edtC2 = findViewById(R.id.edtB); 

        tvY = findViewById(R.id.tvY);
        tvEncResult = findViewById(R.id.tvEncResult);
        tvDecResult = findViewById(R.id.tvDecResult);
        
        tvTableYTitle = findViewById(R.id.tvTableYTitle);
        tvTableEncTitle = findViewById(R.id.tvTableEncTitle);
        tvTableDecTitle = findViewById(R.id.tvTableDecTitle);
        
        llYContainer = findViewById(R.id.llYContainer); 
        llEncContainer = findViewById(R.id.llEncContainer); 
        llDecContainer = findViewById(R.id.llDecContainer);

        findViewById(R.id.btnCalcY).setOnClickListener(v -> createKeys());
        findViewById(R.id.btnEncrypt).setOnClickListener(v -> encrypt());
        findViewById(R.id.btnDecrypt).setOnClickListener(v -> decrypt());
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
            String gStr = edtG.getText().toString().trim();
            String xStr = edtX.getText().toString().trim();
            if (pStr.isEmpty() || gStr.isEmpty() || xStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập p, g, x", Toast.LENGTH_SHORT).show();
                return;
            }

            p = Long.parseLong(pStr);
            g = Long.parseLong(gStr);
            x = Long.parseLong(xStr);

            tvTableYTitle.setVisibility(View.VISIBLE);
            llYContainer.setVisibility(View.VISIBLE);
            llYContainer.removeAllViews();
            
            TableLayout table = createNewTable();
            addTableRowToTable(table, true, "B.lặp", "Số mũ", "Kết quả", "Cơ số");
            y = modPowTable(table, g, x, p);
            llYContainer.addView(table);
            
            tvY.setText(getString(R.string.y_format, g, x, p, y) + "\n\n" + getString(R.string.elgamal_key_summary, p, g, y, x));
            tvY.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            Toast.makeText(this, R.string.error_elgamal_params, Toast.LENGTH_SHORT).show();
        }
    }

    private void encrypt() {
        try {
            if (p <= 0) {
                createKeys();
                if (p <= 0) return;
            }

            String mStr = edtM.getText().toString().trim();
            String zStr = edtZ.getText().toString().trim();
            if (mStr.isEmpty() || zStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập M và z", Toast.LENGTH_SHORT).show();
                return;
            }
            long m = Long.parseLong(mStr);
            long z = Long.parseLong(zStr);

            tvTableEncTitle.setVisibility(View.VISIBLE);
            llEncContainer.setVisibility(View.VISIBLE);
            llEncContainer.removeAllViews();
            
            addHeaderLabel(llEncContainer, "1. Tính c1 = g^z mod p");
            TableLayout tableC1 = createNewTable();
            addTableRowToTable(tableC1, true, "B.lặp", "Số mũ", "Kết quả", "Cơ số");
            long c1 = modPowTable(tableC1, g, z, p);
            llEncContainer.addView(tableC1);

            addHeaderLabel(llEncContainer, "2. Tính c2 = (y^z * M) mod p");
            TableLayout tableC2 = createNewTable();
            addTableRowToTable(tableC2, true, "B.lặp", "Số mũ", "Kết quả", "Cơ số");
            long yz = modPowTable(tableC2, y, z, p);
            long c2 = (yz * m) % p;
            llEncContainer.addView(tableC2);
            
            TableLayout tableRes = createNewTable();
            addTableRowToTable(tableRes, false, "KẾT QUẢ", "c2 = (" + yz + " * " + m + ") mod " + p, "=>", String.valueOf(c2));
            llEncContainer.addView(tableRes);

            tvEncResult.setText(getString(R.string.elgamal_result_format, c1, c2, g, z, p, y, m));
            edtC1.setText(String.valueOf(c1));
            edtC2.setText(String.valueOf(c2));

        } catch (Exception ex) {
            Toast.makeText(this, R.string.error_input, Toast.LENGTH_SHORT).show();
        }
    }

    private void decrypt() {
        try {
            if (p <= 0) {
                createKeys();
                if (p <= 0) return;
            }

            long c1 = Long.parseLong(edtC1.getText().toString().trim());
            long c2 = Long.parseLong(edtC2.getText().toString().trim());

            tvTableDecTitle.setVisibility(View.VISIBLE);
            llDecContainer.setVisibility(View.VISIBLE);
            llDecContainer.removeAllViews();

            addHeaderLabel(llDecContainer, "1. Tính s = c1^x mod p");
            TableLayout tableS = createNewTable();
            addTableRowToTable(tableS, true, "B.lặp", "Số mũ", "Kết quả", "Cơ số");
            long s = modPowTable(tableS, c1, x, p);
            llDecContainer.addView(tableS);

            addHeaderLabel(llDecContainer, "2. Tính s^-1 mod p (Euclid)");
            TableLayout tableInv = createNewTable();
            addTableRowToTable(tableInv, true, "B.lặp", "r", "q", "y", "y0", "y1", "m", "a");
            long sInv = modInverseWithTable(tableInv, s, p);
            llDecContainer.addView(tableInv);

            long mResult = (c2 * sInv) % p;
            addHeaderLabel(llDecContainer, "3. Tính M = (c2 * s^-1) mod p");
            TableLayout tableM = createNewTable();
            addTableRowToTable(tableM, false, "KẾT QUẢ", "M = (" + c2 + " * " + sInv + ") mod " + p, "=>", String.valueOf(mResult));
            llDecContainer.addView(tableM);

            tvDecResult.setText(getString(R.string.elgamal_dec_result_format, c1, x, p, s, sInv, c2, mResult));
        } catch (Exception ex) {
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
            long q = mLocal / aLocal; 
            long y = y0 - q * y1;
            long next_y0 = y1, next_y1 = y, next_m = aLocal,  next_a = r;
            addTableRowToTable(table, false, String.valueOf(step), String.valueOf(r), String.valueOf(q), String.valueOf(y), String.valueOf(next_y0), String.valueOf(next_y1), String.valueOf(next_m), String.valueOf(next_a));
            y0 = next_y0; y1 = next_y1; mLocal = next_m; aLocal = next_a; step++;
        }
        long inverse = y1; if (inverse < 0) inverse += mIn;
        return inverse;
    }

    private void addTableRowToTable(TableLayout table, boolean isHeader, String... cells) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        boolean isSummary = cells.length > 0 && "KẾT QUẢ".equals(cells[0]);
        for (int i = 0; i < cells.length; i++) {
            row.addView(createTextView(cells[i], isHeader, i == 0, cells.length, i, isSummary));
        }
        table.addView(row);
    }

    private TextView createTextView(String text, boolean isHeader, boolean isFirstColumn, int totalCols, int index, boolean isSummary) {
        TextView tv = new TextView(this);
        tv.setText(text);
        float weight = 1.0f;
        if (totalCols > 6) {
            weight = isFirstColumn ? 0.7f : 1.0f;
        } else if (totalCols == 4) {
            if (isSummary) {
                if (index == 1) weight = 2.2f; 
                else if (index == 2) weight = 0.4f;
                else weight = 0.8f;
            } else {
                if (index == 0) weight = 0.7f;
                else if (index == 1) weight = 0.8f;
                else if (index == 2) weight = 1.3f;
                else weight = 1.1f;
            }
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