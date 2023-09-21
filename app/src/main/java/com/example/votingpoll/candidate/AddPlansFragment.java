package com.example.votingpoll.candidate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddPlansFragment extends Fragment {
    private String plans;
    private EditText plansEditText, partySelect;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_plans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        plansEditText = view.findViewById(R.id.addPlans123);
        Button addPlansButton = view.findViewById(R.id.addPlansBtn);
        partySelect = view.findViewById(R.id.partySelect);
        imageView = view.findViewById(R.id.imageViewParty);

        // Handle image selection
        imageView.setOnClickListener(v -> selectImage());

        // Get the candidate's aadhar
        String aadhar = CandiLogin.cidpass;
        db = FirebaseFirestore.getInstance();

        // Fetch candidate data from Firestore
        fetchCandidateData(aadhar);

        // Handle adding plans
        addPlansButton.setOnClickListener(view1 -> {
            String party = partySelect.getText().toString();
            plans = plansEditText.getText().toString();
            if (TextUtils.isEmpty(party) || TextUtils.isEmpty(plans)) {
                partySelect.setError("Enter Party!!!");
                plansEditText.setError("Enter Manifesto!!!");
            } else {
                try {
                    uploadImage(aadhar);
                    updateData(plans, aadhar, party);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Please upload Image...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            assert data != null;
            if (data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    private void uploadImage(String id) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();
        id = id + "Party";
        storageReference = FirebaseStorage.getInstance().getReference("Party_Images/" + id);
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageView.setImageURI(null);
            Toast.makeText(getActivity(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateData(String plans, String aadhar, String party) {
        if (aadhar != null) {
            // Use the userId to update the Firestore data
            DocumentReference update1 = db.collection("CandiData").document(aadhar);
            // Update DB
            update1.update("aucPlans", plans, "partyName", party)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchCandidateData(String cid) {
        DocumentReference docRef = db.collection("CandiData").document(cid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    CandiData c = document.toObject(CandiData.class);
                    assert c != null;
                    plansEditText.setText(c.getAucPlans());
                } else {
                    Log.d("Akash", "No Data");
                }
            } else {
                Log.d("Akash", "get failed with ", task.getException());
            }
        });
    }
}
