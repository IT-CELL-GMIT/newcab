package com.codinglegend.legendsp.cabme.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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
import com.bumptech.glide.Glide;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.databinding.FragmentMyProfileBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {

    String verifyUserApi = common.getBaseUrl() + "setUserType.php";

    String aadharString = "", aadharExt = "";
    String licenseString = "", licenseExt = "";

    private FragmentMyProfileBinding binding;
    private Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false);
        context = binding.getRoot().getContext();

        binding.verifyUserLL.setVisibility(View.GONE);
        binding.userTypeLL.setVisibility(View.GONE);

        binding.username.setText(common.getUserName(context));
        binding.tvPosition.setText(common.getFullName(context));
        Glide.with(context).load(common.getProfilePic(context)).into(binding.mainProfilePic);

        binding.verifyUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (common.getUserType(context).equalsIgnoreCase(common.userTypeVehicalOwner) || common.getUserType(context).equalsIgnoreCase(common.userTypeCustomer)){

                    if (binding.verifyUserLL.getVisibility() == View.GONE) {
                        binding.verifyUserLL.setVisibility(View.VISIBLE);
                    } else {
                        binding.verifyUserLL.setVisibility(View.GONE);
                    }
                    
                }else {

                    Toast.makeText(context, "select user type first", Toast.LENGTH_SHORT).show();

                }

            }
        });

        binding.userTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (common.getUserType(context).equalsIgnoreCase(common.userTypeVehicalOwner) || common.getUserType(context).equalsIgnoreCase(common.userTypeCustomer)){

                    Toast.makeText(context, "user type already selected", Toast.LENGTH_SHORT).show();

                }else {

                    if (binding.userTypeLL.getVisibility() == View.GONE){
                        binding.userTypeLL.setVisibility(View.VISIBLE);
                    }else {
                        binding.userTypeLL.setVisibility(View.GONE);
                    }

                }

            }
        });

        binding.abcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure Vehical Owner ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                common.saveUserType(common.userTypeVehicalOwner, context);
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

        binding.customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("are you sure Customer?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                common.saveUserType(common.userTypeCustomer, context);
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

        binding.aadharBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDoc();

            }
        });

        binding.drivingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (common.getAadharStatus(context).equalsIgnoreCase(common.statusPositive) && !Objects.equals(common.getAadhar(context), "none")){
                    getLicense();
                }else {
                    Toast.makeText(context, "select license first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return binding.getRoot();
    }

    private void getLicense() {

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "please select your profile pic"), 2);

    }

    private void getDoc() {

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "please select your profile pic"), 1);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1
                && resultCode == RESULT_OK
                && data.getData() != null){

            Uri imagePath = data.getData();

            aadharString = common.ConvertToString(imagePath, context);
            aadharExt = common.getExtension(context,imagePath);
            Toast.makeText(context, aadharString, Toast.LENGTH_SHORT).show();

            common.saveAadhar(aadharString, context);
            common.saveAadharStatus(common.statusPositive, context);
            common.saveAadharExt(aadharExt, context);


        }else if (requestCode == 2
                && resultCode == RESULT_OK
                && data.getData() != null){

            Uri imagePath = data.getData();

            licenseString = common.ConvertToString(imagePath, context);
            licenseExt = common.getExtension(context, imagePath);
            Toast.makeText(context, licenseString, Toast.LENGTH_SHORT).show();

            uploadDetails(licenseString, licenseExt);

            common.showProgressDialog(context, "Please wait");

        }else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();

        }

    }

    private void uploadDetails(String licenseString, String licenseExt) {

        StringRequest request = new StringRequest(Request.Method.POST, verifyUserApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                common.dismissProgresDialog();
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                if (response.contains("data inserted") || response.equalsIgnoreCase("data inserted")){
                    common.saveUserStatus(common.userVerified, context);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.dismissProgresDialog();
                Toast.makeText(context, "connection error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("username", common.getUserName(context));
                map.put("user_type", common.getUserType(context));
                map.put("aadhar_verification", common.getAadhar(context));
                map.put("license_verification", licenseString);
                map.put("a_ext", common.getAadharExt(context));
                map.put("b_ext", licenseExt);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }
}