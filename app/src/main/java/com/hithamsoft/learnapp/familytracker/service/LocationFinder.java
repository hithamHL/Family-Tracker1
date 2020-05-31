package com.hithamsoft.learnapp.familytracker.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.hithamsoft.learnapp.familytracker.R;

import static com.hithamsoft.learnapp.familytracker.utils.ToolsUtils.LOCATION_CODE;

public class LocationFinder extends Service implements LocationListener ,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    //the minimum distance to update is every 10 meter
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final String TAG = "LocationFinder";
    // The minimum time between updates in milliseconds ***every 2 sec will update
    private static final long MIN_TIME_BW_UPDATES = 200 * 10 * 1; // 2 seconds
    protected LocationManager locationManager;
    Context context;
    //check gps available
    boolean isGPSEnable = false;
    //flag for network status
    boolean isNetworkEnable = false;
    //flag for GPS status
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;

    //fuse variable
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds


    @SuppressLint("NewApi")
    public LocationFinder(Context context) {
        this.context = context;

        getLocation();
       // getFuseLocation();

    }

    @SuppressLint("NewApi")
    private Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            //get GPS status
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //get Network status
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d(TAG, "getLocation: check net&gps " + isNetworkEnable + " gps: " + isGPSEnable);


            if (!isGPSEnable && !isNetworkEnable) {
                // no network provider is enabled
                Log.d(TAG, "getLocation:  network and GPS is disable");
            } else {

                this.canGetLocation = true;
                //here i get Location from NetWork
                if (isNetworkEnable) {

                    if (ActivityCompat.checkSelfPermission((Activity)context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_CODE);
                    }else {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);
                        Log.d(TAG, "getLocation: from network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Log.d(TAG, "getLocation: location " + "lat " + latitude + " long: " + longitude);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d(TAG, "getLocation: location not null " + "lat " + latitude + " long: " + longitude);

                            }
                        }
                    }
                    } else if (isGPSEnable) {//get location from GPS //// if GPS Enabled get lat/long using GPS Services
                        if (location == null) {
                            Log.d(TAG, "getLocation: from GPS");
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    this);

                            Log.d(TAG, "getLocation: from GPS");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                Log.d(TAG, "getLocation: lat: " + location.getLatitude() + " long: " + location.getLongitude());
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    Log.d(TAG, "getLocation: location " + "lat " + latitude + " long: " + longitude);
                                }
                            }

                        }

                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private Location getFuseLocation(){
        // we build google api client
//        googleApiClient = new GoogleApiClient.Builder(this).
//                addApi(LocationServices.API).
//                addConnectionCallbacks(this).
//                addOnConnectionFailedListener(this).build();
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                        if (locationResult!=null&& locationResult.getLocations().size()>0){
                          location=  locationResult.getLastLocation();
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d(TAG, "getLocation: location " + "lat " + latitude + " long: " + longitude);
                            }
                        }
                    }

                }, Looper.myLooper());

        return location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //get Latitude
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //get longitude
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingAlert() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.enable_gps_dialog, null);
        AppCompatButton enable_Btn = view.findViewById(R.id.enable_btn);
        AppCompatButton cancel_btn = view.findViewById(R.id.cancel_btn);

        enable_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        alertDialog.show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
