package com.example.votingpoll.candidate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.votingadmin.ViewFeedback;
import com.example.votingpoll.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class CandiFeedbackFragment extends Fragment {

    RatingBar ratingBar;
    Button getRating;
    EditText description;
    FirebaseFirestore db;
    ViewFeedback viewFeedback;
    ImageView profile_img;
    StorageReference storageReference;
    Uri imageUri;

    public CandiFeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_candi_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        getRating = view.findViewById(R.id.getRating);
        ratingBar = view.findViewById(R.id.rating);
        description = view.findViewById(R.id.editTextTextMultiLine);
        profile_img = view.findViewById(R.id.profile_image);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch and display the candidate's image
        fetchImage(CandiLogin.cidpass);

        // Initialize ViewFeedback object
        viewFeedback = new ViewFeedback();

        // Handle the feedback submission
        getRating.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String des = description.getText().toString();
            if (TextUtils.isEmpty(des)) {
                description.setError("Enter Description!");
            } else {
                updateUserStar(rating, des);
            }
        });
    }

    // Update candidate's rating and feedback in Firestore
    private void updateUserStar(float rating, String des) {
        String id = CandiLogin.cidpass;
        if (id != null) {
            DocumentReference update1 = db.collection("CandiData").document(id);
            // Update DB
            update1.update("aucRating", rating, "aucFeedback", des)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                        // Add data to FeedbackDB collection
                        addDataToFirestore(rating, des, id);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show());
        }
    }

    // Add data to the FeedbackDB collection
    private void addDataToFirestore(float ratings, String des, String uid) {
        viewFeedback.setViewRating(ratings);
        viewFeedback.setViewDescription(des);

        db.collection("FeedbackDB").document(uid)
                .set(viewFeedback)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Feedback submitted.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to submit feedback.", Toast.LENGTH_SHORT).show());
    }

    // Fetch and display the candidate's image from Firebase Storage
    private void fetchImage(String s) {
        storageReference = FirebaseStorage.getInstance().getReference("images/" + s);
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        profile_img.setImageBitmap(bitmap);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to retrieve image", Toast.LENGTH_SHORT).show());

        } catch (IOException e) {
            Log.e("CandiFeedbackFragment", "Error fetching image", e);
        }
    }
}
