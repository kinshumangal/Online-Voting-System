package com.example.votingpoll.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class FeedbackFragment extends Fragment {

    public FeedbackFragment() {    }
    RatingBar ratingBar;
    Button getRating;
    EditText description;
    FirebaseFirestore db;
    ViewFeedback viewFeedback;
    ImageView profile_img;
    StorageReference storageReference;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRating = view.findViewById(R.id.getRating);
        ratingBar = view.findViewById(R.id.rating);
        description = view.findViewById(R.id.editTextTextMultiLine);
        profile_img = view.findViewById(R.id.profile_image);
        fetchImage(Login.uidpass);
        db = FirebaseFirestore.getInstance();
        viewFeedback = new ViewFeedback();
        getRating.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String des = description.getText().toString();
            Toast.makeText(getActivity(), "Thank You", Toast.LENGTH_LONG).show();
            updateUserStar(rating,des);

        });
    }

    private void fetchImage(String s) {
        storageReference = FirebaseStorage.getInstance().getReference("images/" + s);
        try {
            File localfile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        profile_img.setImageBitmap(bitmap);

                    }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to retrieve", Toast.LENGTH_SHORT).show());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }
    void updateUserStar(float rating,String des){
        String uidpass = Login.uidpass;
        //int rating1 = Integer.parseInt(rating);
        if (uidpass != null) {
            DocumentReference update1 = db.collection("UserData").document(uidpass);
            //Update DB
            update1
                    .update("auRating", rating,
                            "auFeedbackDescription", des
                    )
                    .addOnSuccessListener(aVoid -> addDatatoFireStore(rating,des,uidpass)).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show());
            //To update Admin's database
        }

    }
    private void addDatatoFireStore(float ratings, String des,String uid) {
        // below 3 lines of code is used to set
        // data in our object class.
        viewFeedback.setViewRating(ratings);
        viewFeedback.setViewDescription(des);
        //DocumentReference newDB = db.collection("PollData").document();
        // Add a new document with a generated ID

        db.collection("FeedbackDB").document(uid)
                .set(viewFeedback).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Success...", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed...", Toast.LENGTH_SHORT).show());
    }
}