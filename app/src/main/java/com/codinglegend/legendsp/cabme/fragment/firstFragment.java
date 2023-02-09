package com.codinglegend.legendsp.cabme.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.MainActivity;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.activities.ShareCabActivity;
import com.codinglegend.legendsp.cabme.adapters.ShowCabAdapter;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.ActivityMainBinding;
import com.codinglegend.legendsp.cabme.databinding.FragmentFirstBinding;
import com.codinglegend.legendsp.cabme.models.ShowCabModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link firstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class firstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Context context;
    private FragmentFirstBinding binding;

    List<ShowCabModel> cabList;
    ShowCabAdapter cabAdapter;
    List<String> cabCheck;
Button shareCabBtn;
    String fetchActiveCabs = common.getBaseUrl() + "fetchActiveCabs.php";
View view;
    public firstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment firstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static firstFragment newInstance(String param1, String param2) {
        firstFragment fragment = new firstFragment();
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
//        binding = firstFragment.inflate(inflater, container, false);
//        return inflater.inflate(R.layout.fragment_first, container, false);
//        context = binding.getRoot().getContext();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first,container,false);
        view = binding.getRoot();
        context = binding.getRoot().getContext();
        shareCabBtn = view.findViewById(R.id.shareCabBtn);
        cabList = new ArrayList<>();
        cabCheck = new ArrayList<>();
        RecyclerView showCabRecyclerview = view.findViewById(R.id.shareCabRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        showCabRecyclerview.setLayoutManager(layoutManager);
        cabAdapter = new ShowCabAdapter(cabList, context, "firstFragment");
        showCabRecyclerview.setAdapter(cabAdapter);

        getCabs();

        shareCabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chekcPermission()) {
                    statusCheck();
                } else {
                    common.showToast(context, "Grant location permission first");
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                }
            }
        });

        binding.shareLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ShareCabActivity.class).putExtra("from", "shareLocation"));
            }
        });

        return view;
    }


    private void getCabs() {

        StringRequest request = new StringRequest(Request.Method.POST, fetchActiveCabs,
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

                                    cabList.add(0, new ShowCabModel(object.getString("cab_id"),
                                            object.getString("from_address"),
                                            object.getString("to_address"),
                                            object.getString("starting_time") + " to " + object.getString("ending_time"),
                                            object.getString("cab_owner"),
                                            "none"));

                                    cabAdapter.notifyDataSetChanged();

                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "format error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.showToast(context, "connection error");
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else {

            if (binding.shareCabBtn.getText().toString().equalsIgnoreCase("Offer Lift")) {
                startActivity(new Intent(context, ShareCabActivity.class).putExtra("from", "shareCab"));
                binding.shareCabBtn.setText("refress");
                cabList.clear();
                cabAdapter.notifyDataSetChanged();
            } else {
                binding.shareCabBtn.setText("Offer Lift");
                getCabs();
            }


        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, enable first!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        common.showToast(context, "enable GPS first");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean chekcPermission() {

        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);

    }

    }
