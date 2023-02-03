package com.codinglegend.legendsp.cabme.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.codinglegend.legendsp.cabme.adapters.NotificationAdapter;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.FragmentNotificationBinding;
import com.codinglegend.legendsp.cabme.models.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding binding;
    private Context context;

    List<NotificationModel> list;
    NotificationAdapter adapter;

    String fetchUserCabsApi = common.getBaseUrl() + "FetchUserCabs.php";
    String fetchNotificationApi = common.getBaseUrl() + "FetchNotifications.php";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        context = binding.getRoot().getContext();

        list = new ArrayList<>();
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotificationAdapter(context, list);
        recyclerView.setAdapter(adapter);

        fetchUserCabs();

        return binding.getRoot();
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