package com.example.votingpoll.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.votingpoll.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TermsAndConditionsFragment extends Fragment {
    ScrollView scrollView;
    CheckBox ch, ch1;
    Button button;
    FirebaseFirestore db;
    //LinearLayout linearLayout;


    public TermsAndConditionsFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
        ch = view.findViewById(R.id.checkBox);
        ch1 = view.findViewById(R.id.checkBox2);
        button = view.findViewById(R.id.button);
        scrollView = view.findViewById(R.id.termsScrollView);
        db = FirebaseFirestore.getInstance();
        //linearLayout = view.findViewById(R.id.termsandconditions);
        ScrollFragment scrollFragment = new ScrollFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.votefragmentContainer11, scrollFragment)
                .addToBackStack(null)
                .commit();
        button.setOnClickListener(view1 -> {
            if (ch.isChecked() && ch1.isChecked()) {
                checkDuplicateVotes();
            } else {
                Toast.makeText(getActivity(), "Please Accept Terms  & Conditions", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    void checkDuplicateVotes(){
        DocumentReference docRef = db.collection("UserData").document(Login.uidpass);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ServerData c = document.toObject(ServerData.class);
                    assert c != null;
                    if(c.getVote().equals("Not voted")){
                        VotingPageFragment votingPageFragment = new VotingPageFragment();
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.votefragmentContainer1, votingPageFragment)
                                .commit();
                    }
                    else {
                        Toast.makeText(getActivity(), "Already voted!!!", Toast.LENGTH_SHORT).show();
                        button.setEnabled(false);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Already voted!!!", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                }
            }
            else {
                Log.d("Akash", "Get failed with ", task.getException());
            }
        });
    }
}















//DocumentReference documentReference = db.collection("UserData").document(Login.uidpass);
//        Query aadharQuery = documentReference.
//        aadharQuery.get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                QuerySnapshot querySnapshot = task.getResult();
//                boolean Status = false;
//                if(querySnapshot != null){
//                    for(DocumentSnapshot document : querySnapshot.getDocuments()){
//                        if(document.exists()) {
//                            Status = true;
//                            break;
//                        }
//                    }
//                }
//                if (Status) {
//
//                }
//                else {
//                    Toast.makeText(getActivity(), "Already voted!!!", Toast.LENGTH_SHORT).show();
//                    button.setEnabled(false);
//                }
//
//            }
//        });