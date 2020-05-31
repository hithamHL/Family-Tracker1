package com.hithamsoft.learnapp.familytracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hithamsoft.learnapp.familytracker.MainActivity;
import com.hithamsoft.learnapp.familytracker.MapsActivity;

public class ToolsUtils {
    public static int LOCATION_CODE=100;


    public static void checkLocationPermission(Activity context, String[] permissionName,String familyName){
        if (ContextCompat.checkSelfPermission(context,permissionName[0])!= PackageManager.PERMISSION_GRANTED
        ||ContextCompat.checkSelfPermission(context,permissionName[1])!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context,permissionName,LOCATION_CODE);
        }else {
            Intent intent=new Intent(context, MapsActivity.class);
            intent.putExtra("TYPE",familyName);
            context.startActivity(intent);
        }
    }
}
