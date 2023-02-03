package com.codinglegend.legendsp.cabme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.codinglegend.legendsp.cabme.MainActivity;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.adapters.ChatsAdapter;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.ActivityChatsBinding;
import com.codinglegend.legendsp.cabme.models.ChatsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {

    private ActivityChatsBinding binding;
    private Context context;

    String sendMsgOn = common.getBaseUrl() + "insertchat.php";
    String apiGetChat = common.getBaseUrl() + "getChat.php";
    String fetchUserDataApi = common.getBaseUrl() + "fetchUserData.php";

    String userName, profilePic;

    Thread thread;

    int chatListSize = 0;

    static List<ChatsModel> list;
    ChatsAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);
        context = ChatsActivity.this;

        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        binding.tvFullname.setText(userName);

        fetchUserData(userName);


        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adapter = new ChatsAdapter(list, ChatsActivity.this);
        recyclerView.setAdapter(adapter);


//        getMsgs();


        binding.edMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (i2 == 0) {
                    binding.micBtn.setVisibility(View.VISIBLE);
                    binding.sendMsgBtn.setVisibility(View.GONE);
                } else {
                    binding.micBtn.setVisibility(View.GONE);
                    binding.sendMsgBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg(binding.edMsg.getText().toString().trim());
                binding.edMsg.setText("");
            }
        });


        chatListSize = 0;
        getChat();

        thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1500);
                        getChat();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };



    }

    private void sendMsg(String trim) {

        list.add(0, new ChatsModel(common.getTimeDate(),
                trim,
                "",
                "TEXT",
                common.getUserName(this),
                "unknown",
                ""));
        adapter.notifyDataSetChanged();

        setMsgOnline(trim);

    }

    private void setMsgOnline(String msg) {

        StringRequest request = new StringRequest(Request.Method.POST, sendMsgOn,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                        if (response.contains("Data Inserted") || response.equalsIgnoreCase("Data Inserted")) {
                            Toast.makeText(context, "msg", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(context, "failed to send{Initial Error}", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.remove(0);
                Toast.makeText(context, "unable send msg{Network Error}", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("sender", common.getUserName(context));
                params.put("receiver", userName);
                params.put("content", msg);
                params.put("datetime", common.getTimeDate());
                params.put("url", "none");
                params.put("type", "TEXT");
                params.put("name", "none");
                params.put("extension", "none");

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }


    private void getChat() {

        StringRequest request = new StringRequest(Request.Method.POST, apiGetChat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String success = jsonObject.getString("success");

                            if (success.equalsIgnoreCase("1")) {

                                if (chatListSize != jsonArray.length()) {

                                    list.clear();
                                    chatListSize = jsonArray.length();

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject object = jsonArray.getJSONObject(i);

                                        list.add(new ChatsModel(object.getString("datetime"),
                                                object.getString("content"),
                                                object.getString("url"),
                                                object.getString("type"),
                                                object.getString("sender"),
                                                object.getString("receiver"),
                                                object.getString("name")));

                                        adapter.notifyDataSetChanged();

                                    }


                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getChat();
                                    }
                                }, 1500);

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "format error{JSON}", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("sender", common.getUserName(context));
                params.put("receiver", userName);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }


    private void getMsgs() {

        list.add(0, new ChatsModel("UNKNOW",
                "hii how are you",
                "",
                "TEXT",
                common.getUserName(context),
                "sanjay@123",
                ""));

        list.add(0, new ChatsModel("UNKNOW",
                "i'm fine",
                "",
                "TEXT",
                "sanjay@123",
                common.getUserName(context),
                ""));

        list.add(0, new ChatsModel("UNKNOW",
                "",
                profilePic,
                "IMAGE",
                "sanjay@123",
                common.getUserName(context),
                "tonystark.jpeg"));

        list.add(0, new ChatsModel("UNKNOW",
                "",
                profilePic,
                "IMAGE",
                common.getUserName(context),
                "",
                "tonystark.jpeg"));

        adapter.notifyDataSetChanged();

    }


    private void fetchUserData(String userName) {

        StringRequest request = new StringRequest(Request.Method.POST, fetchUserDataApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (success.equals("1")){
                                String username, fullName, profilepic, phoneNumber, eMail, password;

                                JSONObject object = jsonArray.getJSONObject(0);

                                username = object.getString("username");
                                fullName = object.getString("full_name");
                                profilepic = object.getString("profile_pic");
                                phoneNumber = object.getString("mobile_no");
                                eMail = object.getString("e_mail");
                                password = object.getString("password");

                                profilePic = profilepic;
                                Glide.with(context).load(profilePic).into(binding.profilePic);


                            }else {
                                common.dismissProgresDialog();
                                Toast.makeText(context, "not succeed try later", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            common.dismissProgresDialog();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "failed to retrive old data, try again later", Toast.LENGTH_SHORT).show();
                common.dismissProgresDialog();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", userName);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

}