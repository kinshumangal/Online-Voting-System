package com.example.votingadmin.handlingcandidates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewCandidates extends Fragment {

    private ArrayList<AddCandidatesClass> addPollClasses;
    private MyListAdapter1 myListAdapter1;
    private FirebaseFirestore db;
    private ProgressBar loadingPB;

    public ViewCandidates() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_candidates, container, false);
        RecyclerView voteRV = view.findViewById(R.id.your_recycler_view_id2);
        loadingPB = view.findViewById(R.id.idProgressBar);

        // Initializing our variable for Firestore and getting its instance
        db = FirebaseFirestore.getInstance();
        Log.d("Aka", "getting view");

        // Creating our new array list
        addPollClasses = new ArrayList<>();

        voteRV.setHasFixedSize(true);
        voteRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Adding our array list
        myListAdapter1 = new MyListAdapter1(getContext(), addPollClasses);
        voteRV.setAdapter(myListAdapter1); // Setting the adapter to the RecyclerView

        // Fetch and display user data from Firestore
        fetchUserDataFromFirestore();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUserDataFromFirestore() {
        loadingPB.setVisibility(View.VISIBLE);

        db.collection("PollData").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        loadingPB.setVisibility(View.GONE);
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            AddCandidatesClass c = d.toObject(AddCandidatesClass.class);
                            if (c != null) {
                                // Set the Firestore document ID (Aadhaar number) as a unique identifier
                                c.setaAadhaar(d.getId());
                                addPollClasses.add(c);
                            }
                        }
                        myListAdapter1.notifyDataSetChanged();
                    } else {
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Failed to get the data.", Toast.LENGTH_SHORT).show();
                });
    }
}
