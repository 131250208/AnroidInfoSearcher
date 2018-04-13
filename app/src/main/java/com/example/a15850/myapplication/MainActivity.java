package com.example.a15850.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt_sys_info = (Button)findViewById(R.id.button_system_info);
        bt_sys_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemInfoActivity.actionStart(MainActivity.this);
            }
        });

        Button bt_apps_info = (Button)findViewById(R.id.button_apps_info);
        bt_apps_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppsInfoActivity.actionStart(MainActivity.this);
            }
        });
    }
}
