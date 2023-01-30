package com.codinglegend.legendsp.cabme.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.codinglegend.legendsp.cabme.common;
import com.codinglegend.legendsp.cabme.models.NotificationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private Context context;
    private List<NotificationModel> list;

    ProgressDialog progressDialog;

    String updateRequestApi = common.getBaseUrl() + "UpdateCabRequest.php";

//    String acceptFriendRequest = Common.getBaseUrl() + "acceptFriendRequest.php";
//    String friendRequestAccept = Common.getBaseUrl() + "friendRequestAccept.php";
//    String declineFriendRequest = Common.getBaseUrl() + "declineFriendRequest.php";
//    String disableNotification = Common.getBaseUrl() + "disableFriendrequest.php";

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_notification, parent, false);

        return new NotificationHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {

        sp = context.getSharedPreferences("FILE_NAME", Context.MODE_PRIVATE);
        editor = sp.edit();

        progressDialog = new ProgressDialog(context);

        if (!list.get(position).getIsDisabled().equalsIgnoreCase("yes")) {

            holder.senderUsername.setText(list.get(position).getFromUsername());
            holder.content.setText(list.get(position).getContent());
//
//            String timeDateSplits[] = list.get(position).getTimeDate().split("xxx");
//
//            holder.timeDate.setText(timeDateSplits[0] + " " + timeDateSplits[1]);
            holder.timeDate.setVisibility(View.GONE);

            if (list.get(position).getType().equalsIgnoreCase(common.cabRequest)) {

                holder.acceptDeclineLL.setVisibility(View.VISIBLE);

                holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        changeCabRequest(list.get(position).getId(), common.cabRequestAccept);

                        holder.acceptDeclineLL.setVisibility(View.GONE);
                        holder.freindAcceptText.setVisibility(View.VISIBLE);
                        holder.content.setText("Cab Request");

//                        freindRequestAccepted(common.getUserName(context), list.get(position).getFromUsername(), holder);

                    }
                });

                holder.declineBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new AlertDialog.Builder(context)
                                .setTitle("Friend Request")
                                .setMessage("Do you really want to decline freind request?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        holder.acceptDeclineLL.setVisibility(View.GONE);
                                        holder.freindDeclineText.setVisibility(View.VISIBLE);
                                        changeCabRequest(list.get(position).getId(), common.cabRequestDecline);
                                        holder.content.setText("Cab Request");
//                                        declineFriendRequest(list.get(position).getFromUsername());
                                    }
                                }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();

                    }
                });

            } else if (list.get(position).getType().equalsIgnoreCase(common.cabRequestAccept)) {

                holder.freindAcceptText.setVisibility(View.VISIBLE);
                holder.acceptDeclineLL.setVisibility(View.GONE);

            } else if (list.get(position).getType().equalsIgnoreCase(common.cabRequestDecline)) {

                holder.acceptDeclineLL.setVisibility(View.GONE);
                holder.freindDeclineText.setVisibility(View.VISIBLE);

            } else {
                holder.acceptDeclineLL.setVisibility(View.GONE);
                holder.freindAcceptText.setVisibility(View.GONE);
                holder.freindDeclineText.setVisibility(View.GONE);
            }

            holder.removeNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    list.remove(position);
//                    NotificationFragment.getNewNotification(list);
//
//                    disableNotification(list.get(position).getFromUsername(), list.get(position).getTimeDate(), list.get(position).getType());

                }
            });

        }

    }

    private void changeCabRequest(String id, String cabRequestAccept) {

        StringRequest request = new StringRequest(Request.Method.POST, updateRequestApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("failed") || response.equalsIgnoreCase("failed")){
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
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
                Map<String, String> map =new HashMap<>();

                map.put("cab_id", id);
                map.put("status", cabRequestAccept);

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

    public class NotificationHolder extends RecyclerView.ViewHolder {

        TextView content, timeDate, senderUsername, freindAcceptText, freindDeclineText, acceptBtn, declineBtn;
        LinearLayout removeNotification, fullLayout, acceptDeclineLL;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.tv_content);
            timeDate = itemView.findViewById(R.id.tv_notification_datetime);
            senderUsername = itemView.findViewById(R.id.tv_notification_username);
            removeNotification = itemView.findViewById(R.id.llRemoveNotification);
            fullLayout = itemView.findViewById(R.id.notificationLL);
            freindAcceptText = itemView.findViewById(R.id.tv_friendrequest_accepted);
            freindDeclineText = itemView.findViewById(R.id.tv_friendrequest_declined);
            acceptBtn = itemView.findViewById(R.id.tv_accept_freindrequest);
            declineBtn = itemView.findViewById(R.id.tv_decline_friendrequest);
            acceptDeclineLL = itemView.findViewById(R.id.accept_decline_ll);

        }
    }
}
