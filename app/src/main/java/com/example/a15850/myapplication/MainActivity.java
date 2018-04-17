package com.example.a15850.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;


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


        Button bt_usr_info = (Button)findViewById(R.id.button_users_info);
        bt_usr_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,Build.VERSION.RELEASE, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://4636")));
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {//Android 6.0以上版本需要获取权限
//                    String[] perms = {Manifest.permission.CALL_PHONE,};
//                    requestPermissions(perms,200);//请求权限
//                } else {
//                    callPhone();
//                }
            }
        });

//        PackageManager mPm = getPackageManager();
//        List<PackageInfo> appList=mPm.getInstalledPackages(PackageManager.GET_PERMISSIONS|PackageManager.GET_RECEIVERS|
//                PackageManager.GET_SERVICES|PackageManager.GET_PROVIDERS);
//
//        for (PackageInfo pi : appList) {
//            System.out.println("Process Name: "+pi);
//            // Do not add System Packages
//            if ((pi.requestedPermissions == null || pi.packageName.equals("android")) ||
//                    (pi.applicationInfo != null && (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0))
//                continue;
//
//            for (String permission : pi.requestedPermissions) {
//                //Map<String, String> curChildMap = new HashMap<String, String>();
//                //System.out.println("############     "+permission);
//
//                try {
//                    PermissionInfo pinfo = mPm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
//                    CharSequence label = pinfo.loadLabel(mPm);
//                    CharSequence desc = pinfo.loadDescription(mPm);
//                    System.out.println("$$$$$ "+label+"!!!!!! "+desc);
//
//                } catch (NameNotFoundException e) {
//                    Log.i("", "Ignoring unknown permission " + permission);
//                    continue;
//                }
//            }
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
