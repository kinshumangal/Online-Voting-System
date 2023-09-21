package com.example.votingpoll.user;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingpoll.R;

public class VotingPageMyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView,imageView1;
    TextView textView,textView1;
    LinearLayout linearLayout;

    public VotingPageMyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageview);
        textView=itemView.findViewById(R.id.name);
        imageView1=itemView.findViewById(R.id.imageview1);
        textView1=itemView.findViewById(R.id.partyname);
        linearLayout=itemView.findViewById(R.id.main_container);

    }
}