package com.example.a15850.myapplication;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;

public class UserInfoActivity extends BasicActivity {



//    private String[] mListTitle = { "姓名", "性别", "年龄", "居住地","邮箱"};
//    private String[] mListStr = { "雨松MOMO", "男", "25", "北京",
//            "xuanyusong@gmail.com" };
    ListView mListView = null;
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();


    public static void actionStart(Context context){
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mListView = (ListView) findViewById(R.id.list_view_user);

        getAccountInfo(this);
        getLocationInfo(this);
        getPhoneStateInfo();


//        int lengh = mListTitle.length;
//        for(int i =0; i < lengh; i++) {
//            Map<String,Object> item = new HashMap<String,Object>();
//            item.put("title", mListTitle[i]);
//            item.put("text", mListStr[i]);
//            mData.add(item);
//        }


        SimpleAdapter adapter = new SimpleAdapter(this,mData,android.R.layout.simple_list_item_2,
                new String[]{"title","text"},new int[]{android.R.id.text1,android.R.id.text2});
        mListView.setAdapter(adapter);

    }

    public void getAccountInfo(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccounts();

        Map<String,Object> item = new HashMap<String,Object>();
        item.put("title", "账户信息<类型>");

        String account_info = "";

        for (Account account : accounts) {
            // 账号名字
            String accountName = account.name;
            // 账号类型
            String accountType = account.type;

            account_info += account.name;
            account_info += "<";
            account_info += account.type;
            account_info += ">\n";

        }
       // account_info = account_info.substring(0,account_info.length()-2);
        item.put("text", account_info);
        mData.add(item);
    }

    /**
     * 获取用户的当前位置信息
     *
     * @param context Context
     */
    public void getLocationInfo(final Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 设置查询条件（我想要你当前位置的那些信息）
        String bestProvider = lm.getBestProvider(getCriteria(), true);

        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = lm.getLastKnownLocation(bestProvider);
        getLoactionInfo(location);

        // 监听状态
        lm.addGpsStatusListener(new GpsStatus.Listener() {

            public void onGpsStatusChanged(int event) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                switch (event) {
                    // 第一次定位
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.i("LocationInfo", "第一次定位");
                        break;
                    // 卫星状态改变
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.i("LocationInfo", "卫星状态改变");

                        // 获取当前状态
                        GpsStatus gpsStatus = lm.getGpsStatus(null);
                        // 获取卫星颗数的默认最大值
                        int maxSatellites = gpsStatus.getMaxSatellites();
                        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                        int count = 0;
                        while (iters.hasNext() && count <= maxSatellites) {
                            count++;
                        }
                        Log.i("LocationInfo", "搜索到：" + count + "颗卫星");
                        break;
                    // 定位启动
                    case GpsStatus.GPS_EVENT_STARTED:
                        Log.i("LocationInfo", "定位启动");
                        break;
                    // 定位结束
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.i("LocationInfo", "定位结束");
                        break;
                }
            }

            ;
        });

        // 默认采用网络连接定位，若GPS可用则使用GPS定位
        String provider = LocationManager.NETWORK_PROVIDER;
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }

        /**
         * 参数1：设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
         * 参数2：位置信息更新周期，单位毫秒
         * 参数3：位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
         * 参数4：位置变化监听器
         *
         * 注：如果参数3不为0，则以参数3为准；参数3为0，则通过时间（参数2）来定时更新；两者为0，则随时刷新
         * 3分钟更新一次或者最小位移变化超过10米更新一次
         */
        lm.requestLocationUpdates(provider, 3 * 60 * 1000, 10, new LocationListener() {

            /**
             * 位置信息变化时触发
             */
            public void onLocationChanged(Location location) {
                getLoactionInfo(location);
            }

            /**
             * GPS状态变化时触发
             */
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    // GPS状态为可见时
                    case LocationProvider.AVAILABLE:
                        Log.i("LocationInfo", "当前GPS状态为可见状态");
                        break;
                    // GPS状态为服务区外时
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.i("LocationInfo", "当前GPS状态为服务区外状态");
                        break;
                    // GPS状态为暂停服务时
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.i("LocationInfo", "当前GPS状态为暂停服务状态");
                        break;
                }
            }

            /**
             * GPS开启时触发
             */
            public void onProviderEnabled(String provider) {

                Log.i("LocationInfo", "GPS开启");

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }



                Location location = lm.getLastKnownLocation(provider);
                getLoactionInfo(location);
            }

            /**
             * GPS禁用时触发
             */
            public void onProviderDisabled(String provider) {

            }
        });
    }

    private void getLoactionInfo(Location location) {

        long time = 0;
        double longitude = 0;
        double latitude = 0;
        double altitude = 0;


        if(location !=null)
        {
            Log.i("LocationInfo", "时间：" + location.getTime());
            Log.i("LocationInfo", "经度：" + location.getLongitude());
            Log.i("LocationInfo", "纬度：" + location.getLatitude());
            Log.i("LocationInfo", "海拔：" + location.getAltitude());
            time = location.getTime();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            altitude = location.getAltitude();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= new Date(time);
        String time_string = sdf.format(date);

        Map<String,Object> item = new HashMap<String,Object>();
        item.put("title", "用户地理位置信息");

        String location_info = "";

        location_info += "时间：";
        location_info += time_string;
        location_info += "\n";
        location_info += "经度：";
        location_info += longitude;
        location_info += "\n";
        location_info += "纬度：";
        location_info += latitude;
        location_info += "\n";
        location_info += "海拔：";
        location_info += altitude;

        item.put("text", location_info);

        if(mData.size()<2)
        {
            mData.add(item);
        }
        else
        {
            mData.set(1,item);
        }

    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    void getPhoneStateInfo()
    {
        Map<String,Object> item = new HashMap<String,Object>();
        item.put("title", "手机状态信息");
        item.put("text", "");
        String phone_state_info = "";



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(UserInfoActivity.this, "需要读取手机状态的权限",Toast.LENGTH_LONG).show();
            mData.add(item);
            return;
        }

        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

        /*
        * 电话状态：
         *1.tm.CALL_STATE_IDLE=0     无活动
        * 2.tm.CALL_STATE_RINGING=1  响铃
        * 3.tm.CALL_STATE_OFFHOOK=2  摘机
        */
        phone_state_info += "电话状态(无活动/响铃/通话中):";
        switch (tm.getCallState())
        {
            case TelephonyManager.CALL_STATE_IDLE:
                phone_state_info += "无活动";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                phone_state_info += "响铃";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                phone_state_info += "通话中";
                break;
            default:
        }
        phone_state_info += "\n";

         /*
         * 电话方位：
         */
        phone_state_info += "电话方位:";
        phone_state_info += tm.getCellLocation();//CellLocation
        phone_state_info += "\n";


        /*
        * 唯一的设备ID：
        * GSM手机的 IMEI 和 CDMA手机的 MEID.
        * Return null if device ID is not available.
        */
//        String dev_id= tm.getDeviceId();//String
//        phone_state_info += "唯一的设备ID:";
//        if (dev_id!=null)
//        {
//            phone_state_info += dev_id;
//        }
//        phone_state_info += "\n";

        /*
        *设备的软件版本号：
        * 例如：the IMEI/SV(software version) for GSM phones.
        * Return null if the software version is not available.
        */
        String soft_version= tm.getDeviceSoftwareVersion();//String
        phone_state_info += "设备的软件版本号:";
        if (soft_version!=null)
        {
            phone_state_info += soft_version;
        }
        phone_state_info += "\n";

        /** 手机号：
         * GSM手机的 MSISDN.
         * * Return null if it is unavailable.
         */
        String line_number= tm.getLine1Number();//String
        phone_state_info += "手机号:";
        if (line_number!=null)
        {
            phone_state_info += line_number;
        }
        phone_state_info += "\n";

        /*
        * 附近的电话的信息:
        * 类型：List<NeighboringCellInfo>
        * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
        */
        List<NeighboringCellInfo> nei_scells = tm.getNeighboringCellInfo();//List<NeighboringCellInfo>

        phone_state_info += "附近的电话的信息:";
        for (NeighboringCellInfo nei_scell : nei_scells) {
            phone_state_info += nei_scell;
            phone_state_info += "\n";
        }
        phone_state_info += "\n";

        /*
        * 获取ISO标准的国家码，即国际长途区号。
        * 注意：仅当用户已在网络注册后有效。
        * 在CDMA网络中结果也许不可靠。
        */
//        String net_country_iso = tm.getNetworkCountryIso();//String
//        phone_state_info += "国际长途区号:";
//        if (net_country_iso!=null)
//        {
//            phone_state_info += net_country_iso;
//        }
//        phone_state_info += "\n";

        /*
        * 当前使用的网络类型：
        * 例如： NETWORK_TYPE_UNKNOWN  网络类型未知  0
        NETWORK_TYPE_GPRS     GPRS网络  1
        NETWORK_TYPE_EDGE     EDGE网络  2
        NETWORK_TYPE_UMTS     UMTS网络  3
        NETWORK_TYPE_HSDPA    HSDPA网络  8
        NETWORK_TYPE_HSUPA    HSUPA网络  9
        NETWORK_TYPE_HSPA     HSPA网络  10
        NETWORK_TYPE_CDMA     CDMA网络,IS95A 或 IS95B.  4
        NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5 NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6
        NETWORK_TYPE_1xRTT    1xRTT网络  7
        */
        phone_state_info += "当前使用的网络类型:";
        switch (tm.getNetworkType())
        {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                phone_state_info += "网络类型未知";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                phone_state_info += "GPRS网络";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                phone_state_info += "EDGE网络";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                phone_state_info += "UMTS网络";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                phone_state_info += "HSDPA网络";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                phone_state_info += "HSUPA网络";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                phone_state_info += "HSPA网络";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                phone_state_info += "CDMA网络";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                phone_state_info += "EVDO网络";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                phone_state_info += "1xRTT网络";
                break;
            default:
        }
        phone_state_info += "\n";

        /*
        * 当前信号：
        * 例如： PHONE_TYPE_NONE  无信号
        PHONE_TYPE_GSM   GSM信号
        PHONE_TYPE_CDMA  CDMA信号
        */
        phone_state_info += "当前信号:";
        switch (tm.getPhoneType())
        {
            case TelephonyManager.PHONE_TYPE_NONE:
                phone_state_info += "无信号";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phone_state_info += "GSM信号";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                phone_state_info += "CDMA信号";
                break;
            default:
        }
        phone_state_info += "\n";

        /*
        * Returns the ISO country code equivalent for the SIM provider's country code.
            * 获取ISO国家码，相当于提供SIM卡的国家码。
        */
//        String sim_country_iso = tm.getSimCountryIso();//String
//        phone_state_info += "提供SIM卡的国家码:";
//        if (sim_country_iso!=null)
//        {
//            phone_state_info += sim_country_iso;
//        }
//        phone_state_info += "\n";



        /*
        * 取得和语音邮件相关的标签，即为识别符
         * 需要权限：READ_PHONE_STATE
            */
        String voice_mail_tag = tm.getVoiceMailAlphaTag();//String
        phone_state_info += "语音邮件相关的标签:";
        if (voice_mail_tag!=null)
        {
            phone_state_info += voice_mail_tag;
        }
        phone_state_info += "\n";

        /*
        * 获取语音邮件号码：
        * 需要权限：READ_PHONE_STATE
         */
        String voice_mail_num = tm.getVoiceMailNumber();//String
        phone_state_info += "语音邮件号码:";
        if (voice_mail_num!=null)
        {
            phone_state_info += voice_mail_num;
        }
        phone_state_info += "\n";

        /** ICC卡是否存在*/
        phone_state_info += "ICC卡是否存在:";
        if(tm.hasIccCard())
        {
            phone_state_info += "是";
        }
        else
        {
            phone_state_info += "否";
        }
        phone_state_info += "\n";

        item.put("text", phone_state_info);
        mData.add(item);
    }

}
