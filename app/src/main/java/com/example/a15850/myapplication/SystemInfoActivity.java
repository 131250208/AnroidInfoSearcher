package com.example.a15850.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SystemInfoActivity extends BasicActivity {


    public static void actionStart(Context context){
        Intent intent = new Intent(context, SystemInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);

    }
}
