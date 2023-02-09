package com.codinglegend.legendsp.cabme.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.ActivityShareCabBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ShareCabActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener{

    private ActivityShareCabBinding binding;
    private Context context;

    RadioGroup availableSpaceRG, typeRG, acNoneAcRG, vehicalTypeRG, timesOfTripRG;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5;

    String startingAddress = "", endingAddress = "", startingTime = "", endingTime = "";
    String cabName = "", cabModel = "", cabDriverMo = "", cabVehicalNum = "";
    String availableSpace = "", cabType = "", cabAcNoneAc = "", vehicalType = "", timesOfTrip = "";
    String paymentAmount = "";
    String uniqueCabId;

    Boolean stTimeBoll = false, endTimeBool = false;

    private DatePickerDialog dpd;
    private TimePickerDialog tpd;

    String dateTime;

    String addCabApi = common.getBaseUrl() + "addCabToShare.php";
    String updateLocation = common.getBaseUrl() + "CabLocation.php";
    String updateCabDetails = common.getBaseUrl() + "UpdateCab.php";
    String fetchCabDetailsApi = common.getBaseUrl() + "FetchCabDetails.php";
    String addLocationApi = common.getBaseUrl() + "AddLocation.php";

    Intent intent;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private GoogleMap map;
    private static final int DEFAULT_ZOOM = 15;
    private boolean locationPermissionGranted = true;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    Double Latitude, Longitude;
    Boolean cameraCheck = true;
    Marker marker;

    int COUNTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_cab);
        context = ShareCabActivity.this;

        availableSpaceRG = findViewById(R.id.availableSpaceRG);
        typeRG = findViewById(R.id.typeRG);
        acNoneAcRG = findViewById(R.id.acNoneAcRG);
        vehicalTypeRG = findViewById(R.id.vehical_type);
        timesOfTripRG = findViewById(R.id.timesOfTripRG);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        intent = getIntent();
        if (intent.getStringExtra("cabId") != null){
            uniqueCabId = intent.getStringExtra("cabId");
            binding.doneBtn.setText("done");
            common.showProgressDialog(context, "Please wait");
            getCabDetails();
        }else if (intent.getStringExtra("from") != null){

            if (intent.getStringExtra("from").equalsIgnoreCase("shareLocation")){

                binding.doneBtn.setText("Share Location");
                binding.cabDetials.setVisibility(View.GONE);
                binding.startingTIme.setVisibility(View.GONE);
                binding.endingTIme.setVisibility(View.GONE);

            }

        }

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (binding.doneBtn.getText().toString().equalsIgnoreCase("Offer Lift")){
                    getData();
                }else if (binding.doneBtn.getText().toString().equalsIgnoreCase("Share Location")){
                    getData2();
                }
                
            }
        });

        binding.selectStartingTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stTimeBoll = true;
                endTimeBool = false;

                selectStartintTime();
            }
        });

        binding.selectEndingTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stTimeBoll = false;
                endTimeBool = true;

                selectStartintTime();
            }
        });

    }

    private void getData2() {
        
        startingAddress = binding.edStartingAddress.getText().toString().trim();
        endingAddress = binding.endingAddress.getText().toString().trim();
        
        if (startingAddress.length() < 3){
            Toast.makeText(context, "enter proper starting address", Toast.LENGTH_SHORT).show();
        }else if (endingAddress.length() < 3){
            Toast.makeText(context, "enter proper ending address", Toast.LENGTH_SHORT).show();
        }else {
            requestLocation1();
        }
        
    }

    private void requestLocation1() {

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {

                                if (cameraCheck) {

                                }else {
                                    marker.remove();
                                }

                                Latitude = lastKnownLocation.getLatitude();
                                Longitude = lastKnownLocation.getLongitude();

                                if (Latitude != null && Longitude != null){
                                    updateLocation1(String.valueOf(Latitude), String.valueOf(Longitude));
                                    common.showProgressDialog(context, "please wait...");
                                }else {

                                    if (COUNTER < 10){
                                        requestLocation();
                                        COUNTER++;
                                    }else {
                                        Toast.makeText(context, "can't get location check permission and GPS", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }



                            }else {

                                if (COUNTER < 10){
                                    requestLocation();
                                    COUNTER++;
                                }else {
                                    Toast.makeText(context, "cab shared without location update", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            }
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }


    }

    private void updateLocation1(String Latitude, String Longitude) {

        StringRequest request = new StringRequest(Request.Method.POST, addLocationApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("username", common.getUserName(context));
                map.put("starting_address", startingAddress);
                map.put("ending_address", endingAddress);
                map.put("latitude", Latitude);
                map.put("longitude", Longitude);
                map.put("status", common.activeCab);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
        
    }


    private void getCabDetails() {

        StringRequest request = new StringRequest(Request.Method.POST, fetchCabDetailsApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        common.dismissProgresDialog();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String success = jsonObject.getString("success");

                            if (success.equalsIgnoreCase("1")){

                                for (int i=0; i<jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

//                                    binding.startingAddress.setText(object.getString("from_address"));
//                                    binding.endingAddress.setText(object.getString("to_address"));
//                                    binding.selectStartingTimeBtn.setText(object.getString("starting_time"));
//                                    binding.selectEndingTimeBtn.setText(object.getString("ending_time"));
//                                    binding.tvCarName.setText(object.getString("cab_name"));
//                                    binding.tvCarModel.setText(object.getString("cab_model"));
//                                    binding.tvDriverContact.setText(object.getString("cab_driver_mo"));
//                                    binding.tvVehicalNum.setText(object.getString("cab_vehical_no"));
//                                    binding.tvNoOfSeats.setText(object.getString("available_space"));
//                                    binding.tvType.setText(object.getString("cab_type"));
//                                    binding.tvAcNoneAc.setText(object.getString("ac_noneac"));
//                                    binding.tvVehicalType.setText(object.getString("vehical_type"));
//                                    binding.tgvPaymentAmount.setText(object.getString("payment_amount"));
                                    binding.edStartingAddress.setText(object.getString("from_address"));
                                    binding.endingAddress.setText(object.getString("to_address"));
                                    binding.selectStartingTimeBtn.setText(object.getString("starting_time"));
                                    binding.selectEndingTimeBtn.setText(object.getString("ending_time"));
                                    binding.edCarName.setText(object.getString("cab_name"));
                                    binding.edCarModel.setText(object.getString("cab_model"));
                                    binding.edDriverNo.setText(object.getString("cab_driver_mo"));
                                    binding.edVehicalNo.setText(object.getString("cab_vehical_no"));
                                    binding.edPaymentAmount.setText(object.getString("payment_amount"));
                                    startingTime = object.getString("starting_time");
                                    endingTime = object.getString("ending_time");

                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("cab_id", uniqueCabId);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);


    }

    private void selectStartintTime() {

        Calendar now = Calendar.getInstance();

        if (dpd == null) {
            dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    (DatePickerDialog.OnDateSetListener) context,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    (DatePickerDialog.OnDateSetListener) context,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.setThemeDark(true);
        dpd.setCancelable(false);

        dpd.setOnCancelListener(dialog -> {
            Log.d("DatePickerDialog", "Dialog was cancelled");
            dpd = null;
        });
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");

    }

    private void getData() {

        startingAddress = binding.edStartingAddress.getText().toString().trim();
        endingAddress = binding.endingAddress.getText().toString().trim();
        cabName = binding.edCarName.getText().toString().trim();
        cabModel = binding.edCarModel.getText().toString().trim();
        cabDriverMo = binding.edDriverNo.getText().toString().trim();
        cabVehicalNum = binding.edVehicalNo.getText().toString().trim();


        int ID1 = availableSpaceRG.getCheckedRadioButtonId();
        int ID2 = typeRG.getCheckedRadioButtonId();
        int ID3 = acNoneAcRG.getCheckedRadioButtonId();
        int ID4 = vehicalTypeRG.getCheckedRadioButtonId();
        int ID5 = timesOfTripRG.getCheckedRadioButtonId();

        radioButton1 = findViewById(ID1);
        radioButton2 = findViewById(ID2);
        radioButton3 = findViewById(ID3);
        radioButton4 = findViewById(ID4);
        radioButton5 = findViewById(ID5);

        if (startingAddress.length() < 3){
            common.showToast(context, "fill starting address");
        }else if (endingAddress.length() < 3){
            common.showToast(context, "fill ending address");
        }else if (startingTime.length() < 3){
            common.showToast(context, "select starting time");
        }else if (endingTime.length() < 3){
            common.showToast(context, "select ending time");
        }else if (cabName.length() < 3){
            common.showToast(context, "fill car name");
        }else if (cabModel.length() < 3){
            common.showToast(context, "fill cab model");
        }else if (cabDriverMo.length() < 3){
            common.showToast(context, "fill driver contact");
        }else if (cabVehicalNum.length() < 3){
            common.showToast(context, "fill vehical number");
        }else if (findViewById(ID1) == null){
            common.showToast(context, "select available space");
        }else if (findViewById(ID2) == null){
            common.showToast(context, "select car type");
        }else if (findViewById(ID3) == null){
            common.showToast(context, "select AC/noneAC");
        }else if(findViewById(ID4) ==null){
            common.showToast(context, "select vehical type");
        }else if(findViewById(ID5) == null){
            common.showToast(context, "select times of tirp");
        }else if (binding.edPaymentAmount.getText().toString().length() < 1){
            common.showToast(context, "select payment amount");
        } else {

            common.showProgressDialog(context, "Please wait...");

            availableSpace = radioButton1.getText().toString();
            cabType = radioButton2.getText().toString();
            cabAcNoneAc = radioButton3.getText().toString();
            vehicalType = radioButton4.getText().toString();
            timesOfTrip = radioButton5.getText().toString();

            paymentAmount = binding.edPaymentAmount.getText().toString().trim();

            if (intent.getStringExtra("cabId") != null){
                changeCabDetails();
            }else{
                uniqueCabId = common.getUserName(context) + "_" + cabName.replace(" ", "") + "_" + String.valueOf(System.currentTimeMillis());

                shareCab();
            }

        }

//        if (findViewById(ID) == null){
//            common.showToast(context, "select every radio group");
//        }else {
//
//            radioButton = findViewById(ID);
//            common.showToast(context, radioButton.getText().toString());
//        }

    }

    private void changeCabDetails() {

        StringRequest request = new StringRequest(Request.Method.POST, updateCabDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equalsIgnoreCase("failed") || response.contains("failed")){
                            common.showToast(context, response);
                            finish();
                        }else {
                            requestLocation();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.dismissProgresDialog();
                common.showToast(context, "connection error");
                finish();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("cab_id", uniqueCabId);
                params.put("from_address", startingAddress);
                params.put("to_address", endingAddress);
                params.put("cab_owner", common.getUserName(context));
                params.put("status", "ACTIVE");
                params.put("starting_time", startingTime);
                params.put("ending_time", endingTime);
                params.put("cab_name", cabName);
                params.put("cab_model", cabModel);
                params.put("cab_driver_mo", cabDriverMo);
                params.put("cab_vehical_no", cabVehicalNum);
                params.put("available_space", availableSpace);
                params.put("cab_type", cabType);
                params.put("ac_noneac", cabAcNoneAc);
                params.put("payment", paymentAmount);
                params.put("vehical_type", vehicalType);
                params.put("times_of_trip", timesOfTrip);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void shareCab() {

        StringRequest request = new StringRequest(Request.Method.POST, addCabApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equalsIgnoreCase("failed") || response.contains("failed")){
                            common.showToast(context, response);
                            finish();
                        }else {
                            requestLocation();
                        }
                        
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.dismissProgresDialog();
                common.showToast(context, "connection error");
                finish();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("cab_id", uniqueCabId);
                params.put("from_address", startingAddress);
                params.put("to_address", endingAddress);
                params.put("cab_owner", common.getUserName(context));
                params.put("status", "ACTIVE");
                params.put("starting_time", startingTime);
                params.put("ending_time", endingTime);
                params.put("cab_name", cabName);
                params.put("cab_model", cabModel);
                params.put("cab_driver_mo", cabDriverMo);
                params.put("cab_vehical_no", cabVehicalNum);
                params.put("available_space", availableSpace);
                params.put("cab_type", cabType);
                params.put("ac_noneac", cabAcNoneAc);
                params.put("payment", paymentAmount);
                params.put("vehical_type", vehicalType);
                params.put("times_of_trip", timesOfTrip);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void requestLocation() {

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {

                                if (cameraCheck) {

                                }else {
                                    marker.remove();
                                }

                                Latitude = lastKnownLocation.getLatitude();
                                Longitude = lastKnownLocation.getLongitude();

                                if (Latitude != null && Longitude != null){
                                    updateLocation();
                                }else {

                                    if (COUNTER < 10){
                                        requestLocation();
                                        COUNTER++;
                                    }else {
                                        Toast.makeText(context, "cab shared without location update", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }



                            }else {

                                if (COUNTER < 10){
                                    requestLocation();
                                    COUNTER++;
                                }else {
                                    Toast.makeText(context, "cab shared without location update", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            }
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }

    private void updateLocation() {

        StringRequest request = new StringRequest(Request.Method.POST, updateLocation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        common.dismissProgresDialog();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
                common.dismissProgresDialog();
                finish();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("cab_id", uniqueCabId);
                map.put("latitude", String.valueOf(Latitude));
                map.put("longitude", String.valueOf(Longitude));

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String y = String.valueOf(year);
        String month = String.valueOf(monthOfYear + 1);
        String day = String.valueOf(dayOfMonth);

        dateTime = day + "-" + month + "_" + y;

        Calendar now = Calendar.getInstance();

        if (tpd == null) {
            tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                    (TimePickerDialog.OnTimeSetListener) context,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );
        } else {
            tpd.initialize(
                    (TimePickerDialog.OnTimeSetListener) context,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND),
                    true
            );
        }
        tpd.setThemeDark(true);
        tpd.vibrate(true);
        tpd.setCancelable(false);
        tpd.setOnCancelListener(dialogInterface -> {
            Log.d("TimePicker", "Dialog was cancelled");
            tpd = null;
        });
        tpd.show(getSupportFragmentManager(), "Timepickerdialog");

    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {

        String hour = String.valueOf(hourOfDay);
        String min = String.valueOf(minute);
        String sec = String.valueOf(second);

        dateTime = dateTime + " , " +hour + ":" + min;

        if (stTimeBoll){
            binding.selectStartingTimeBtn.setText(dateTime);
            startingTime = dateTime;
        }else {
            binding.selectEndingTimeBtn.setText(dateTime);
            endingTime = dateTime;
        }

    }
}