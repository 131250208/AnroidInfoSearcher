package com.example.a15850.myapplication;

import android.app.Application;

import java.util.ArrayList;

public class DataContainer extends Application {
    private ArrayList<App> apps;
    public ArrayList<App> getApps() {
        return apps;
    }

    public void setApps(ArrayList<App> apps) {
        this.apps = apps;
    }
}
