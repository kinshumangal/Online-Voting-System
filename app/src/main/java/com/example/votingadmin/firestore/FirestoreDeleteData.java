package com.example.votingadmin.firestore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreDeleteData {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void deleteAllDocumentsInCollection(String collectionName) {
        CollectionReference collectionRef = db.collection(collectionName);

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                } else {
                    // Handle errors here
                }
            }
        });
    }
}

