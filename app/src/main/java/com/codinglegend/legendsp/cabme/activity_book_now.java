package com.codinglegend.legendsp.cabme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.databinding.ActivityBookNowBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class activity_book_now extends AppCompatActivity {

    private ActivityBookNowBinding binding;
    private Context context;

    String cabId, fromActiviy;

    String fetchCabDetailsApi = common.getBaseUrl() + "FetchCabDetails.php";
    String sendCabRequestApi = common.getBaseUrl() + "RequestCab.php";

    Intent mServiceIntent;
    private YourService mYourService;

    String cabOwner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_now);
        context = activity_book_now.this;

        Intent intent = getIntent();
        cabId = intent.getStringExtra("cabId");
        fromActiviy = intent.getStringExtra("activityName");

        if (fromActiviy != null){

            if (fromActiviy.equalsIgnoreCase("CabNotificaionFragment")){
                binding.doneBtn.setVisibility(View.GONE);
            }

        }

        getCabDetails();

        mYourService = new YourService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendCabRequest();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("tag", "Running");
                return true;
            }
        }
        Log.i ("tag", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    private void sendCabRequest() {

        StringRequest request = new StringRequest(Request.Method.POST, sendCabRequestApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
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

                map.put("cab_id", cabId);
                map.put("username", common.getUserName(context));
                map.put("status", common.cabRequest);

                return  map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }



    private void getCabDetails() {

        common.showProgressDialog(context, "Please wait...");

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

                                    binding.startingAddress.setText(object.getString("from_address"));
                                    binding.endingAddress.setText(object.getString("to_address"));
                                    binding.selectStartingTimeBtn.setText(object.getString("starting_time"));
                                    binding.selectEndingTimeBtn.setText(object.getString("ending_time"));
                                    binding.tvCarName.setText(object.getString("cab_name"));
                                    binding.tvCarModel.setText(object.getString("cab_model"));
                                    binding.tvDriverContact.setText(object.getString("cab_driver_mo"));
                                    binding.tvVehicalNum.setText(object.getString("cab_vehical_no"));
                                    binding.tvNoOfSeats.setText(object.getString("available_space"));
                                    binding.tvType.setText(object.getString("cab_type"));
                                    binding.tvAcNoneAc.setText(object.getString("ac_noneac"));
                                    binding.tvVehicalType.setText(object.getString("vehical_type"));
                                    binding.tgvPaymentAmount.setText(object.getString("payment_amount"));
                                    binding.tvTimesOfTrip.setText(object.getString("times_of_trip"));

                                    cabOwner = object.getString("cab_owner");

                                    if (cabOwner.equals(common.getUserName(context))){
                                        binding.doneBtn.setVisibility(View.GONE);
                                    }

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

                map.put("cab_id", cabId);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }
}