package com.example.votingadmin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
public class AdminProfileFragment extends Fragment {
    private TextView proName, proEmail, proMobile, proAddress, proAadhar;
    private Button proEdit;
    private ImageView profileImg;
    private FirebaseFirestore db;
    private String uID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
        initializeViews(view);
        setupProfileEditButton();
        fetchAdminData();
        fetchProfileImage();
        return view;
    }

    private void initializeViews(View view) {
        proAadhar = view.findViewById(R.id.pAadhar1);
        proAddress = view.findViewById(R.id.pAddress);
        proEmail = view.findViewById(R.id.pEmail);
        proMobile = view.findViewById(R.id.pmobile);
        proName = view.findViewById(R.id.pname);
        proEdit = view.findViewById(R.id.pEdit);
        profileImg = view.findViewById(R.id.profile_image);
        db = FirebaseFirestore.getInstance();
        uID = AdminLogin.logID;
    }

    private void setupProfileEditButton() {
        proEdit.setOnClickListener(view -> {
            AdminProfileFragmentEdit adminProfileFragmentEdit = new AdminProfileFragmentEdit();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.framelayoutContainer23, adminProfileFragmentEdit)
                    .commit();
        });
    }

    private void fetchAdminData() {
        DocumentReference docRef = db.collection("AdminData").document(uID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                AdminAddedData adminData = documentSnapshot.toObject(AdminAddedData.class);
                updateUI(adminData);
            } else {
                Log.d("AdminProfileFragment", "No such document");
            }
        }).addOnFailureListener(e -> Log.e("AdminProfileFragment", "Error fetching admin data", e));
    }

    private void updateUI(AdminAddedData adminData) {
        if (adminData != null) {
            proName.setText(adminData.getaFullname());
            proMobile.setText(String.valueOf(adminData.getaMobile()));
            proEmail.setText(adminData.getaEmail());
            proAadhar.setText(String.valueOf(adminData.getaAadhaar()));
            proAddress.setText(adminData.getaAddress());
        } else {
            Log.d("AdminProfileFragment", "Admin data is null");
        }
    }

    private void fetchProfileImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + uID);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        profileImg.setImageBitmap(bitmap);
                    }).addOnFailureListener(e -> Log.e("AdminProfileFragment", "Failed to retrieve profile image", e));
        } catch (IOException e) {
            Log.e("AdminProfileFragment", "Failed to create temp file for image", e);
        }
    }
}
