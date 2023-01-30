package com.codinglegend.legendsp.cabme.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.activity_book_now;
import com.codinglegend.legendsp.cabme.common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment{

    private Context context;

    View view;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private GoogleMap map;
    private static final int DEFAULT_ZOOM = 17;
    private boolean locationPermissionGranted = true;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    Double Latitude, Longitude;
    Boolean cameraCheck = true;
    Marker marker;

    String fetchNearCabs = common.getBaseUrl() + "FetchNearCabs.php";
    String fetchCabOwner = common.getBaseUrl() + "FetchCabOwner.php";

    String latString = "", longString = "";
    String lat, longs;
    Boolean latBool = true, longBool = true;
    Boolean cabFetchCheck = true;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
             map = googleMap;
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            Toast.makeText(context, "map ready", Toast.LENGTH_SHORT).show();


//            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//                @Override
//                // Return null here, so that getInfoContents() is called next.
//                public View getInfoWindow(Marker arg0) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//                    // Inflate the layouts for the info window, title and snippet.
//                    View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
//                            (FrameLayout) view.findViewById(R.id.map), false);
//
//                    TextView title = infoWindow.findViewById(R.id.title);
//                    title.setText(marker.getTitle());
//
//                    TextView snippet = infoWindow.findViewById(R.id.snippet);
//                    snippet.setText(marker.getSnippet());
//
//                    return infoWindow;
//                }
//            });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker clickMarker) {
                    startActivity(new Intent(context, activity_book_now.class)
                            .putExtra("cabId", clickMarker.getSnippet()));
                    return false;
                }
            });
            
            updateLocationUI();

            getDeviceLocation();
            
        }
    };

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // [END maps_current_place_update_location_ui]


    private void getDeviceLocation() {


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

                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(lastKnownLocation.getLatitude(),
                                                    lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                    cameraCheck = false;
                                }else {
                                    marker.remove();
                                }

                                Latitude = lastKnownLocation.getLatitude();
                                Longitude = lastKnownLocation.getLongitude();

                                lat = String.valueOf(Latitude);
                                longs = String.valueOf(Longitude);

                                LatLng locationLL = new LatLng(Latitude, Longitude);


                                for (int i=0; i<lat.length(); i++){

                                    if (latBool){

                                        if (lat.charAt(i) == '.'){
                                            latBool = false;
                                            latString = latString + String.valueOf(lat.charAt(i));
                                            latString = latString + String.valueOf(lat.charAt(i+1));
                                        }else {

                                            latString = latString + String.valueOf(lat.charAt(i));
                                        }

                                    }

                                }

                                for (int i=0; i<longs.length(); i++){

                                    if (longBool){

                                        if (longs.charAt(i) == '.'){
                                            longBool = false;
                                            longString = longString + String.valueOf(longs.charAt(i));
                                            longString = longString + String.valueOf(longs.charAt(i+1));
                                        }else {

                                            longString = longString + String.valueOf(longs.charAt(i));
                                        }

                                    }

                                }


                                fetchNearCabs();
                                cabFetchCheck = false;


                                MarkerOptions options = new MarkerOptions();
                                options.position(locationLL);
                                options.title("My Location");

                                marker = map.addMarker(options);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getDeviceLocation();
                                    }
                                }, 1500);

                            }
                        } else {

                            getDeviceLocation();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }


    }

    private void fetchNearCabs() {

        if (cabFetchCheck) {

            StringRequest request = new StringRequest(Request.Method.POST, fetchNearCabs,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                String success = jsonObject.getString("success");

                                if (success.equalsIgnoreCase("1")) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject object = jsonArray.getJSONObject(i);

                                        Double lat = Double.valueOf(object.getString("latitude"));
                                        Double longs = Double.valueOf(object.getString("longitude"));


                                        fetchDetailFromCabId(object.getString("cab_id"), lat, longs);

                                    }

                                }

                            } catch (JSONException e) {
                                Toast.makeText(context, "formar error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();

                    map.put("latitude", latString);
                    map.put("longitude", longString);

                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        }

    }

    private void fetchDetailFromCabId(String cab_id, Double lat, Double longs) {

        StringRequest request = new StringRequest(Request.Method.POST, fetchCabOwner,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String success = jsonObject.getString("success");

                            if (success.equalsIgnoreCase("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String cabOwner = object.getString("cab_owner");

                                    LatLng latLng = new LatLng(lat, longs);

                                    MarkerOptions options = new MarkerOptions();
                                    options.position(latLng);
                                    options.title(cabOwner);
                                    options.snippet(cab_id);
//                                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_directions_car_filled_24));

                                    Marker marker1 = map.addMarker(options);
                                    marker1.showInfoWindow();


                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "formar error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("cab_id", cab_id);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_maps, container, false);
        context = view.getContext();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        return view;
        
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

//
    }
    
}