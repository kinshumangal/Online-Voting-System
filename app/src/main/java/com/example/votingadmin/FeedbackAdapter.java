package com.example.votingadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingadmin.handlingcandidates.EditCandidatesActivity;
import com.example.votingpoll.R;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private final List<ViewFeedback> listdata;
    private final Context context;

    public FeedbackAdapter(Context context, List<ViewFeedback> listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder
        final ViewFeedback viewFeedback = listdata.get(position);
        holder.rating.setText("Rating: " + viewFeedback.getViewRating());
        holder.description.setText("Description: " + viewFeedback.getViewDescription());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    // View holder class to hold the item view elements
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rating, description;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize views within the item layout
            this.rating = itemView.findViewById(R.id.rv_Name);
            this.description = itemView.findViewById(R.id.rv_aadhar);

            // Handle item click to open EditCandidatesActivity
            itemView.setOnClickListener(view -> {
                // Start EditCandidatesActivity when the item is clicked
                Intent intent = new Intent(context, EditCandidatesActivity.class);
                context.startActivity(intent);
            });
        }
    }
}

