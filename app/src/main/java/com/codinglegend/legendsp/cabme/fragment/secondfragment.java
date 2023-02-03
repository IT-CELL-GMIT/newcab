package com.codinglegend.legendsp.cabme.fragment;

import static com.codinglegend.legendsp.cabme.common.getUserName;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.codinglegend.legendsp.cabme.adapters.ChatListAdapter;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.FragmentSecondfragmentBinding;
import com.codinglegend.legendsp.cabme.models.ChatListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link secondfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class secondfragment extends Fragment {

    private FragmentSecondfragmentBinding binding;
    private Context context;

    List<ChatListModel> list;
    ChatListAdapter adapter;
    List<String> stringList;

    String apiChatList = common.getBaseUrl() + "getChatList.php";
    String fetchUserDataApi = common.getBaseUrl() + "fetchUserData.php";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public secondfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment secondfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static secondfragment newInstance(String param1, String param2) {
        secondfragment fragment = new secondfragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secondfragment, container, false);
        context = binding.getRoot().getContext();

        list = new ArrayList<>();
        stringList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(list, binding.getRoot().getContext(), "CHATSFRAGMENT");
        binding.recyclerView.setAdapter(adapter);

        getChats();

        return binding.getRoot();
    }

    private void getChats() {

        stringList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, apiChatList,
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

                                    if (object.getString("sender").equals(getUserName(context))){
                                        getUserInfo(object.getString("receiver"));
                                    }else if (object.getString("receiver").equalsIgnoreCase(getUserName(context))){
                                        getUserInfo(object.getString("sender"));
                                    }

                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "format error{JSON}", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();

                params.put("username", getUserName(context));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void getUserInfo(String friendname) {

        if (stringList.contains(friendname)){

        }else {

            stringList.add(friendname);

            StringRequest request = new StringRequest(Request.Method.POST, fetchUserDataApi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                String success = jsonObject.getString("success");

                                if (success.equalsIgnoreCase("1")) {

                                    JSONObject object = jsonArray.getJSONObject(0);

                                    list.add(new ChatListModel("",
                                            object.getString("username"),
                                            object.getString("full_name"),
                                            object.getString("profile_pic"),
                                            object.getString("mobile_no"),
                                            object.getString("e_mail"),
                                            "recently",
                                            "",
                                            "0",
                                            "IMG",
                                            "TEXT"));

                                    adapter.notifyDataSetChanged();

                                }

                            } catch (JSONException e) {
                                Toast.makeText(context, "format error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
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
                    Map<String, String> params = new HashMap<>();

                    params.put("username", friendname);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

        }

    }


}