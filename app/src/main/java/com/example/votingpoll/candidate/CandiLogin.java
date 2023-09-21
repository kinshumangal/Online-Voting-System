package com.example.votingpoll.candidate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.votingadmin.EmailCheckCallback;
import com.example.votingpoll.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class CandiLogin extends AppCompatActivity {
    EditText uname, pword, message;
    Button button1;
    private ProgressBar progressbar;
    int counter = 3;
    private FirebaseFirestore db;
    static String cidpass = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candi_login);

        uname = findViewById(R.id.username1);
        pword = findViewById(R.id.password1);
        message = findViewById(R.id.msg);
        button1 = findViewById(R.id.loginbtn);
        progressbar = findViewById(R.id.progressbar);
        //for firestore
        db = FirebaseFirestore.getInstance();
        button1.setOnClickListener(v -> loginUserAccount());
        Button buttonsignup = findViewById(R.id.signupbtn);
        buttonsignup.setOnClickListener(view -> {
            Intent inte = new Intent(getApplicationContext(), CandiRegistration.class);
            startActivity(inte);
        });
        //getSupportActionBar().setTitle("Akash");

    }


    private void loginUserAccount() {
        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String Uid, password;
        Uid = uname.getText().toString();
        password = pword.getText().toString();
        cidpass = Uid;
        // validations for input email and password
        if (TextUtils.isEmpty(Uid)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter userID!!",
                            Toast.LENGTH_LONG)
                    .show();
        }

        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
        }
        else {
            checkEmailInFireStore(Uid, exists -> {
                if (exists) {
                    //Fetch the data from AdminData DB and
                    // Check the user entered data and DB data should match or not...
                    fetchTheData(Uid,password);
                }
                else {
                    Toast.makeText(CandiLogin.this, "User not exists Sign up and try again...", Toast.LENGTH_SHORT).show();
                    Intent inte = new Intent(getApplicationContext(), CandiRegistration.class);
                    startActivity(inte);
                    // hide the progress bar
                    progressbar.setVisibility(View.GONE);
                }
            });
        }
    }
    private void checkEmailInFireStore(String Uid, EmailCheckCallback callback){
        CollectionReference collectionReference = db.collection("CandiData");
        Query aadharQuery = collectionReference.whereEqualTo("aucAadhaar", Uid);
        aadharQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot querySnapshot = task.getResult();
                boolean emailExists = false;
                if(querySnapshot != null){
                    for(DocumentSnapshot document : querySnapshot.getDocuments()){
                        if(document.exists()) {
                            emailExists = true;
                            break;
                        }
                    }
                }
                callback.onEmailExists(emailExists);
            }
        });

    }
    @SuppressLint("SetTextI18n")
    void fetchTheData(String Uid, String password){
        DocumentReference docRef = db.collection("CandiData").document(Uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    CandiData c = document.toObject(CandiData.class);
                    assert c != null;
                    String getUID = c.getAucAadhaar();
                    String getPass = c.getAucPass();
                    if(Uid.equals(getUID) && password.equals(getPass)){
                        message.getText().clear();
                        message.setBackgroundColor(Color.TRANSPARENT);
                        counter = 3;
                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);
                        uname.setText("");
                        pword.setText("");
                        Intent intent
                                = new Intent(CandiLogin.this,
                                CandiHomeActivity.class);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                        pword.setError("wrong password...");
                        message.setVisibility(View.VISIBLE);
                        message.setBackgroundColor(Color.RED);
                        counter--;
                        message.setText(Integer.toString(counter));
                        if (counter == 0) {
                            button1.setEnabled(false);
                            new CountDownTimer(10000, 10) { //Set Timer for 10 seconds
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    button1.setEnabled(true);
                                    message.getText().clear();
                                    message.setBackgroundColor(Color.TRANSPARENT);
                                    counter = 3;
                                }
                            }.start();
                        }
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(CandiLogin.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CandiLogin.this, "get failed with "+ task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}