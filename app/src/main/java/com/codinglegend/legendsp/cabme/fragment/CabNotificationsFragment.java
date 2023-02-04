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
import com.codinglegend.legendsp.cabme.databinding.FragmentCabNotificationsBinding;
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
 * Use the {@link CabNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CabNotificationsFragment extends Fragment {

    private FragmentCabNotificationsBinding binding;
    private Context context;

    String myNotificationApi = common.getBaseUrl() + "fetchUserNotification.php";
    String fetchCabDetailsApi = common.getBaseUrl() + "FetchCabDetails.php";

    List<NotificationModel> list;
    NotificationAdapter adapter;
    List<String> notiCheck;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CabNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CabNotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CabNotificationsFragment newInstance(String param1, String param2) {
        CabNotificationsFragment fragment = new CabNotificationsFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cab_notifications, container, false);
        context = binding.getRoot().getContext();

        list = new ArrayList<>();
        notiCheck = new ArrayList<>();
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotificationAdapter(context, list, "CabNotificationFragment");
        recyclerView.setAdapter(adapter);

        getNotifications();

        return  binding.getRoot();
    }

    private void getNotifications() {

        StringRequest request = new StringRequest(Request.Method.POST, myNotificationApi,
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

                                    fetchCabDetails(object.getString("cab_id"), object.getString("status"));

                                }

                            }

                        } catch (JSONException e) {
                            common.showToast(context, "format error");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.showToast(context, common.getStrings(context, R.string.cn_error));
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

    private void fetchCabDetails(String cab_id, String status) {

        StringRequest request = new StringRequest(Request.Method.POST, fetchCabDetailsApi,
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

                                    if (status.equalsIgnoreCase(common.cabRequest)){

                                        list.add(new NotificationModel(cab_id,
                                                "",
                                                object.getString("cab_owner"),
                                                "You sent cab request {Request is in a queue}",
                                                "",
                                                "",
                                                common.cabRequest,
                                                object.getString("from_address") + " to " + object.getString("to_address")));

                                        adapter.notifyDataSetChanged();

                                    }else if (status.equalsIgnoreCase(common.cabRequestAccept)){

                                        list.add(new NotificationModel(cab_id,
                                                "",
                                                object.getString("cab_owner"),
                                                "Your cab request has been accepted",
                                                "",
                                                "",
                                                common.cabRequestAccept,
                                                object.getString("from_address") + " to " + object.getString("to_address")));

                                        adapter.notifyDataSetChanged();

                                    }else if (status.equalsIgnoreCase(common.cabRequestDecline)){

                                        list.add(new NotificationModel(cab_id,
                                                "",
                                                object.getString("cab_owner"),
                                                "Your cab request has been declined",
                                                "",
                                                "",
                                                common.cabRequestDecline,
                                                object.getString("from_address") + " to " + object.getString("to_address")));

                                        adapter.notifyDataSetChanged();

                                    }else {

                                    }

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
//                                    binding.tvTimesOfTrip.setText(object.getString("times_of_trip"));
//
//                                    cabOwner = object.getString("cab_owner");
//
//                                    if (cabOwner.equals(common.getUserName(context))){
//                                        binding.doneBtn.setVisibility(View.GONE);
//                                    }

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

                map.put("cab_id", cab_id);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }
}