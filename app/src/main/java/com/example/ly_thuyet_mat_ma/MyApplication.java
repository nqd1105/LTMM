package com.example.ly_thuyet_mat_ma;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Firebase persistence ONCE when the app starts.
        // This must be done before any other Firebase Database instance is called.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
