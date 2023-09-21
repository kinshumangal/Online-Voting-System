package com.example.votingadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTermsAndConditionsFragment extends Fragment {
    private EditText editText;
    private Button addButton;
    private FirebaseFirestore db;
    private TermsAndConditions termsAndConditions;

    public AddTermsAndConditionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupButton();

        // Initializing Firestore
        db = FirebaseFirestore.getInstance();
        termsAndConditions = new TermsAndConditions();
    }

    private void initializeViews(View view) {
        editText = view.findViewById(R.id.editTextTextMultiLine);
        addButton = view.findViewById(R.id.termsandconditionsbutton);
    }

    private void setupButton() {
        addButton.setOnClickListener(v -> {
            String terms = editText.getText().toString();
            editText.setText("");
            addDataToFirestore(terms);
        });
    }

    private void addDataToFirestore(String terms) {
        // Set data in the TermsAndConditions object
        termsAndConditions.setTcData(terms);

        // Add or update the terms and conditions document in Firestore
        db.collection("termsData").document("terms")
                .set(termsAndConditions)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Success...", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed...", Toast.LENGTH_SHORT).show());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_terms_and_conditions, container, false);
    }
}
