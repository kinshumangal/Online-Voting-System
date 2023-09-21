package com.example.votingadmin.handlingusers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.votingadmin.TermsAndConditions;
import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewTermsAndConditionFragment extends Fragment {

    private FirebaseFirestore db;
    private ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_terms_and_condition, container, false);
        scrollView = view.findViewById(R.id.TextTextMultiLine);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch and display terms and conditions data
        fetchAndDisplayTerms();

        return view;
    }

    private void fetchAndDisplayTerms() {
        // Reference to the "termsData" document in Firestore
        DocumentReference docRef = db.collection("termsData").document("terms");

        // Fetch the document data
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Convert the document data to the TermsAndConditions object
                    TermsAndConditions terms = document.toObject(TermsAndConditions.class);
                    // Display the terms and conditions data
                    displayTerms(terms);
                } else {
                    // Document does not exist
                    Log.d("ViewTerms", "No such document");
                }
            } else {
                // Fetching data failed
                Log.d("ViewTerms", "get failed with ", task.getException());
            }
        });
    }

    private void displayTerms(TermsAndConditions terms) {
        if (terms != null) {
            // Create a TextView to display terms and conditions data
            TextView textView = new TextView(getContext());
            textView.setText(terms.getTcData());

            // Add the TextView containing terms and conditions data to the ScrollView
            scrollView.addView(textView);
        } else {
            // TermsAndConditions object is null
            Log.d("ViewTerms", "TermsAndConditions object is null");
        }
    }
}
