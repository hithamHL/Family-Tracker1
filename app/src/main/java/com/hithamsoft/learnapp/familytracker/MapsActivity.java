package com.hithamsoft.learnapp.familytracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "MapsActivity";
    //map variable
    LocationManager locationManager;
    double lat = 31.000055, lng = 29.595689;
    boolean isExistingPoint = false;
    GeoPoint existingPoint;
    List<GeoPoint> allPoint;
    private GoogleMap mMap;
    private FloatingActionButton fabLocation_btn;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fabLocation_btn = findViewById(R.id.fab_location);

        //check intent type
        checkType();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initMapLocation();
        //send location of user to server
        sendLocation();
    }

    private void initMapLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        } else {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            }
        }

    }

    private void sendLocation() {
        fabLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, " send Location..", Toast.LENGTH_SHORT).show();
                if (!isExistingPoint) {
                    sendPoint();
                } else {

                    Backendless.Geo.removePoint(existingPoint, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                            sendPoint();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(MapsActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        if (mMap != null) {
            LatLng positon = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person_marker))
                    .anchor(0.5f, 0.5f)
                    .title("last loc")
                    .position(positon));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(positon));

        }
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

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        } else {
            locationManager.requestLocationUpdates(provider, 10000, 10, this);
        }

    }

    private void sendPoint() {
        List<String> categories = new ArrayList<>();
        categories.add("family");
        //send data to backend server(Backendless)
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", getIntent().getStringExtra("TYPE"));
        metadata.put("update", new Date().toString());
        Backendless.Geo.savePoint(lat, lng, categories, metadata, new AsyncCallback<GeoPoint>() {
            @Override
            public void handleResponse(GeoPoint response) {
                Toast.makeText(MapsActivity.this, "Location Send", Toast.LENGTH_SHORT).show();
                isExistingPoint = true;
                existingPoint = response;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MapsActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkType() {
        String type = getIntent().getStringExtra("TYPE");
        Log.d(TAG, "checkType: userType: "+type);
        if (type.equals("Family")) {
            fabLocation_btn.hide();
            BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
            geoQuery.addCategory("family");
            geoQuery.setIncludeMeta(true);
            Backendless.Geo.getPoints(geoQuery, new AsyncCallback<List<GeoPoint>>() {
                @Override
                public void handleResponse(List<GeoPoint> response) {
                    allPoint = response;
                    if (allPoint.size() != 0) {
                        for (int i = 0; i < allPoint.size(); i++) {
                            LatLng positionMarker = new LatLng(allPoint.get(i).getLatitude(), allPoint.get(i).getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(positionMarker)
                                    .snippet(allPoint.get(i).getMetadata("update").toString())
                                    .title(allPoint.get(i).getMetadata("name").toString()));
                            Log.d(TAG, "handleResponse: positon data: "+allPoint.get(i).getMetadata("name").toString()+"\n");
                        }
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(MapsActivity.this, "ERROr: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fabLocation_btn.hide();
            BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
            geoQuery.addCategory("family");
            geoQuery.setIncludeMeta(true);
            Backendless.Geo.getPoints(geoQuery, new AsyncCallback<List<GeoPoint>>() {
                @Override
                public void handleResponse(List<GeoPoint> response) {
                    allPoint = response;
                    if (allPoint.size() != 0) {
                        for (int i = 0; i < allPoint.size(); i++) {
                            if (allPoint.get(i).getMetadata("name").equals(getIntent().getStringExtra("TYPE"))) {
                                isExistingPoint = true;
                                existingPoint = allPoint.get(i);
                                break;
                            }
                        }
                    }
                    fabLocation_btn.show();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(MapsActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
