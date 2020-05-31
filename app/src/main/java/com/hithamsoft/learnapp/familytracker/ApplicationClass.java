package com.hithamsoft.learnapp.familytracker;

import android.app.Application;
import android.location.Geocoder;

import com.backendless.Backendless;

import java.util.Locale;

public class ApplicationClass extends Application {
    public static final String APPLICATION_ID = "366D8DFF-373D-F6EA-FF7D-30142206E700";
    public static final String API_KEY = "CE6E7E5C-7B92-4266-AB17-E3FD4CC6C6C3";
    public static final String SERVER_URL = "https://api.backendless.com";


    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(),APPLICATION_ID,API_KEY);

    }
}
