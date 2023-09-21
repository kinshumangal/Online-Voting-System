package com.example.votingadmin.handlingpoll;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.votingadmin.AdminHomeActivity;
import com.example.votingpoll.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddContest extends Fragment {
    private EditText contestName, contestID;
    private Button addContest;
    private FirebaseFirestore db;
    private ContestClass contestClass;
    private String id, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contest, container, false);
        contestName = view.findViewById(R.id.ContestID1);
        contestID = view.findViewById(R.id.contestName1);
        addContest = view.findViewById(R.id.addContest);

        contestClass = new ContestClass();
        db = FirebaseFirestore.getInstance();

        addContest.setOnClickListener(view1 -> {
            id = contestID.getText().toString();
            name = contestName.getText().toString();
            addDataToFirestore(id, name);
        });

        return view;
    }

    private void addDataToFirestore(String idData, String name) {
        contestClass.setConName(name);
        contestClass.setConId(idData);

        db.collection("contestData").document(idData)
                .set(contestClass)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getActivity(), "Success...", Toast.LENGTH_SHORT).show();
                    clearFields();
                    navigateToAdminHomeActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed...", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        contestName.setText("");
        contestID.setText("");
    }

    private void navigateToAdminHomeActivity() {
        Intent intent = new Intent(getContext(), AdminHomeActivity.class);
        startActivity(intent);
    }

    public AddContest() {
        // Required empty public constructor
    }
}
