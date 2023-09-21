package com.example.votingadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingpoll.R;
import com.example.votingpoll.candidate.CandiData;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.WinnerViewHolder> {
    private final List<CandiData> winners;

    public ResultsAdapter(List<CandiData> winners) {
        this.winners = winners;
    }

    @NonNull
    @Override
    public WinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate, parent, false);
        return new WinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WinnerViewHolder holder, int position) {
        CandiData winner = winners.get(position);
        holder.winnerNameTextView.setText(winner.getAucFullname());
        holder.winnerVoteCountTextView.setText(String.valueOf(winner.getVoteCountCandi()));
        holder.winnerPartyName.setText(winner.getPartyName());
    }

    @Override
    public int getItemCount() {
        return winners.size();
    }

    static class WinnerViewHolder extends RecyclerView.ViewHolder {
        TextView winnerNameTextView;
        TextView winnerVoteCountTextView;
        TextView winnerPartyName;
        WinnerViewHolder(View itemView) {
            super(itemView);
            winnerNameTextView = itemView.findViewById(R.id.winnerNameTextView);
            winnerVoteCountTextView = itemView.findViewById(R.id.winnerVoteCountTextView);
            winnerPartyName = itemView.findViewById(R.id.winnerPartyTextView);
        }
    }
}
