package com.example.votingadmin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingpoll.R;
import com.example.votingpoll.candidate.CandiData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewResultsFragment extends Fragment {
    private final List<DocumentSnapshot> candidates = new ArrayList<>();
    private ResultsAdapter resultsAdapter;
    // Assuming you have a ResultsAdapter that can work with Winner objects
    private final List<CandiData> winners = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_results, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.your_recycler_view_id_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultsAdapter = new ResultsAdapter(winners);  // Update the adapter to work with winners
        recyclerView.setAdapter(resultsAdapter);
        retrieveAndSortCandidates();
        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void retrieveAndSortCandidates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("CandiData")
                .orderBy("voteCountCandi", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        candidates.clear();
                        candidates.addAll(task.getResult().getDocuments());
                        // Sort candidates based on votes (Descending order)
                        candidates.sort((doc1, doc2) -> {
                            Long votes1 = doc1.getLong("voteCountCandi");
                            Long votes2 = doc2.getLong("voteCountCandi");
                            if (votes1 != null && votes2 != null) {
                                return votes2.compareTo(votes1);
                            } else {
                                return 0;
                            }
                        });

                        // Clear the winners list and populate it with the top 3 candidates
                        winners.clear();
                        for (int i = 0; i < Math.min(3, candidates.size()); i++) {
                            DocumentSnapshot candidate = candidates.get(i);
                            String name = candidate.getString("aucFullname");
                            int voteCount = Objects.requireNonNull(candidate.getLong("voteCountCandi")).intValue();
                            String party = candidate.getString("partyName");
                            winners.add(new CandiData(name, voteCount, party));
                        }

                        resultsAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Aka", "Not Fetching...");
                    }
                });
    }

}
