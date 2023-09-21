package com.example.votingadmin.handlingcandidates;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCandidates extends Fragment {
    private static final int MIN_NAME_LENGTH = 3;
    private static final String COLLECTION_NAME = "PollData";

    private EditText candidateNameEditText, candidateAadhaarEditText;
    private AddCandidatesClass addCandidatesClass;
    private FirebaseFirestore db;

    public AddCandidates() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_candidates, container, false);

        // Find views and buttons
        candidateNameEditText = view.findViewById(R.id.pollname);
        candidateAadhaarEditText = view.findViewById(R.id.pollaadhar);
        Button addButton = view.findViewById(R.id.polladd);
        Button viewButton = view.findViewById(R.id.pollView);
        Button cancelButton = view.findViewById(R.id.pollcancel);

        // Initialize Firebase Firestore
        addCandidatesClass = new AddCandidatesClass();
        db = FirebaseFirestore.getInstance();

        // Set up the "Add" button click listener
        addButton.setOnClickListener(view1 -> addCandidate());

        // Set up the "View" button click listener
        viewButton.setOnClickListener(view12 -> {
            // Show the candidates' view
            ViewCandidates vUser = new ViewCandidates();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.viewfragmentContainer4, vUser)
                    .commit();
        });

        // Set up the "Cancel" button click listener
        cancelButton.setOnClickListener(view13 -> clearFields());

        return view;
    }

    // Method to handle adding a candidate
    private void addCandidate() {
        String aadhar = candidateAadhaarEditText.getText().toString();
        String fullname = candidateNameEditText.getText().toString();

        // Validate candidate name
        if (TextUtils.isEmpty(fullname) || fullname.length() < MIN_NAME_LENGTH) {
            candidateNameEditText.setError("Invalid name!!!");
        } else if (TextUtils.isEmpty(aadhar)) {
            // Validate Aadhaar number
            candidateAadhaarEditText.setError("Invalid Aadhaar no.!!!");
        } else {
            // If valid, add candidate data to Firestore
            addDataToFirestore(fullname, aadhar);
        }
    }

    // Method to add candidate data to Firestore
    private void addDataToFirestore(String fname, String aadhar) {
        // Set candidate data
        addCandidatesClass.setaFullname(fname);
        addCandidatesClass.setaAadhaar(aadhar);

        // Add data to Firestore
        db.collection(COLLECTION_NAME).document(aadhar)
                .set(addCandidatesClass)
                .addOnSuccessListener(unused -> {
                    // On success, clear input fields and show success toast
                    clearFields();
                    showToast("Success...");
                })
                .addOnFailureListener(e -> {
                    // On failure, show failure toast
                    showToast("Failed...");
                });
    }

    // Method to clear input fields
    private void clearFields() {
        candidateNameEditText.setText("");
        candidateAadhaarEditText.setText("");
    }

    // Method to show a toast message
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
