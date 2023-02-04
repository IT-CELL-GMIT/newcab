package com.codinglegend.legendsp.cabme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.codinglegend.legendsp.cabme.activities.NotificationActivity;
import com.codinglegend.legendsp.cabme.adapters.ShowCabAdapter;
import com.codinglegend.legendsp.cabme.databinding.ActivityMainBinding;
import com.codinglegend.legendsp.cabme.fragment.ActiveCabsFragment;
import com.codinglegend.legendsp.cabme.fragment.MapsFragment;
import com.codinglegend.legendsp.cabme.fragment.MyProfileFragment;
import com.codinglegend.legendsp.cabme.fragment.SettingsFragment;
import com.codinglegend.legendsp.cabme.fragment.firstFragment;
import com.codinglegend.legendsp.cabme.fragment.secondfragment;
import com.codinglegend.legendsp.cabme.models.ShowCabModel;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;
    private ActivityMainBinding binding;

    List<ShowCabModel> cabList;
    ShowCabAdapter cabAdapter;
    List<String> cabCheck;

    String fetchActiveCabs = common.getBaseUrl() + "fetchActiveCabs.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = MainActivity.this;
        replacefragment(new MapsFragment());

//        getDummyCabs();

        ImageView notificationBtn = findViewById(R.id.notif);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, NotificationActivity.class));
            }
        });

        binding.navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {


                    case R.id.first:
                        replacefragment(new MapsFragment());
                        break;
                    case R.id.second:
                        replacefragment(new firstFragment());
                        break;
                    case R.id.third:
                        replacefragment(new secondfragment());
                        break;
                    case R.id.my_cabs:
                        replacefragment(new ActiveCabsFragment());
                        break;
                    case R.id.fourth:
                        replacefragment(new MyProfileFragment());
                        break;
                }

                return true;
            }
        });
        cabList = new ArrayList<>();
        cabCheck = new ArrayList<>();
//        RecyclerView showCabRecyclerview = findViewById(R.id.shareCabRecyclerview);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setReverseLayout(true);
//        showCabRecyclerview.setLayoutManager(layoutManager);
//        cabAdapter = new ShowCabAdapter(cabList, context, "MainActivity");
//        showCabRecyclerview.setAdapter(cabAdapter);

//        getCabs();

//        binding.shareCabBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (chekcPermission()) {
//                    statusCheck();
//                } else {
//                    common.showToast(context, "Grant location permission first");
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                            0);
//                }
//            }
//        });

    }
    private  void replacefragment(Fragment  fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }

//    private void getCabs() {
//
//        StringRequest request = new StringRequest(Request.Method.POST, fetchActiveCabs,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//                            String success = jsonObject.getString("success");
//
//                            if (success.equalsIgnoreCase("1")) {
//
//                                for (int i = 0; i < jsonObject.length(); i++) {
//
//                                    JSONObject object = jsonArray.getJSONObject(i);
//
//                                    cabList.add(new ShowCabModel(object.getString("cab_id"),
//                                            object.getString("from_address"),
//                                            object.getString("to_address"),
//                                            object.getString("starting_time") + " to " + object.getString("ending_time"),
//                                            object.getString("cab_owner")));
//
//                                    cabAdapter.notifyDataSetChanged();
//
//                                }
//
//                            }
//
//                        } catch (JSONException e) {
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                common.showToast(context, "connection error");
//            }
//        }) {
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//
//                return params;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(context);
//        queue.add(request);
//
//    }
//
//    public void statusCheck() {
//        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps();
//
//        } else {
//
//            if (binding.shareCabBtn.getText().toString().equalsIgnoreCase("share a cab")) {
//                startActivity(new Intent(context, ShareCabActivity.class));
//                binding.shareCabBtn.setText("refress");
//                cabList.clear();
//                cabAdapter.notifyDataSetChanged();
//            } else {
//                binding.shareCabBtn.setText("share a cab");
//                getCabs();
//            }
//
//
//        }
//    }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Your GPS seems to be disabled, enable first!")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                        common.showToast(context, "enable GPS first");
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    private boolean chekcPermission() {
//
//        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
//        int res = checkCallingOrSelfPermission(permission);
//        return (res == PackageManager.PERMISSION_GRANTED);
//
//    }
//
//    private void getDummyCabs() {
//
//        cabList = new ArrayList<>();
//        cabCheck = new ArrayList<>();
//        RecyclerView showCabRecyclerview = findViewById(R.id.shareCabRecyclerview);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        showCabRecyclerview.setLayoutManager(layoutManager);
//        cabAdapter = new ShowCabAdapter(cabList, context, "MainActivity");
//        showCabRecyclerview.setAdapter(cabAdapter);
//
//        cabList.add(new ShowCabModel("1",
//                "Bhavnagar",
//                "Ahemdabad",
//                "55-55-5555 55:55 to 66-66-6666 66:66",
//                "Tanishq"));
//
//        cabList.add(new ShowCabModel("2",
//                "Rajkot",
//                "Katch",
//                "55-55-5555 55:55 to 66-66-6666 66:66",
//                "Pandey"));
//
//        cabList.add(new ShowCabModel("3",
//                "Dang",
//                "Bhavena",
//                "55-55-5555 55:55 to 66-66-6666 66:66",
//                "Hamza"));
//
//        cabList.add(new ShowCabModel("4",
//                "Bhavanagar",
//                "Bombay",
//                "55-55-5555 55:55 to 66-66-6666 66:66",
//                "Zala"));
//
//        cabAdapter.notifyDataSetChanged();
//
//    }
}