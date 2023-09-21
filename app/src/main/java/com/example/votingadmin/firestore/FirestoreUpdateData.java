package com.example.votingadmin.firestore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreUpdateData {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void updateFieldForAllDocuments(String collectionName, String fieldName, Object newValue) {
        CollectionReference collectionRef = db.collection(collectionName);

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        DocumentReference documentRef = collectionRef.document(document.getId());
                        // Use the update method to change a specific field's value
                        // Alternatively, you can use the set method with SetOptions.merge() to update specific fields
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(fieldName, newValue);
                        documentRef.update(updates);
                    }
                } else {
                    // Handle errors here
                }
            }
        });
    }
}

