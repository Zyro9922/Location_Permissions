package com.example.alihasan.locationtest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button getLocation, next;

    TextView lat, lng;

    String slongi, slati;

    public static final int LOCATION_REQ_CODE = 100;
    LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    ProgressBar progressBar;
    Switch switchCamera,switchLocation,switchStorage;
    private int REQUEST_STRING_CODE=1234;
    public static final String TAG = "MA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
progressBar=findViewById(R.id.progressBar);
switchCamera=findViewById(R.id.switchCamera);
        switchLocation=findViewById(R.id.switchLocation);
        switchStorage=findViewById(R.id.switchStorage);
        getLocation = findViewById(R.id.locationButton);
        next = findViewById(R.id.nextButton);
        lat = findViewById(R.id.lat);
        lng = findViewById(R.id.lng);
        String []permissionsList={Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this,
                                            permissionsList,
                                    REQUEST_STRING_CODE);
        //Switch adjusted here
        permissionCheckedToShow();



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if (isLocationServicesAvailable(MainActivity.this)) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
//                    dialog.dismiss();
                } else {
                    Log.d("THIS", "HERE 2");
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(MainActivity.this);
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final String message = "Enable either GPS or any other location"
                            + " service to find current location.  Click OK to go to"
                            + " location services settings to let you do so.";
                    builder.setTitle("Enable Location");

                    builder.setMessage(message)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            startActivity(new Intent(action));
                                            d.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            d.cancel();
                                        }
                                    }).show();
                }

            }
        });

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = addresses.get(0);
                String useradd = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    useradd = useradd + address.getAddressLine(i).toString() + "\n";
                useradd = useradd + (address.getCountryName().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

//            dialog.dismiss();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            if (latitude != 0 && longitude != 0) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onLocationChanged: " + longitude + "  " + latitude);

                lat.setText("" + location.getLatitude());
                lng.setText("" + location.getLongitude());

//                dialog.dismiss();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
//            dialog.dismiss();
        }

        @Override
        public void onProviderDisabled(String provider) {
//            dialog.dismiss();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQ_CODE) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    dialog.setMessage("Getting Coordinates");
//                    dialog.show();
                    //noinspection MissingPermission

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//                    dialog = new ProgressDialog(MainActivity.this);
                }
            }
        }
        else if (requestCode==REQUEST_STRING_CODE)
        {
           permissionCheckedToShow();

        }
    }

    public static boolean isLocationServicesAvailable(Context context) {
        int locationMode = 0;
        String locationProviders;
        boolean isAvailable = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        boolean coarsePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean finePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return isAvailable && (coarsePermissionCheck || finePermissionCheck);
    }
    public void permissionCheckedToShow(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)
            switchCamera.setChecked(true);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            switchStorage.setChecked(true);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            switchLocation.setChecked(true);

    }
}
