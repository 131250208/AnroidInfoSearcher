package com.example.a15850.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UsrInfoActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usr_info);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UsrInfoActivity.class);
        context.startActivity(intent);
    }
}
