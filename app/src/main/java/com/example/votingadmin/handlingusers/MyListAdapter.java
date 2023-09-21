package com.example.votingadmin.handlingusers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingpoll.R;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final List<AddUserData> listdata;
    private final Context context;

    public MyListAdapter(Context context, List<AddUserData> listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AddUserData userData = listdata.get(position);

        // Bind user data to the views
        holder.bindUserData(userData);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    // ViewHolder class to hold and bind views
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView fullname;
        private final TextView aadhar;

        public ViewHolder(View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.rv_Name);
            aadhar = itemView.findViewById(R.id.rv_aadhar);

            // Set click listener to open EditUserActivity when a list item is clicked
            itemView.setOnClickListener(view -> {
                AddUserData userData = listdata.get(getAdapterPosition());
                navigateToEditUserActivity(userData);
            });
        }

        // Bind user data to the views
        public void bindUserData(AddUserData userData) {
            fullname.setText("Name: " + userData.getAuFullname());
            aadhar.setText("Aadhar: " + userData.getAuAadhaar());
        }

        // Navigate to EditUserActivity
        private void navigateToEditUserActivity(AddUserData userData) {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("userId1", userData);
            context.startActivity(intent);
        }
    }
}
