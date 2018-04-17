package com.example.a15850.myapplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class SystemInfoActivity extends BasicActivity implements View.OnClickListener {
    private Button getRAM, getROM, getSD, getLanguage, getNetwork, getIP_MAC, getWifi;
    private Button getTelephony, getLinuxKernelVersion, getWifiHot, getHard, getSensor;
    private Button getProcess;
    private static Context con;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SystemInfoActivity.class);
        con = context;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);
        //获取RAM
        getRAM = (Button) findViewById(R.id.button_getRAM);
        getRAM.setOnClickListener(this);
        //获取ROM
        getROM = (Button) findViewById(R.id.button_getROM);
        getROM.setOnClickListener(this);
        //获取SD
        getSD = (Button) findViewById(R.id.button_getSD);
        getSD.setOnClickListener(this);
        //获取version
        getLanguage = (Button) findViewById(R.id.button_getLanguage);
        getLanguage.setOnClickListener(this);
        //获取网络连接信息
        getNetwork = (Button) findViewById(R.id.button_getNetwork);
        getNetwork.setOnClickListener(this);
        //获取ip_mac信息
        getIP_MAC = (Button) findViewById(R.id.button_getIP_MAC);
        getIP_MAC.setOnClickListener(this);
        //获取wifi信息
        getWifi = (Button) findViewById(R.id.button_getWIFI);
        getWifi.setOnClickListener(this);
        //获取SIM卡及网络信息
        getTelephony = (Button) findViewById(R.id.button_getTelephony);
        getTelephony.setOnClickListener(this);
        //获取linxu内核版本信息
        getLinuxKernelVersion = (Button) findViewById(R.id.button_getLinuxKernelVersion);
        getLinuxKernelVersion.setOnClickListener(this);
        //获取wifi热点信息
        getWifiHot = (Button) findViewById(R.id.button_getWifiHot);
        getWifiHot.setOnClickListener(this);
        //获取传感器信息
        getSensor = (Button) findViewById(R.id.button_getSensor);
        getSensor.setOnClickListener(this);
        //获取进程信息
        getProcess = (Button) findViewById(R.id.button_getProcess);
        getProcess.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_getRAM:
                getMemoryInfo();
                break;
            case R.id.button_getROM:
                getROMInfo();
                break;
            case R.id.button_getSD:
                getSDInfo();
                break;
            case R.id.button_getLanguage:
                getLanguageInfo();
                break;
            case R.id.button_getNetwork:
                getNetworkInfo();
                break;
            case R.id.button_getIP_MAC:
                getIPMACInfo();
                break;
            case R.id.button_getWIFI:
                getWifiInfo();
                break;
            case R.id.button_getTelephony:
                getTelephonyInfo();
                break;
            case R.id.button_getLinuxKernelVersion:
                getLinuxKernelVersionInfo();
                break;
            case R.id.button_getWifiHot:
                getWifiHotInfo();
                break;
            case R.id.button_getSensor:
                getSensorInfo();
                break;
            case R.id.button_getProcess:
                getProcessInfo();
                break;
            default:
                break;
        }
    }

    //获取内存信息
    public void getMemoryInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("内存（RAM）信息获取");
        String str1 = "/proc/meminfo";
        String str2 = "";
        String str3 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                str3 = str3 + str2 + "\n";
            }
        } catch (IOException e) {
        }
        builder.setMessage(str3);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //获取ROM
    public void getROMInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("ROM信息获取");
        File path = Environment.getDataDirectory();
        StatFs mStatFs = new StatFs(path.getPath());
        long blockSize = mStatFs.getBlockSize();
        long totalBlocks = mStatFs.getBlockCount();
        long r1 = totalBlocks * blockSize;//手机内部空间大小
        long availableBlocks = mStatFs.getAvailableBlocks();
        long r2 = availableBlocks * blockSize;//手机内部可用空间大小
        builder.setMessage("手机内部空间大小：" + formatSize(r1) + "\n" + "手机内部可用空间大小:" + formatSize(r2));
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //获取sd
    public void getSDInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("SD信息获取");
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs mStatFs = new StatFs(path.getPath());
            long blockSize = mStatFs.getBlockSize();
            long totalBlocks = mStatFs.getBlockCount();
            long r1 = totalBlocks * blockSize;//获取手机外部空间大小
            long availableBlocks = mStatFs.getAvailableBlocks();
            long r2 = availableBlocks * blockSize;//获取外部可用空间大小
            builder.setMessage("手机外部空间大小：" + formatSize(r1) + "\n" + "手机外部可用空间大小:" + formatSize(r2));
        } else {
            builder.setMessage("外部存储不可用");
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

    //获取version
    public void getLanguageInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("语言和区域信息获取");
        //获取系统当前使用的语言
        String lan = Locale.getDefault().getLanguage();
        //获取区域
        String country = Locale.getDefault().getCountry();
        builder.setMessage("语言：" + lan + "\n" + "区域：" + country);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //获取网络连接信息
    public void getNetworkInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("网络连接信息获取");
        String r = "";
        /**获得系统级联网管理员对象*/
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {  //无网情况下
            //跳转到网络设置页面
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else {   //有网情况下
            if (info.isAvailable()) { //网络可用时
                /**是手机自带的联网方式*/
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        r = "手机网络可用并已连接" + "\n" + "连接网络方式为:\n" + info.getType() + ",MOBILE";
                    }

                    /**WIFI联网方式*/
                } else {
                    r = "连接网络方式为:" + info.getType() + ",WI-FI";
//                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                }
            } else {
                r = "手机网络不可用";
            }
        }
        builder.setMessage("网路：" + r);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //获取ip和mac地址信息
    public void getIPMACInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("ip和MAC地址获取");
        String ip = "";
        String mac = "";

        WifiManager wifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        ip = formatIpAddress(ipAddress);

        if (wifiInfo.getMacAddress() != null) {
            mac = wifiInfo.getMacAddress();// MAC地址
        } else {
            mac = "null";
        }
        //todo 目前只是实现了获取无线情况下的ip地址
        //ip = getIPAddress(this);

        builder.setMessage("IP地址：" + ip + "\n" + "MAC地址：" + mac);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //获取wifi信息
    public void getWifiInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("wifi信息获取");
        WifiManager mWifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();//获取WiFi信息
        StringBuffer sInfo = new StringBuffer();
        int Ip = info.getIpAddress();
        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
        sInfo.append("\n--BSSID : " + info.getBSSID());//获取BSSID地址。
        sInfo.append("\n--SSID : " + info.getSSID()); // 获取SSID地址。  需要连接网络的ID
        sInfo.append("\n--nIpAddress : " + strIp);//获取IP地址。4字节Int, XXX.XXX.XXX.XXX 每个XXX为一个字节
        sInfo.append("\n--MacAddress : " + info.getMacAddress());//获取MAC地址。
        sInfo.append("\n--NetworkId : " + info.getNetworkId());//获取网络ID。
        sInfo.append("\n--LinkSpeed : " + info.getLinkSpeed() + "Mbps");// 获取连接速度，可以让用户获知这一信息。
        sInfo.append("\n--Rssi : " + info.getRssi());//获取RSSI，RSSI就是接受信号强度指示
        sInfo.append("\n--SupplicantState : " + info.getSupplicantState());
        sInfo.append("\n\n\n\n");

        //搜索到的周边WIFI信号信息
        List<ScanResult> scanResults = mWifiManager.getScanResults();//搜索到的设备列表
        for (ScanResult scanResult : scanResults) {
            sInfo.append("\n设备名：" + scanResult.SSID
                    + " 信号强度：" + scanResult.level + "/n :" + mWifiManager.calculateSignalLevel(scanResult.level, 4));
        }

        builder.setMessage("wifi设备信息和信号信息：" + sInfo.toString());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //SIM卡及网络信息获取
    public void getTelephonyInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("SIM卡及网络信息获取");
        String r = "";
        // 获取系统的TelephonyManager对象
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 获取代表SIM卡状态的数组
        String[] statusNames = getResources().getStringArray(R.array.statusNames);
        // 声明代表手机状态的集合
        ArrayList<String> statusValues = new ArrayList<String>();
        // 获取代表电话网络类型的数组
        String[] phoneType = getResources().getStringArray(R.array.phoneType);
        // 获取代表SIM卡状态的数组
        String[] simState = getResources().getStringArray(R.array.simState);
        // 获取设备编号
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            statusValues.add(tm.getDeviceId());
            // 获取系统平台的版本
            statusValues.add(tm.getDeviceSoftwareVersion() != null ? tm
                    .getDeviceSoftwareVersion() : "未知");
            // 获取网络运营商代号
            statusValues.add(tm.getNetworkOperator());
            // 获取网络运营商名称
            statusValues.add(tm.getNetworkOperatorName());
            // 获取手机网络类型
            statusValues.add(phoneType[tm.getPhoneType()]);
            // 获取设备所在位置
            statusValues.add(tm.getCellLocation() != null ? tm.getCellLocation()
                    .toString() : "未知位置");
            // 获取SIM的国别
            statusValues.add(tm.getSimCountryIso());
            // 获取SIM卡序列号
            statusValues.add(tm.getSimSerialNumber());
            // 获取SIM卡状态
            statusValues.add(simState[tm.getSimState()]);
            // 获取手机IMSI
            statusValues.add(tm.getSubscriberId());
        }
        for (int i = 0; i < statusValues.size(); i++) {
            r += statusNames[i] + statusValues.get(i) + "\n";
        }
        builder.setMessage("SIM卡及网络信息：" + r);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //linux内核版本
    public void getLinuxKernelVersionInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("系统版本");
        String r = Build.VERSION.RELEASE;

        builder.setMessage("系统版本：" + r);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //wifi热点信息
    public void getWifiHotInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("wifi热点信息");
        String r = "";
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder resultList = new StringBuilder();
        for (String ip : connectedIP) {
            resultList.append(ip);
            resultList.append("\n");
        }
        builder.setMessage("链接到当前热点的设备的IP地址：" + resultList);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //传感器信息
    public void getSensorInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("传感器信息");

        //获取传感器管理器
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append(" 传感器类型 - " + item.getType() + "\r\n");
            strLog.append(" 传感器名称 - " + item.getName() + "\r\n");
            strLog.append(" 传感器版本 - " + item.getVersion() + "\r\n");
            strLog.append(" 传感器供应商 - " + item.getVendor() + "\r\n");
            strLog.append(" 最大范围 - " + item.getMaximumRange() + "\r\n");
            strLog.append(" 最小延迟 - " + item.getMinDelay() + "\r\n");
            strLog.append(" Power - " + item.getPower() + "\r\n");
            strLog.append(" 分辨率 - " + item.getResolution() + "\r\n");
            strLog.append("\r\n");
            iIndex++;
        }

        builder.setMessage("传感器列表\n" + strLog.toString());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //进程信息
    public void getProcessInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemInfoActivity.this);
        builder.setTitle("进程信息");
        String r="";
        int index=1;

        // 获得ActivityManager服务的对象
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            r=r+index+" ********\n";
            index++;
            // 进程ID号
            int pid = appProcessInfo.pid;
            r=r+"进程ID: "+pid+"\n";
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            r=r+"用户ID: "+uid+"\n";
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            r=r+"进程名: "+processName+"\n";
            // 获得该进程占用的内存
            int[] myMempid = new int[] { pid };
            // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = mActivityManager
                    .getProcessMemoryInfo(myMempid);
            // 获取进程占内存用信息 kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;
            r=r+"进程所占内存: "+memSize+"kb\n";

            // 获得每个进程里运行的应用程序(包),即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            r=r+"进程的package列表: ";
            for (String pkg : packageList) {
                r=r+pkg+",";
            }
            r=r+"\n";
        }

        builder.setMessage("进程列表\n" + r.toString());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        toast("确定");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //todo
    public String onSensorChanged(int sensor, float[] values) {
        synchronized (this) {
            String r = "";
            String str = "X：" + values[0] + "，Y：" + values[1] + "，Z：" + values[2];
            switch (sensor) {
                case Sensor.TYPE_ACCELEROMETER:
                   r="加速度：" + str;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    r="磁场：" + str;
                    break;
                case Sensor.TYPE_ORIENTATION:
                    r="定位：" + str;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                   r="陀螺仪：" + str;
                    break;
                case Sensor.TYPE_LIGHT:
                    r="光线：" + str;
                    break;
                case Sensor.TYPE_PRESSURE:
                    r="压力：" + str;
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    r="温度：" + str;
                    break;
                case Sensor.TYPE_PROXIMITY:
                    r="距离：" + str;
                    break;
                case Sensor.TYPE_GRAVITY:
                    r="重力：" + str;
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    r="线性加速度：" + str;
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    r="旋转矢量：" + str;
                    break;
                default:
                    r="NORMAL：" + str;
                    break;
            }
            return r;
        }
    }

    private void setEditText(int id, String s) {
        LayoutInflater factory = LayoutInflater.from(SystemInfoActivity.this);
        View layout = factory.inflate(R.layout.dialog, null);
        ((TextView) layout.findViewById(id)).setText("++++++++++++++");
        ((TextView) layout.findViewById(id)).setTextColor(Color.GREEN);
    }
    //获取CPU型号
    public static String getCpuName(){
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            while ((str2=localBufferedReader.readLine()) != null) {
                if (str2.contains("Hardware")) {
                    return str2.split(":")[1];
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return null;

    }

    //获取ip地址
    public String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return "手机网络未打开";
        }
        return null;
    }

    //将得到的int类型的IP转换为String类型
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    //对空间大小有bit变为KB,MB,GB
    public String formatSize(long size) {
        String suffix = "";
        float fSzie = 0;
        if (size >= 1024) {
            suffix = "KB";
            fSzie = size / 1024;
            if (fSzie >= 1024) {
                suffix = "MB";
                fSzie /= 1024;
                if (fSzie >= 1024) {
                    suffix = "GB";
                    fSzie /= 1024;
                }
            }
        }
        DecimalFormat formatter = new DecimalFormat("#0.00");// 字符显示格式
        /* 每3个数字用,分隔，如1,000 */
        formatter.setGroupingSize(3);
        StringBuilder resultBuffer = new StringBuilder(formatter.format(fSzie));
        if (suffix != null) {
            resultBuffer.append(suffix);
        }
        return resultBuffer.toString();
    }

    //判断外部存储是否可用
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    //格式化ip地址
    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }

}
