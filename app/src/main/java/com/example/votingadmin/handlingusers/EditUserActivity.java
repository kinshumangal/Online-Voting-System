package com.example.votingadmin.handlingusers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.votingadmin.AdminHomeActivity;
import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EditUserActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private AddUserData userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        db = FirebaseFirestore.getInstance();
        userId = (AddUserData) getIntent().getSerializableExtra("userId1");
        if (userId != null) {
            fetchUserData(userId); // Fetch user data using the provided userId
            setupSaveButton();
        } else {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchUserData(AddUserData userId) {
        String id = userId.getAuAadhaar();
        db.collection("AddUser")
                .whereEqualTo("auAadhaar", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> userData = document.getData();
                            assert userData != null;
                            setUserDataInViews(userData); // Update the UI with fetched user data
                        }
                    } else {
                        Toast.makeText(EditUserActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUserDataInViews(Map<String, Object> userData) {
        EditText fullNameEditText = findViewById(R.id.editFullName);
        TextView aadharEditText = findViewById(R.id.editAadhar);

        // Update UI fields with user data
        fullNameEditText.setText(String.valueOf(userData.get("auFullname")));
        aadharEditText.setText(String.valueOf(userData.get("auAadhaar")));
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> onSaveButtonClick());
    }

    private void updateFirestoreData(AddUserData userId, String fullName, String aadhar) {
        // Create a new AddUserData instance with updated fields
        AddUserData updatedUserData = new AddUserData();
        updatedUserData.setauAadhaar(aadhar);
        updatedUserData.setauFullname(fullName);

        // Update the Firestore document with the new data
        db.collection("AddUser").document(userId.getAuAadhaar())
                .set(updatedUserData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(EditUserActivity.this, "Successfully updated...", Toast.LENGTH_SHORT).show();
                    navigateToAdminHomeActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(EditUserActivity.this, "Failed to update...", Toast.LENGTH_SHORT).show());
    }

    public void onSaveButtonClick() {
        String fullName = ((EditText) findViewById(R.id.editFullName)).getText().toString();
        String aadhar = ((TextView) findViewById(R.id.editAadhar)).getText().toString();

        if (userId != null) {
            updateFirestoreData(userId, fullName, aadhar);
        }
    }

    private void navigateToAdminHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(intent);
    }
}
