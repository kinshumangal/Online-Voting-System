package com.example.votingpoll.user;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingpoll.R;
import com.example.votingpoll.candidate.CandiData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VotingPageFragment extends Fragment implements SelectListener {
    RecyclerView recyclerView;
    StorageReference storageReference;
    static String toGetId;
    FirebaseFirestore db;
    List<CandiData> items=new ArrayList<>();


    public VotingPageFragment() { }
    @Override
    public void onIemClicked(CandiData item) {
        Toast.makeText(getActivity(), item.getAucFullname(), Toast.LENGTH_SHORT).show();
        FinalVotePage finalVotePage = new FinalVotePage();
        toGetId = item.getAucAadhaar();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.votefinalfragmentContainer1, finalVotePage)
                .commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting_page, container, false);
        recyclerView = view.findViewById(R.id.recyclicview12);
        db = FirebaseFirestore.getInstance();
        fetchTheData();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchTheData() {
        db.collection("CandiData").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            CandiData c = d.toObject(CandiData.class);
                            assert c != null;
                            c.setaucAadhaar(d.getId());
                            try {

                                storageReference = FirebaseStorage.getInstance().getReference("images/"+c.getAucAadhaar());
                                File localfile = File.createTempFile("tempfile", ".jpg");
                                storageReference.getFile(localfile)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                            try {
                                                storageReference = FirebaseStorage.getInstance().getReference("Party_Images/"+c.getAucAadhaar()+"Party");
                                                File localfile1 = File.createTempFile("tempfile", ".jpg");
                                                storageReference.getFile(localfile1)
                                                        .addOnSuccessListener(taskSnapshot1 -> {
                                                            Bitmap bitmap1 = BitmapFactory.decodeFile(localfile1.getAbsolutePath());
                                                            items.add(new CandiData(bitmap,c.getAucFullname(),bitmap1,c.getPartyName(),c.getAucAadhaar()));
                                                            recyclerView.setHasFixedSize(true);
                                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                            recyclerView.setAdapter(new VotingPageMyAdapter(getContext(),items,VotingPageFragment.this));
                                                        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to retrieve", Toast.LENGTH_SHORT).show());

                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to retrieve", Toast.LENGTH_SHORT).show());

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
//                        VotingPageMyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        Log.d("Aka","no data found");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    Log.d("Aka","failure to get");
                });
    }
}