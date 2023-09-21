package com.example.votingadmin.handlingusers;

import android.os.Bundle;
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

public class ViewUser extends Fragment {

    private ArrayList<AddUserData> userList;
    private MyListAdapter myListAdapter;
    private FirebaseFirestore db;
    private ProgressBar loadingPB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_user, container, false);
        RecyclerView voteRV = view.findViewById(R.id.your_recycler_view_id1);
        loadingPB = view.findViewById(R.id.idProgressBar);

        // Initializing Firestore
        db = FirebaseFirestore.getInstance();

        // Initializing the user data list
        userList = new ArrayList<>();

        voteRV.setHasFixedSize(true);
        voteRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initializing the adapter with the user data list
        myListAdapter = new MyListAdapter(getContext(), userList);
        voteRV.setAdapter(myListAdapter);

        // Fetch user data from Firestore
        fetchUserDataFromFirestore();

        return view;
    }

    private void fetchUserDataFromFirestore() {
        loadingPB.setVisibility(View.VISIBLE);

        db.collection("AddUser").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    loadingPB.setVisibility(View.GONE);
                    userList.clear(); // Clear existing data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        AddUserData userData = document.toObject(AddUserData.class);
                        if (userData != null) {
                            userData.setauAadhaar(document.getId());
                            userList.add(userData);
                        }
                    }
                    myListAdapter.notifyDataSetChanged(); // Notify adapter of data change
                })
                .addOnFailureListener(e -> {
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Failed to get data.", Toast.LENGTH_SHORT).show();
                });
    }
}
