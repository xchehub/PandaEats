package com.erranddaddy.pandaeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.erranddaddy.pandaeats.adapters.PlaceYourOrderAdapter;
import com.erranddaddy.pandaeats.model.Menu;
import com.erranddaddy.pandaeats.model.RestaurantModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PlaceYourOrderActivity extends AppCompatActivity {

    private EditText inputName, inputAddress, inputCity, inputState, inputZip, inputCardNumber, inputCardExpiry, inputCardPin;
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, tvDeliveryChargeAmount, tvDeliveryCharge, tvTotalAmount, buttonPlaceYourOrder, buttonAutoFill;
    private SwitchCompat switchDelivery;
    private boolean isDeliveryOn;
    private PlaceYourOrderAdapter placeYourOrderAdapter;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        inputName = findViewById(R.id.inputName);
        inputAddress = findViewById(R.id.inputAddress);
        inputCity = findViewById(R.id.inputCity);
        inputState = findViewById(R.id.inputState);
        inputZip = findViewById(R.id.inputZip);
        inputCardNumber = findViewById(R.id.inputCardNumber);
        inputCardExpiry = findViewById(R.id.inputCardExpiry);
        inputCardPin = findViewById(R.id.inputCardPin);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        buttonPlaceYourOrder = findViewById(R.id.buttonPlaceYourOrder);
        buttonAutoFill = findViewById(R.id.tvAutoFill);
        switchDelivery = findViewById(R.id.switchDelivery);

        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        buttonPlaceYourOrder.setOnClickListener(v -> onPlaceOrderButtonClick(restaurantModel));
        buttonAutoFill.setOnClickListener(v -> onAutoFillButtonClick(restaurantModel));
        switchDelivery.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                inputAddress.setVisibility(View.VISIBLE);
                inputCity.setVisibility(View.VISIBLE);
                inputState.setVisibility(View.VISIBLE);
                inputZip.setVisibility(View.VISIBLE);
                tvDeliveryChargeAmount.setVisibility(View.VISIBLE);
                tvDeliveryCharge.setVisibility(View.VISIBLE);
                isDeliveryOn = true;
                calculateTotalAmount(restaurantModel);
                buttonAutoFill.setVisibility(View.VISIBLE);
            } else {
                inputAddress.setVisibility(View.GONE);
                inputCity.setVisibility(View.GONE);
                inputState.setVisibility(View.GONE);
                inputZip.setVisibility(View.GONE);
                tvDeliveryChargeAmount.setVisibility(View.GONE);
                tvDeliveryCharge.setVisibility(View.GONE);
                isDeliveryOn = false;
                calculateTotalAmount(restaurantModel);
                buttonAutoFill.setVisibility(View.GONE);
            }
        });
        initRecyclerView(restaurantModel);
        calculateTotalAmount(restaurantModel);
    }

    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float subTotalAmount = 0f;

        for (Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText(String.format("$ %.2f", subTotalAmount));
        if (isDeliveryOn) {
            tvDeliveryChargeAmount.setText(String.format("$ %.2f", restaurantModel.getDelivery_charge()));
            subTotalAmount += restaurantModel.getDelivery_charge();
        }
        tvTotalAmount.setText(String.format("$ %.2f", subTotalAmount));
    }

    private void onPlaceOrderButtonClick(RestaurantModel restaurantModel) {
        if (TextUtils.isEmpty(inputName.getText().toString())) {
            inputName.setError("Please enter name ");
            return;
        } else if (isDeliveryOn && TextUtils.isEmpty(inputAddress.getText().toString())) {
            inputAddress.setError("Please enter address ");
            return;
        } else if (isDeliveryOn && TextUtils.isEmpty(inputCity.getText().toString())) {
            inputCity.setError("Please enter city ");
            return;
        } else if (isDeliveryOn && TextUtils.isEmpty(inputState.getText().toString())) {
            inputState.setError("Please enter state ");
            return;
        } else if (isDeliveryOn && TextUtils.isEmpty(inputZip.getText().toString())) {
            inputZip.setError("Please enter Zip ");
            return;
        } else if (TextUtils.isEmpty(inputCardNumber.getText().toString())) {
            inputCardNumber.setError("Please enter card number ");
            return;
        } else if (TextUtils.isEmpty(inputCardExpiry.getText().toString())) {
            inputCardExpiry.setError("Please enter card expiry ");
            return;
        } else if (TextUtils.isEmpty(inputCardPin.getText().toString())) {
            inputCardPin.setError("Please enter card pin/cvv ");
            return;
        }
        //start success activity..
        Intent i = new Intent(PlaceYourOrderActivity.this, OrderSuccessActivity.class);
        i.putExtra("RestaurantModel", restaurantModel);
        startActivityForResult(i, 1000);
    }

    private void onAutoFillButtonClick(RestaurantModel restaurantModel) {
        getLastLocation();
    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location == null) {
                location.set(newLocationData());
            } else {
                Log.d("Debug:" ,"Device Location Longitude :"+ location.getLongitude());
                Log.d("Debug:" ,"Device Location Latitude:"+ location.getLatitude());
            }

            // fill in
            if (location != null) {
                setAddressInfo(location.getLatitude(), location.getLongitude());
            }
        });

//        LocationManager locationManager = (LocationManager)
//                getApplicationContext().getSystemService(LOCATION_SERVICE);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, );
//        if (location != null) {
//                setAddressInfo(location.getLatitude(), location.getLongitude());
//        }

//        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getApplicationContext());
//        fusedLocationProviderClient.getLastLocation()
//                .addOnSuccessListener(location -> {
//                    if (location != null) {
//                        setAddressInfo(location.getLatitude(), location.getLongitude());
//                    }
//                });
    }

    private void setAddressInfo(double latitude, double longitude) {

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);

            inputAddress.setText(address.get(0).getAddressLine(0));
            inputCity.setText(address.get(0).getAdminArea());
            inputState.setText(address.get(0).getCountryName());
            inputZip.setText(address.get(0).getPostalCode());

            buttonAutoFill.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private Location newLocationData() {
        final Location[] newLocation = {null};
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                Log.d("Debug:","new Device Location Longitude: "+ lastLocation.getLongitude());
                Log.d("Debug:","new Device Location Latitude: "+ lastLocation.getLatitude());
                newLocation[0] = lastLocation;
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        return newLocation[0];
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }


}