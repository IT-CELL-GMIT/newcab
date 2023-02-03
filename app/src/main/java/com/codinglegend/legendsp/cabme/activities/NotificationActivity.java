package com.codinglegend.legendsp.cabme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
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
import com.codinglegend.legendsp.cabme.fragment.CabNotificationsFragment;
import com.codinglegend.legendsp.cabme.fragment.NotificationFragment;
import com.codinglegend.legendsp.cabme.models.NotificationModel;
import com.google.android.material.tabs.TabLayout;

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
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        context = NotificationActivity.this;

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }

    private void setUpWithViewPager(ViewPager viewPager){

        NotificationActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new NotificationFragment(), "My Notification");
        adapter.addFragment(new CabNotificationsFragment(), "Cab Notification");

        viewPager.setAdapter(adapter);

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager){

            super(manager);
        }

        @Override
        public Fragment getItem(int positon){

            return mFragmentList.get(positon);
        }
        @Override
        public int getCount(){

            return mFragmentList.size();

        }
        public void addFragment(Fragment fragment, String title){

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }
        @Override
        public CharSequence getPageTitle(int position){

            return mFragmentTitleList.get(position);
        }
    }


}