package com.codinglegend.legendsp.cabme.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codinglegend.legendsp.cabme.R;
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

        holder.bookNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkCab(list.get(position).getCabId());
                
            }
        });

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

        public CabHolder(@NonNull View itemView) {
            super(itemView);

            fromAddress = itemView.findViewById(R.id.tvFromCab);
            toAddress = itemView.findViewById(R.id.tvToCab);
            time = itemView.findViewById(R.id.tvTime);
            bookNowBtn = itemView.findViewById(R.id.bookNowBtn);


        }
    }
}
