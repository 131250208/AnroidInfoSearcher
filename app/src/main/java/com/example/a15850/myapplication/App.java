package com.example.a15850.myapplication;

import android.graphics.drawable.Drawable;

public class App {
    private Drawable appIcon;
    private String appLabel;
    private String pkgName;
    private String versionName;

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public App(String appLabel, Drawable appIcon, String pkgName, String versionName){
        this.appLabel = appLabel;
        this.pkgName = pkgName;
        this.versionName = versionName;
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
