package com.codinglegend.legendsp.cabme.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.activities.ShareCabActivity;
import com.codinglegend.legendsp.cabme.activity_book_now;
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.models.ShowCabModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowCabAdapter extends RecyclerView.Adapter<ShowCabAdapter.CabHolder> {

    List<ShowCabModel> list;
    Context context;
    String activityName;

    String cabCheckApi = common.getBaseUrl() + "CabRequestCheck.php";
    String deleteCabApi = common.getBaseUrl() + "DeleteCab.php";

    public ShowCabAdapter(List<ShowCabModel> list, Context context, String activityName) {
        this.list = list;
        this.context = context;
        this.activityName = activityName;
    }

    @NonNull
    @Override
    public ShowCabAdapter.CabHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_sharecab_layout, parent, false);

        return new CabHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowCabAdapter.CabHolder holder, int position) {

        holder.fromAddress.setText(list.get(position).getCabFromAddress());
        holder.toAddress.setText(list.get(position).getCabToAdress());
        holder.time.setText(list.get(position).getCabTime());

        if (activityName.equalsIgnoreCase("firstFragment")){

            holder.settingsLL.setVisibility(View.GONE);
            holder.bookNowBtn.setVisibility(View.VISIBLE);

            holder.bookNowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checkCab(list.get(position).getCabId());

                }
            });

        }else if (activityName.equalsIgnoreCase("SettingsFragment")){

            holder.bookNowBtn.setVisibility(View.GONE);
            holder.settingsLL.setVisibility(View.VISIBLE);

            holder.settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ShareCabActivity.class)
                            .putExtra("cabId", list.get(position).getCabId()));
                }
            });

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Sure to delete this cab?");
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            "Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    deleteCab(list.get(position).getCabId());
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

    }

    private void deleteCab(String cabId) {

        StringRequest request = new StringRequest(Request.Method.POST, deleteCabApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("failed") || response.contains("failed")){
                            common.showToast(context, response);
                        }else {
                            common.showToast(context, "deleted");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.showToast(context, "connection error");
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

    private void checkCab(String cabID) {

        StringRequest request = new StringRequest(Request.Method.POST, cabCheckApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        
                        if (response.equalsIgnoreCase("not exist") || response.contains("not exist")){
                            context.startActivity(new Intent(context, activity_book_now.class)
                                    .putExtra("cabId", cabID));
                        }else {
                            Toast.makeText(context, "request already sent", Toast.LENGTH_SHORT).show();
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
                map.put("cab_id", cabID);

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CabHolder extends RecyclerView.ViewHolder {

        TextView fromAddress, toAddress, time;
        Button bookNowBtn;
        LinearLayout settingsLL;
        ImageView settingsBtn, deleteBtn;

        public CabHolder(@NonNull View itemView) {
            super(itemView);

            fromAddress = itemView.findViewById(R.id.tvFromCab);
            toAddress = itemView.findViewById(R.id.tvToCab);
            time = itemView.findViewById(R.id.tvTime);
            bookNowBtn = itemView.findViewById(R.id.bookNowBtn);
            settingsBtn = itemView.findViewById(R.id.settingsBtn);
            settingsLL = itemView.findViewById(R.id.settingsLL);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);


        }
    }
}
