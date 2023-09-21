package com.example.votingadmin.handlingpoll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingadmin.AdminHomeActivity;
import com.example.votingpoll.R;

import java.util.List;

public class AdapterContest extends RecyclerView.Adapter<AdapterContest.ViewHolder> {
    private final List<ContestClass> listdata;
    private final Context context;

    // Constructor to set the OnItemClickListener
    public AdapterContest(Context context, List<ContestClass> listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ContestClass serverData = listdata.get(position);
        Log.d("Aka", "onBindViewHolder");

        // Set data to the views in the RecyclerView item
        holder.fullname.setText("Poll Name: " + serverData.getConName());
        holder.aadhar.setText("Poll ID: " + serverData.getConId());
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return listdata.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView fullname;
        private final TextView aadhar;

        public ViewHolder(View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.rv_Name);
            aadhar = itemView.findViewById(R.id.rv_aadhar);

            // Set an OnClickListener to handle item clicks in the RecyclerView
            itemView.setOnClickListener(view -> {
                // Get the clicked contest data
                ContestClass serverData = listdata.get(getAdapterPosition());
                // Cast the context to AdminHomeActivity and call the showDeletePopup method
                AdminHomeActivity adminHomeActivity = (AdminHomeActivity) context;
                adminHomeActivity.showDeletePopup(serverData);
            });
        }
    }
}
