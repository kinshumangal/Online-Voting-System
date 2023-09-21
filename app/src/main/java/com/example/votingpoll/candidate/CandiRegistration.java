package com.example.votingpoll.candidate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.votingpoll.R;
import com.example.votingpoll.user.AadharCheckCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class CandiRegistration extends AppCompatActivity {

    ImageView imageView;
    ProgressDialog progressDialog;
    Uri imageUri;
    StorageReference storageReference;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    CandiData userData;
    EditText fulln,usern,passw,cpassword,eMobile,eAddress,eAadharno;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candi_registration);
        fulln = findViewById(R.id.fullname);
        usern = findViewById(R.id.username);
        passw = findViewById(R.id.password);
        eMobile = findViewById(R.id.mobile);
        eAadharno = findViewById(R.id.aadhar);
        eAddress = findViewById(R.id.address);
        cpassword = findViewById(R.id.confirmpassword);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        Button signbtn = findViewById(R.id.signbtn);

        //for firestore
        db = FirebaseFirestore.getInstance();
        userData = new CandiData();
        signbtn.setOnClickListener(view -> registerNewUser()

        );
        //for login text in reg page
        TextView logintxtbtn = findViewById(R.id.logintxtbtn);
        logintxtbtn.setOnClickListener(v -> startActivity(new Intent(CandiRegistration.this, CandiLogin.class)));
        imageView.setOnClickListener(v -> selectImage());
        //getSupportActionBar().setTitle("Akash");
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) {
            assert data != null;
            if (data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }
    private void registerNewUser()
    {

        // show the visibility of progress bar to show loading
        progressBar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password, fullname, address, conpassword,aadhar;
        long mobile;
        email = usern.getText().toString();
        password = passw.getText().toString();
        fullname = fulln.getText().toString();
        mobile = Long.parseLong(eMobile.getText().toString());
        address = eAddress.getText().toString();
        aadhar = eAadharno.getText().toString();
        conpassword = cpassword.getText().toString();

        // Validations for input
        if (TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usern.setError("Invalid mail!!!");
        }
        else if (!validatePassword(password,conpassword,passw)) {
            passw.setError("Invalid password!!!");
            cpassword.setError("Invalid password!!!");
        }
        else if (TextUtils.isEmpty(fullname) || fullname.length()<3) {
            fulln.setError("Invalid name!!!");
        }
        else if (!(String.valueOf(mobile).length() == 10)) {
            eMobile.setError("Invalid mobile no!!!");
        }
        else if (TextUtils.isEmpty(address)) {
            eAddress.setError("Invalid address!!!");
        }
        else if (TextUtils.isEmpty(aadhar)) {
            eAadharno.setError("Invalid aadhaar no.!!!");
        }
        else {
            checkAadharNumberInFireStore(aadhar, exists -> {
                if (exists) {
                    db.collection("contestData").get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    addDatatoFireStore(fullname, email, address, mobile, aadhar,password);
                                    uploadIamage();
                                    Intent intent = new Intent(CandiRegistration.this, CandiLogin.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(CandiRegistration.this, "Poll not exist...", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                            });
                }
                else {
                    usern.setText("");
                    passw.setText("");
                    eAadharno.setText("");
                    eAddress.setText("");
                    eMobile.setText("");
                    fulln.setText("");
                    cpassword.setText("");
                    Toast.makeText(CandiRegistration.this, "Your not eligible...", Toast.LENGTH_SHORT).show();
                    // hide the progress bar
                    progressBar.setVisibility(View.GONE);
                }

            });
        }
    }

    private void checkAadharNumberInFireStore(String aadhar, AadharCheckCallback callback){
        CollectionReference collectionReference = db.collection("PollData");
        Query aadharQuery = collectionReference.whereEqualTo("aAadhaar", aadhar);
        aadharQuery.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot querySnapshot = task.getResult();
                boolean aadharExists = false;
                if(querySnapshot != null){
                    for(DocumentSnapshot document : querySnapshot.getDocuments()){
                        if(document.exists()) {
                            aadharExists = true;
                            break;
                        }
                    }
                }
                if (!aadharExists) {
                    Toast.makeText(this, "ID no. is not exists in the database", Toast.LENGTH_SHORT).show();
                }
                callback.onAadharExists(aadharExists);
            }
        });
    }
    public boolean validatePassword(String passwordInput,String cpass, EditText password) {
        // defining our own password pattern
        final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[@#$%^&+=])" +     // at least 1 special character
                        "(?=\\S+$)" +            // no white spaces
                        ".{4,}" +                // at least 4 characters
                        "$");
        if (passwordInput.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        }
        if(!passwordInput.equals(cpass)){
            password.setError("password and confirm password both should be same");
            return false;
        }

        // if password does not matches to the pattern
        // it will display an error message "Password is too weak"
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }
    private void addDatatoFireStore(String fname, String email, String address, long mobile,String aadhar,String pass) {

        //CollectionReference dbUserData = db.collection("UserData");
        CandiData userData1 = new CandiData();
        userData1.setaucAadhaar(aadhar);
        userData1.setaucAddress(address);
        userData1.setaucEmail(email);
        userData1.setaucFullname(fname);
        userData1.setaucMobile(mobile);
        userData1.setAucPass(pass);
        // Add a new document with a generated ID
        db.collection("CandiData").document(aadhar)
                .set(userData1).addOnSuccessListener(unused -> {
                   // Toast.makeText(CandiRegistration.this, "Success...", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(CandiRegistration.this, "Failed...", Toast.LENGTH_SHORT).show());
    }

    private void uploadIamage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();
        String fileName = eAadharno.getText().toString();
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);

        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            imageView.setImageURI(null);
            Toast.makeText(CandiRegistration.this, "Successfully registered", Toast.LENGTH_SHORT).show();

            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }).addOnFailureListener(e -> {

            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Toast.makeText(CandiRegistration.this, "Failed to upload", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}