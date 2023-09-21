package com.example.votingadmin;

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
import com.example.votingpoll.user.Register;
import com.example.votingpoll.user.ServerData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class AdminRegister extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private EditText fulln, usern, passw, cpassword, eMobile, eAddress, eAadharno;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        // Initialize UI elements
        initUI();

        // Firestore instance
        db = FirebaseFirestore.getInstance();

        // Image selection
        imageView.setOnClickListener(v -> selectImage());

        // Registration button
        Button signbtn = findViewById(R.id.signbtn);
        signbtn.setOnClickListener(v -> {
            uploadImage();
            registerNewUser();
        });

        // Login text in registration page
        TextView logintxtbtn = findViewById(R.id.logintxtbtn);
        logintxtbtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdminLogin.class)));
    }

    // Initialize UI elements
    private void initUI() {
        fulln = findViewById(R.id.fullname);
        usern = findViewById(R.id.username);
        passw = findViewById(R.id.password);
        eMobile = findViewById(R.id.mobile);
        eAadharno = findViewById(R.id.aadhar);
        eAddress = findViewById(R.id.address);
        cpassword = findViewById(R.id.confirmpassword);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
    }

    // Image selection from gallery
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    // Upload selected image to Firebase Storage
    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        String fileName = eAadharno.getText().toString();
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageView.setImageURI(null);
                    Toast.makeText(AdminRegister.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(AdminRegister.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                });
    }

    // Register a new user
    private void registerNewUser() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve user input
        String email, password, aadhar, fullname, address, conpassword;
        long mobile = 0;

        email = usern.getText().toString();
        password = passw.getText().toString();
        fullname = fulln.getText().toString();
        try {
            mobile = Long.parseLong(eMobile.getText().toString());
        } catch (Exception ignored) {}

        address = eAddress.getText().toString();
        aadhar = eAadharno.getText().toString();
        conpassword = cpassword.getText().toString();

        // Validate input fields
        if (!isValidInput(email, password, aadhar, fullname, address, mobile, conpassword)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Hide the progress bar before adding data to Firestore
        progressBar.setVisibility(View.GONE);

        // Add user data to Firestore
        addDataToFirestore(fullname, email, address, mobile, aadhar, password);
    }

    // Validate input fields
    private boolean isValidInput(String email, String password, String aadhar, String fullname,
                                 String address, long mobile, String conpassword) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usern.setError("Invalid email!");
            return false;
        }

        if (!validatePassword(password, conpassword, passw)) {
            return false;
        }

        if (TextUtils.isEmpty(fullname) || fullname.length() < 3) {
            fulln.setError("Invalid name!");
            return false;
        }

        if (String.valueOf(mobile).length() != 10) {
            eMobile.setError("Invalid mobile number!");
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            eAddress.setError("Invalid address!");
            return false;
        }

        if (TextUtils.isEmpty(aadhar)) {
            eAadharno.setError("Invalid Aadhaar number!");
            return false;
        }

        return true;
    }

    // Validate password
    public boolean validatePassword(String passwordInput, String cpass, EditText password) {
        // Password pattern
        final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[@#$%^&+=])" +     // at least 1 special character
                        "(?=\\S+$)" +            // no white spaces
                        ".{4,}" +                // at least 4 characters
                        "$");
        if (passwordInput.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        }
        if (!passwordInput.equals(cpass)) {
            password.setError("Password and confirm password must match");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    // Add user data to Firestore
    private void addDataToFirestore(String fname, String email, String address, long mobile,
                                    String aadhar, String passwd) {
        AdminAddedData adminAddedData = new AdminAddedData();
        adminAddedData.setaAadhaar(aadhar);
        adminAddedData.setaAddress(address);
        adminAddedData.setaEmail(email);
        adminAddedData.setaMobile(mobile);
        adminAddedData.setaFullname(fname);
        adminAddedData.setaPassword(passwd);

        // Add a new document with the Aadhaar number as the document ID
        db.collection("AdminData").document(aadhar)
                .set(adminAddedData)
                .addOnSuccessListener(unused -> {
                    // Redirect to the login activity after successful registration
                    Intent intent = new Intent(AdminRegister.this, AdminLogin.class);
                    startActivity(intent);
                    Toast.makeText(AdminRegister.this, "Signed up Successfully...", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminRegister.this, "Failed, please try again...", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide progress bar when resuming the activity
        progressBar.setVisibility(View.GONE);
    }
}
