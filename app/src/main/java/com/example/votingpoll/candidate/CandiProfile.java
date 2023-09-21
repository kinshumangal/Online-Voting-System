package com.example.votingpoll.candidate;

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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class CandiProfile extends Fragment {
    TextView proName,proEmail,proMobile,proAddress;
    TextView proAadhar;
    Button proEdit;
    FirebaseFirestore db;
    ImageView profile_img;
    StorageReference storageReference;
    public CandiProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_candi_profile, container, false);
        proAadhar = view.findViewById(R.id.pAadhar1);
        proAddress = view.findViewById(R.id.pAddress);
        proEmail = view.findViewById(R.id.pEmail);
        proMobile = view.findViewById(R.id.pmobile);
        proName = view.findViewById(R.id.pname);
        proEdit = view.findViewById(R.id.pEdit);
        db = FirebaseFirestore.getInstance();
        profile_img = view.findViewById(R.id.profile_image);
        // Getting Intent...
        String cidpass =  CandiLogin.cidpass;
        fetchTheData(cidpass);
        fetchImage(cidpass);
        CandiProfileFragmentEdit candiProfileFragmentEdit=new CandiProfileFragmentEdit();
        proEdit.setOnClickListener(view1 -> getChildFragmentManager().beginTransaction()
                .replace(R.id.framelayoutContainer3, candiProfileFragmentEdit)
                .commit());

        return view;
    }

    private void fetchImage(String s) {
        storageReference = FirebaseStorage.getInstance().getReference("images/"+s);
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



    void fetchTheData(String cid){
        DocumentReference docRef = db.collection("CandiData").document(cid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    CandiData c = document.toObject(CandiData.class);
                    assert c != null;
                    proName.setText(c.getAucFullname());
                    proMobile.setText(String.valueOf(c.getAucMobile()));
                    proEmail.setText(c.getAucEmail());
                    proAadhar.setText(String.valueOf(c.getAucAadhaar()));
                    proAddress.setText(c.getAucAddress());
                } else {
                    Log.d("Akash", "No such document");
                }
            } else {
                Log.d("Akash", "get failed with ", task.getException());
            }
        });
    }
}