package com.example.votingadmin.handlingcandidates;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.votingpoll.R;
public class MyListAdapter1 extends RecyclerView.Adapter<MyListAdapter1.ViewHolder>{
    private final List<AddCandidatesClass> listdata;
    private final Context context;
    // RecyclerView recyclerView;
    public MyListAdapter1(Context context, List<AddCandidatesClass> listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyListAdapter1.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyListAdapter1.ViewHolder holder, int position) {
        final AddCandidatesClass addCandidatesClass = listdata.get(position);
        holder.fullname.setText("Name :"+ addCandidatesClass.getaFullname());
        holder.aadhar.setText("Aadhar :"+ addCandidatesClass.getaAadhaar());

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullname,aadhar;
        public ViewHolder(View itemView) {
            super(itemView);
            this.fullname = itemView.findViewById(R.id.rv_Name);
            this.aadhar = itemView.findViewById(R.id.rv_aadhar);

            itemView.setOnClickListener(view -> {
                AddCandidatesClass addCandidatesClass = listdata.get(getAdapterPosition());
                Intent intent = new Intent(context, EditCandidatesActivity.class);
                Log.d("Akash", addCandidatesClass.getaAadhaar());
                intent.putExtra("userId", addCandidatesClass);
                context.startActivity(intent);
            });
        }
    }
}
