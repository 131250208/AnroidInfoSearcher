package com.example.a15850.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<App> apps_info = get_data();
        DataContainer dc = (DataContainer)getApplication();
        dc.setApps(apps_info);

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


        Button bt_cve_info = (Button)findViewById(R.id.button_cve_info);
        bt_cve_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCVE(find_cves());
            }
        });

        Button bt_user_info = (Button)findViewById(R.id.button_user_info);
        bt_user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfoActivity.actionStart(MainActivity.this);
            }
        });

        Button bt_monitor_noti_bar = (Button)findViewById(R.id.button_monitor_noti_bar);
        bt_monitor_noti_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonitorNotiBarActivity.actionStart(MainActivity.this);
            }
        });
    }
    private ArrayList<CVE> find_cves(){
        ArrayList<CVE> cves = new ArrayList<CVE>();
        DataContainer dc = (DataContainer)getApplication();
        ArrayList<App> apps = dc.getApps();
        String sysVersion = Build.VERSION.RELEASE;

        // find cve of sys
        MyDBHelper myDBHelper = new MyDBHelper(MainActivity.this, "CveStore.db", null, 2);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.query("cves", new String[]{"cve_num"}, "prod_type = ? and product = ? and version = ?",
                new String[]{"OS", "Android", sysVersion}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String cve_num = cursor.getString(cursor.getColumnIndex("cve_num"));
                cves.add(new CVE(cve_num, "Android", sysVersion));
            }while (cursor.moveToNext());
        }
        cursor.close();

        //find cve of apps
        for(App app: apps){
            cursor = db.query("cves", new String[]{"cve_num"}, "prod_type = ? and product = ? and version = ?",
                    new String[]{"Application", app.getAppLabel(), app.getVersionName()}, null, null, null);
            if(cursor.moveToFirst()){
                do{
                    String cve_num = cursor.getString(cursor.getColumnIndex("cve_num"));
                    cves.add(new CVE(cve_num, app.getAppLabel(), app.getVersionName()));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

        return cves;
    }

    private void showCVE(ArrayList<CVE> cves) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if(cves.size() == 0){
            builder.setTitle("啊哦");
            builder.setMessage("没有找到已公开的潜在漏洞...");
        }else {
            builder.setTitle("潜在漏洞");
            StringBuilder stringBuilder = new StringBuilder();

            for (CVE cve: cves){
                stringBuilder.append(String.format("%s \n\tproduct: %s, version: %s\n", cve.getNum(), cve.getProduct(), cve.getVersion()));
            }

            builder.setMessage(stringBuilder.toString());
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
//            String backupAgentName = pkgInfo.applicationInfo.backupAgentName;
//            String[] permissions = pkgInfo.requestedPermissions;
            int versionCode = pkgInfo.versionCode;
            String type = "第三方应用";
            int flags = pkgInfo.applicationInfo.flags;
            if((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP&flags) == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP){
                type = "更新过的系统应用";
            }else if((ApplicationInfo.FLAG_SYSTEM&flags) == ApplicationInfo.FLAG_SYSTEM) {
                type = "原生系统应用";
            }

            App app = new App(appLabel, appIcon, pkgName, versionName, type, versionCode);
            apps.add(app);

            Log.i("MainActivity", String.format("onCreate: appLabel: %s, pkgName: %s, versionName: %s, versionCode: %d", appLabel, pkgName, versionName, versionCode));
        }

        return apps;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    callPhone();
                } else {
                    Log.i("MainActivity", "没有权限操作这个请求");
                }
                break;

        }
    }

    private void callPhone(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + Uri.encode("*#*#4636#*#,*")));
            startActivity(intent);
        }
    }
    private boolean checkPermissions(String permission){
        if (Build.VERSION.SDK_INT < 23) {//一般android6以下会在安装时自动获取权限,但在小米机上，可能通过用户权限管理更改权限
            return true;
        }else {

            if (getApplicationInfo().targetSdkVersion < 23) {
                //targetSdkVersion<23时 即便运行在android6及以上设备 ContextWrapper.checkSelfPermission和Context.checkSelfPermission失效
                //返回值始终为PERMISSION_GRANTED
                //此时必须使用PermissionChecker.checkSelfPermission

                if (PermissionChecker.checkPermission(this, permission, Binder.getCallingPid(), Binder.getCallingUid(), getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }

            } else {

                if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;

                }
            }
        }
    }
}
