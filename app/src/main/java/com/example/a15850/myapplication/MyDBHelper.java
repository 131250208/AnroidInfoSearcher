package com.example.a15850.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyDBHelper extends SQLiteOpenHelper {

    public static String CREATE_TABLE = "create table cves (" +
            "id integer primary key autoincrement," +
            "cve_num text," +
            "prod_type text," +
            "vendor text," +
            "product text," +
            "version text)";

    private Context mContext = null;
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(CREATE_TABLE);
        import_cves(sqLiteDatabase);
    }

    private void import_cves(SQLiteDatabase sqLiteDatabase){
        AssetManager assetManager = mContext.getAssets();
        StringBuilder strbuilder = new StringBuilder();
        try{
            InputStream inpstr = assetManager.open("cve.json");
            BufferedReader bfr = new BufferedReader(new InputStreamReader(inpstr));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                strbuilder.append(line);
            }
        }
        catch ( IOException e){
            Log.e("MainActivity", "onCreate: readFile fail...");
            Log.e("MainActivity", e.toString());
        }

        String json_str = strbuilder.toString();
        JSONArray ave_array = null;
        try {
            ave_array = new JSONArray(json_str);
            ContentValues ctnval = new ContentValues();
            for(int r = 0;r < ave_array.length(); ++r){
                String cve_num = ave_array.getJSONObject(r).getString("cve_num");
                String prod_type = ave_array.getJSONObject(r).getString("prod_type");
                String vendor = ave_array.getJSONObject(r).getString("vendor");
                String product = ave_array.getJSONObject(r).getString("product");
                String version = ave_array.getJSONObject(r).getString("version");
                ctnval.put("cve_num", cve_num);
                ctnval.put("prod_type", prod_type);
                ctnval.put("vendor", vendor);
                ctnval.put("product", product);
                ctnval.put("version", version);

                sqLiteDatabase.insert("cves", null, ctnval);
                Log.i("DataBase", String.format("insert cve_num: %s, prod_type: %s, vendor: %s, product: %s, version: %s", cve_num
                        ,prod_type, vendor, product, version));
            }
        }
        catch (JSONException e){
            Log.e("MainActivity", "onCreate: loadjson fail...");
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
