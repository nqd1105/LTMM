package com.example.ly_thuyet_mat_ma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_IS_VERIFIED = "isDeviceVerified";
    private static final String DB_URL = "https://ltmm-c567d-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> handleLogin());

        // ƯU TIÊN: Kiểm tra trạng thái Offline trước để vào app siêu nhanh
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (isDeviceVerifiedLocally()) {
                // Đã từng đăng nhập và khớp thiết bị, cho vào ngay không cần mạng
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Đã login Auth nhưng chưa lưu dấu thiết bị, cần kiểm tra Online lần cuối
                checkDeviceAndNavigate(currentUser.getUid());
            }
        }
    }

    private boolean isDeviceVerifiedLocally() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_VERIFIED, false);
    }

    private void saveDeviceVerifiedLocally(boolean verified) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_VERIFIED, verified).apply();
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkDeviceAndNavigate(user.getUid());
                        }
                    } else {
                        setLoading(false);
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi đăng nhập";
                        Toast.makeText(LoginActivity.this, "Thất bại: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkDeviceAndNavigate(String userId) {
        setLoading(true);
        String currentDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DatabaseReference userRef = mDatabase.child("Users").child(userId);

        userRef.child("deviceId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String savedDeviceId = snapshot.getValue(String.class);

                if (savedDeviceId == null || savedDeviceId.isEmpty()) {
                    // Tài khoản mới hoặc Admin đã reset trên Web
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("deviceId", currentDeviceId);
                    updates.put("email", mAuth.getCurrentUser().getEmail());

                    userRef.updateChildren(updates).addOnCompleteListener(task -> {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            saveDeviceVerifiedLocally(true); // Lưu dấu thành công
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(LoginActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (savedDeviceId.equals(currentDeviceId)) {
                    // Khớp thiết bị, lưu dấu và vào app
                    saveDeviceVerifiedLocally(true);
                    setLoading(false);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Sai thiết bị
                    saveDeviceVerifiedLocally(false);
                    setLoading(false);
                    mAuth.signOut();
                    showErrorDialog("Tài khoản này đã được gắn với thiết bị khác. Liên hệ Zalo: 0976790962 để đổi máy.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                setLoading(false);
                // ĐẶC BIỆT: Nếu lỗi do mất mạng nhưng máy này ĐÃ từng xác thực thành công thì vẫn cho vào
                if (isDeviceVerifiedLocally()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Yêu cầu có mạng cho lần đầu đăng nhập!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        edtEmail.setEnabled(!isLoading);
        edtPassword.setEnabled(!isLoading);
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Truy cập bị chặn")
                .setMessage(message)
                .setPositiveButton("Đã hiểu", null)
                .setCancelable(false)
                .show();
    }
}
