package com.example.a15850.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("BasicActivity", getClass().getSimpleName());
    }
}
