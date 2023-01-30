package com.codinglegend.legendsp.cabme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.adapters.NotificationAdapter;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.ActivityNotificationBinding;
import com.codinglegend.legendsp.cabme.models.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private Context context;

    List<NotificationModel> list;
    NotificationAdapter adapter;

    String fetchUserCabsApi = common.getBaseUrl() + "FetchUserCabs.php";
    String fetchNotificationApi = common.getBaseUrl() + "FetchNotifications.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        context = NotificationActivity.this;

        list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotificationAdapter(context, list);
        recyclerView.setAdapter(adapter);

        fetchUserCabs();

    }

    private void fetchUserCabs() {

        StringRequest request = new StringRequest(Request.Method.POST, fetchUserCabsApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String success = jsonObject.getString("success");

                            if (success.equalsIgnoreCase("1")){

                                for (int i=0; i<jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    fetchNotification(object.getString("cab_id"));

                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "format error", Toast.LENGTH_SHORT).show();
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

                map.put("username", common.getUserName(context));

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void fetchNotification(String cab_id) {

        StringRequest request = new StringRequest(Request.Method.POST, fetchNotificationApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String success = jsonObject.getString("success");

                            if (success.equalsIgnoreCase("1")){

                                for (int i=0; i<jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    if (object.getString("status").equalsIgnoreCase(common.cabRequest)){

                                        list.add(new NotificationModel(object.getString("cab_id"),
                                                "",
                                                object.getString("username"),
                                                "Has sent request",
                                                "",
                                                "",
                                                common.cabRequest));
                                        adapter.notifyDataSetChanged();

                                    }else if (object.getString("status").equalsIgnoreCase(common.cabRequestAccept)){

                                        list.add(new NotificationModel(object.getString("cab_id"),
                                                "",
                                                object.getString("username"),
                                                "Cab Request",
                                                "",
                                                "",
                                                common.cabRequestAccept));
                                        adapter.notifyDataSetChanged();

                                    }else if (object.getString("status").equalsIgnoreCase(common.cabRequestDecline)){

                                        list.add(new NotificationModel(object.getString("cab_id"),
                                                "",
                                                object.getString("username"),
                                                "Cab Request",
                                                "",
                                                "",
                                                common.cabRequestDecline));
                                        adapter.notifyDataSetChanged();

                                    }else {

                                    }




                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "format error", Toast.LENGTH_SHORT).show();
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

                map.put("cab_id", cab_id);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

}