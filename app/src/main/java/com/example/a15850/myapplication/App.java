package com.example.a15850.myapplication;

import android.graphics.drawable.Drawable;

public class App{
    private Drawable appIcon;
    private String appLabel;
    private String pkgName;
    private String versionName;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    private int versionCode;

    public App(){

    }
    public App(String appLabel, Drawable appIcon, String pkgName, String versionName, String type, int versionCode){
        this.appLabel = appLabel;
        this.pkgName = pkgName;
        this.versionName = versionName;
        this.appIcon = appIcon;
        this.type = type;
        this.versionCode = versionCode;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }
    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

}
