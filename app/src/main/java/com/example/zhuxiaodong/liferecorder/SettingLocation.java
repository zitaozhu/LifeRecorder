package com.example.zhuxiaodong.liferecorder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Assign the current user's location to the note
 * or use the recommended one
 */

public class SettingLocation extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 555;
    private double latitude;
    private double longitude;
    private LocationListener mLocationListener;
    private LocationManager locationManager;
    private String key;
    private TextView recommendView;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_location);

        final TextView locationView  = (TextView) findViewById(R.id.location_suggestion_view);
        final Button setLocationButton = (Button) findViewById(R.id.set_location_button);
        recommendView  = (TextView) findViewById(R.id.recommend_location_list_view);

        final Intent intent = getIntent();
        key = intent.getStringExtra("key");

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //TODO
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
        };

        getLanglat();
        locationView.setText(getAddress(latitude, longitude));
        addRecmmendLocation();

        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceeceLocation(locationView.getText().toString());
            }
        });

        recommendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceeceLocation(recommendView.getText().toString());
            }
        });

    }

    private void proceeceLocation(String location) {
        final String address = location;
        final DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(currentUser.getUid()).child("Note");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference ref = dataSnapshot.child(key).getRef();
                ref.child("location").setValue(address);
                Toast.makeText(getApplicationContext(),
                        "location saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        addLocationPage(address);
    }

    private void addRecmmendLocation() {
        final DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("location");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    int index = (int)(Math.random() * (double)dataSnapshot.getChildrenCount());
                    int count = 0;
                    for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        if(count == index) {
                            recommendView.setText(messageSnapshot.
                                    child("location").getValue(String.class));
                            return;
                        }
                        count++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void addLocationPage(String text) {
        final String address = text;
        final DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("location");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        if(messageSnapshot.child("location").getValue() != null
                                && messageSnapshot.child("location").getValue().toString().equals(address)) {
                            finish();
                        }
                    }
                }
                Toast.makeText(getApplicationContext(),
                        "location saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        createNewLocation(dbRef, address);
    }

    private void createNewLocation(DatabaseReference dbRef, String address) {
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("location");
        //tempRef.child("Name").setValue(currentUser.getUid());
        tempRef.push().child("location").setValue(address);
        finish();
    }

    protected void getLanglat() {
        Context mContext = SettingLocation.this;
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no GPS Provider and no network provider is enabled
        } else {   // Either GPS provider or network provider is enabled

            // First get location from Network Provider
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(SettingLocation.this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,mLocationListener);

                if (locationManager != null) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }// End of IF network enabled
                else if (isGPSEnabled) {
                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) mLocationListener);
                    if (locationManager != null) {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        }// End of Either GPS provider or network provider is enabled
    }

    /**
     * returns the string of location by lattitude and longtitude
     * @param latitude the double form of lattitude
     * @param longitude the double form of longtitude
     * @return the string of address.
     * //source http://blog.sina.com.cn/s/blog_6c762bb301014i9f.html
     */
    public String getAddress(double latitude, double longitude){
        String strAddress = "";
        Geocoder geocoder = new Geocoder(this);
        List places = null;
        try {
            places = geocoder.getFromLocation(latitude, longitude, 5);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(places != null && places.size() > 0) {
            strAddress = ((Address)places.get(0)).getAddressLine(0);
            // + "," + ((Address)places.get(0)).getAddressLine(1); //+ ","
            //+ ((Address)places.get(0)).getAddressLine(2);
        }
        return strAddress;
    }

    //give the permission of gps and internet location
    //if successful then call the get lat and long again,
    //if not then pop up a toast.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLanglat();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    longitude = 0.0;
                    latitude = 0.0;
                    Toast.makeText(SettingLocation.this, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

