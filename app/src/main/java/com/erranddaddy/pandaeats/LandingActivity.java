package com.erranddaddy.pandaeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class LandingActivity extends AppCompatActivity {
    private final int COARSE_PERMISSION_ID = 1000;
    private final int FINE_PERMISSION_ID = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(() -> {
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_PERMISSION_ID);
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_PERMISSION_ID);
            isLocationEnabled();
            startActivity(new Intent(LandingActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == COARSE_PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Coarse location Permission Granted", Toast.LENGTH_SHORT) .show();
            }else {
                Toast.makeText(this, "Coarse location Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        if (requestCode == FINE_PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fine location Permission Granted", Toast.LENGTH_SHORT) .show();
            }else {
                Toast.makeText(this, "Fine location Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    public void checkPermission (String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
    }

    private void isLocationEnabled() {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Location service is not enabled", Toast.LENGTH_SHORT).show();
        }
    }
}