package com.hithamsoft.learnapp.familytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoCategory;
import com.hithamsoft.learnapp.familytracker.adapter.FamilyAdapter;
import com.hithamsoft.learnapp.familytracker.databinding.ActivityMainBinding;
import com.hithamsoft.learnapp.familytracker.model.Family;
import com.hithamsoft.learnapp.familytracker.service.LocationFinder;
import com.hithamsoft.learnapp.familytracker.utils.ToolsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding activityMainBinding;
    FamilyAdapter familyAdapter;
    List<Family> familyList;
    String[] permissionName = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.recyclerViewFamily.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        activityMainBinding.recyclerViewFamily.setHasFixedSize(true);
        //add categore to backendless
        joinBackendless();

        familyList = new ArrayList<>();
        addFamileMember();
        familyAdapter = new FamilyAdapter(MainActivity.this, familyList, new FamilyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToolsUtils.checkLocationPermission(MainActivity.this,
                        permissionName, familyList.get(position).getPersonName());

            }
        });
        activityMainBinding.recyclerViewFamily.setAdapter(familyAdapter);


    }

    private void joinBackendless(){
        //category name must be a-z, A-Z,0-9,_
        Backendless.Geo.addCategory("family", new AsyncCallback<GeoCategory>() {
            @Override
            public void handleResponse(GeoCategory response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        try {
            userAddres();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addFamileMember() {
        familyList.add(new Family("Family", R.drawable.ic_all_family));
        familyList.add(new Family("Hitham", R.drawable.ic_person));
        familyList.add(new Family("Mohammed", R.drawable.ic_person));
        familyList.add(new Family("Hesham", R.drawable.ic_person));
        familyList.add(new Family("Mona", R.drawable.ic_person));
    }

   private void userAddres() throws IOException {
       String  userCountry;
       LocationFinder finder;
       double longitude = 0.0, latitude = 0.0;
       finder = new LocationFinder(MainActivity.this);
       if (finder.canGetLocation()) {
           Log.d(TAG, "userAddres: can get Location");
           latitude = finder.getLatitude();
           longitude = finder.getLongitude();

           activityMainBinding.addressTxt.setText(latitude+"--"+longitude);
           Log.d(TAG, "userAddres: lat: "+latitude +" long: "+longitude);
           try {

               Geocoder geocoder = new Geocoder(this, Locale.getDefault());
               List<Address> addresses = geocoder.getFromLocation(-91.7442717, 37.9539294, 1);
               Log.d(TAG, "userAddres: address is: "+addresses.get(0));
               if (addresses != null && addresses.size() > 0) {
                   userCountry = addresses.get(0).getCountryName();
                  String userAddress = addresses.get(0).getAddressLine(0);
//                   activityMainBinding.addressTxt.setText(userCountry + ", " + userAddress);
               }
               else {
                   userCountry = "Unknown";
//                   activityMainBinding.addressTxt.setText(userCountry);
               }

           } catch (Exception e) {
               e.printStackTrace();
           }
       } else {
           finder.showSettingAlert();
       }
   }

}
