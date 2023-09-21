package com.example.votingadmin.handlingcandidates;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.votingadmin.AdminHomeActivity;
import com.example.votingpoll.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class EditCandidatesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private AddCandidatesClass userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_candidates);
        db = FirebaseFirestore.getInstance();
        userId = (AddCandidatesClass) getIntent().getSerializableExtra("userId");

        // Check if userId is available
        if (userId != null) {
            // Fetch user data and set up Save button click listener
            fetchUserData(userId);
            setUpSaveButton();
        } else {
            // Handle case when user ID is not available
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Fetch user data based on the provided user ID
    private void fetchUserData(AddCandidatesClass userId) {
        String id = userId.getaAadhaar();
        db.collection("PollData")
                .whereEqualTo("aAadhaar", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract user data from Firestore document
                            Map<String, Object> addPollClass1 = document.getData();
                            // Set retrieved data in EditText and TextView fields
                            EditText fullNameEditText = findViewById(R.id.editFullName);
                            TextView aadharEditText = findViewById(R.id.editAadhar);
                            fullNameEditText.setText(String.valueOf(addPollClass1.get("aFullname")));
                            aadharEditText.setText(String.valueOf(addPollClass1.get("aAadhaar")));
                        }
                    } else {
                        Toast.makeText(EditCandidatesActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Set up the Save button click listener
    private void setUpSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> {
            // Handle the Save button click to update the Firestore data
            onSaveButtonClick();
        });
    }

    // Update Firestore data with edited user data
    private void updateFirestoreData(AddCandidatesClass userId, String fullName, String aadhar) {
        // Use the userId to update the Firestore data
        AddCandidatesClass updatePoll = new AddCandidatesClass();
        updatePoll.setaAadhaar(aadhar);
        updatePoll.setaFullname(fullName);
        db.collection("PollData").document(userId.getaAadhaar())
                .set(updatePoll)
                .addOnSuccessListener(unused -> {
                    // On successful update, show a success message and navigate back to AdminHomeActivity
                    Toast.makeText(EditCandidatesActivity.this, "Successfully updated...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // On update failure, show an error message
                    Toast.makeText(EditCandidatesActivity.this, "Failed to update...", Toast.LENGTH_SHORT).show();
                });
    }

    // Handle the Save button click to update the Firestore data
    public void onSaveButtonClick() {
        // Get the updated data from the EditText and TextView fields
        String fullName = ((EditText)findViewById(R.id.editFullName)).getText().toString();
        String aadhar = ((TextView)findViewById(R.id.editAadhar)).getText().toString();
        if (userId != null) {
            updateFirestoreData(userId, fullName, aadhar); // Call the updateFirestoreData method to update data
        }
    }
}
