package com.example.votingadmin;

import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminProfileFragmentEdit extends Fragment {
    private EditText proName, proEmail, proMobile, proAddress;
    private TextView proAadhar;
    private Button proEdit;
    private ImageView profileImg;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String uID;

    public AdminProfileFragmentEdit() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile_edit, container, false);
        initializeViews(view);
        fetchAdminData(uID);
        fetchProfileImage(uID);

        proEdit.setOnClickListener(view1 -> updateData());

        return view;
    }

    private void initializeViews(View view) {
        proAadhar = view.findViewById(R.id.pAadhar1);
        proAddress = view.findViewById(R.id.pAddress);
        proEmail = view.findViewById(R.id.pEmail);
        proMobile = view.findViewById(R.id.pmobile);
        proName = view.findViewById(R.id.pname);
        proEdit = view.findViewById(R.id.pEdit);
        db = FirebaseFirestore.getInstance();
        profileImg = view.findViewById(R.id.profile_image);
        uID = AdminLogin.logID;
    }

    private void fetchAdminData(String id) {
        DocumentReference docRef = db.collection("AdminData").document(id);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    AdminAddedData adminData = document.toObject(AdminAddedData.class);
                    updateUI(adminData);
                } else {
                    Log.d("AdminProfileEdit", "No such document");
                }
            } else {
                Log.d("AdminProfileEdit", "get failed with ", task.getException());
            }
        });
    }

    private void updateUI(AdminAddedData adminData) {
        if (adminData != null) {
            proName.setText(adminData.getaFullname());
            proMobile.setText(String.valueOf(adminData.getaMobile()));
            proEmail.setText(adminData.getaEmail());
            proAadhar.setText(String.valueOf(adminData.getaAadhaar()));
            proAddress.setText(adminData.getaAddress());
        } else {
            Log.d("AdminProfileEdit", "Admin data is null");
        }
    }

    private void fetchProfileImage(String s) {
        storageReference = FirebaseStorage.getInstance().getReference("images/" + s);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        profileImg.setImageBitmap(bitmap);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to retrieve", Toast.LENGTH_SHORT).show();
                        Log.e("AdminProfileEdit", "Failed to retrieve profile image", e);
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateData() {
        String fullName = proName.getText().toString();
        String email = proEmail.getText().toString();
        long mobile = Long.parseLong(proMobile.getText().toString());
        String address = proAddress.getText().toString();
        String aadhar = proAadhar.getText().toString();

        // Use the userId (aadhar) to update the Firestore data
        DocumentReference update1 = db.collection("AdminData").document(aadhar);

        // Prepare the updates as a map
        Map<String, Object> updates = new HashMap<>();
        updates.put("aFullname", fullName);
        updates.put("aEmail", email);
        updates.put("aAddress", address);
        updates.put("aMobile", mobile);

        // Update the document
        update1.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                    Log.d("AdminProfileEdit", "Document successfully updated!");
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_SHORT).show();
                    Log.e("AdminProfileEdit", "Error updating document", e);
                });
    }
}
