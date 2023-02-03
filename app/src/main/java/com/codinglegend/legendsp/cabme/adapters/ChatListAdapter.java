package com.codinglegend.legendsp.cabme.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.activities.ChatsActivity;
import com.codinglegend.legendsp.cabme.models.ChatListModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

    private List<ChatListModel> list;
    private Context context;
    String activityName;

    public ChatListAdapter(List<ChatListModel> list, Context context, String activityName) {
        this.list = list;
        this.context = context;
        this.activityName = activityName;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_chat_list,parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {


        holder.tvName.setText(list.get(position).getFullName());
        holder.position.setText(list.get(position).getUserName());
        Glide.with(context).load(list.get(position).getProfilePic()).into(holder.profile);

       holder.ll.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               context.startActivity(new Intent(context, ChatsActivity.class)
                       .putExtra("username", list.get(position).getUserName()));
           }
       });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private TextView tvName, position;
        private CircleImageView profile;
        private LinearLayout ll;

        public Holder(@NonNull View itemView) {
            super(itemView);

            position = itemView.findViewById(R.id.tv_desc);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
            ll = itemView.findViewById(R.id.chatListLL);

        }
    }
}
