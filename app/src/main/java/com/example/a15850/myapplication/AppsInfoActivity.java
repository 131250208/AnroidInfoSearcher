package com.example.a15850.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class AppsInfoActivity extends BasicActivity {

    public static void actionStart(Context context){
        Intent intent = new Intent(context, AppsInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_info);

        DataContainer dc = (DataContainer)getApplication();
        AppAdapter appAppAdapter = new AppAdapter(AppsInfoActivity.this, R.layout.list_item_app, dc.getApps());
        ListView listView = (ListView)findViewById(R.id.apps_list);
        listView.setAdapter(appAppAdapter);
    }
}
