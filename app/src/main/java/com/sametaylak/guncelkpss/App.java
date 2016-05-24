package com.sametaylak.guncelkpss;

import android.app.Application;

import com.onesignal.OneSignal;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).init();
    }
}
