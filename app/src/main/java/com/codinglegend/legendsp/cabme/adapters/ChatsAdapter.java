package com.codinglegend.legendsp.cabme.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codinglegend.legendsp.cabme.R;
import com.codinglegend.legendsp.cabme.models.ChatsModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatHolder> {

    List<ChatsModel> list;
    Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userName;

    public ChatsAdapter(List<ChatsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setList(List<ChatsModel> list){

        this.list = list;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ChatHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_itm_right, parent, false);
            return new ChatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, @SuppressLint("RecyclerView") int position) {

        sp = context.getSharedPreferences("FILE_NAME", Context.MODE_PRIVATE);
        editor = sp.edit();

        userName = sp.getString("userName", null);

         if (list.get(position).getType().equals("TEXT")){

            holder.textMsg.setVisibility(View.VISIBLE);
            holder.textMsg.setText(list.get(position).getTextMsg());

        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView textMsg, downloadSize, pdfName;
        CardView imageView;
        ImageView image;
        LinearLayout downloadBtn;
        ImageView blurImage;
        CardView documentBtn;
        CircleImageView pdfDownloadBtn;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            textMsg = itemView.findViewById(R.id.tv_msg);
//            imageView = itemView.findViewById(R.id.card_image);
//            image = itemView.findViewById(R.id.msgSendImage);
//            downloadBtn = itemView.findViewById(R.id.llDownalod);
//            blurImage = itemView.findViewById(R.id.blurImage);
//            downloadSize = itemView.findViewById(R.id.downlaod_txt);
//            documentBtn = itemView.findViewById(R.id.card_pdf);
//            pdfDownloadBtn = itemView.findViewById(R.id.downloadBtn);
//            pdfName = itemView.findViewById(R.id.pdfName);

        }
    }

    @Override
    public int getItemViewType(int position) {

        sp = context.getSharedPreferences("FILE_NAME", Context.MODE_PRIVATE);
        String userName = sp.getString("userName", null);

        if (list.get(position).getSender().equals(userName)){
            return 1;
        }else {
            return 0;
        }

    }

}
