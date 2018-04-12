package com.example.a15850.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppsInfoActivity extends AppCompatActivity {

    public static void actionStart(Context context){
        Intent intent = new Intent(context, AppsInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_info);

        AppAdapter appAppAdapter = new AppAdapter(AppsInfoActivity.this, R.layout.list_item_app, get_data());
        ListView listView = (ListView)findViewById(R.id.apps_list);
        listView.setAdapter(appAppAdapter);
    }

    private ArrayList<App> get_data(){
        ArrayList<App> apps = new ArrayList<App>();

        PackageManager pm = this.getPackageManager();

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for(PackageInfo pkgInfo: packageInfos){
            String appLabel = (String) pkgInfo.applicationInfo.loadLabel(pm);
            Drawable appIcon = pkgInfo.applicationInfo.loadIcon(pm);
            String pkgName = pkgInfo.packageName; // 获得应用程序的包名
            String versionName = pkgInfo.versionName;
            String backupAgentName = pkgInfo.applicationInfo.backupAgentName;
            String[] permissions = pkgInfo.requestedPermissions;
            int versionCode = pkgInfo.versionCode;

            App app = new App(appLabel, appIcon, pkgName, versionName);
            apps.add(app);

            if (permissions != null){
                for(String p : permissions){
                    Log.i("MainActivity", String.format("onCreate: %s", p));
                }
            }

            Log.i("MainActivity", String.format("onCreate: appLabel: %s, pkgName: %s, versionName: %s, versionCode: %d", appLabel, pkgName, versionName, versionCode));
        }

        return apps;
    }
}
