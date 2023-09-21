package com.example.votingadmin;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.votingpoll.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminLogin extends AppCompatActivity {

    private EditText uname, pword;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView message;
    private FirebaseFirestore db;
    private int counter = 3;
    static String logID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        uname = findViewById(R.id.username1);
        pword = findViewById(R.id.password1);
        message = findViewById(R.id.msg);
        loginButton = findViewById(R.id.loginbtn);
        progressBar = findViewById(R.id.progressbar);

        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(v -> loginUserAccount());

        Button signUpButton = findViewById(R.id.signupbtn);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AdminRegister.class);
            startActivity(intent);
        });
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String uid = uname.getText().toString();
        String password = pword.getText().toString();
        logID = uid;

        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter userID and password", Toast.LENGTH_LONG).show();
        } else {
            checkEmailInFirestore(uid, exists -> {
                if (exists) {
                    fetchUserData(uid, password);
                } else {
                    Toast.makeText(AdminLogin.this, "User not exists. Sign up and try again...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AdminRegister.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void checkEmailInFirestore(String uid, EmailCheckCallback callback) {
        CollectionReference collectionReference = db.collection("AdminData");
        Query aadharQuery = collectionReference.whereEqualTo("aAadhaar", uid);
        aadharQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                boolean emailExists = querySnapshot != null && !querySnapshot.isEmpty();
                callback.onEmailExists(emailExists);
            }
        });
    }

    void fetchUserData(String uid, String password) {
        DocumentReference docRef = db.collection("AdminData").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    AdminAddedData adminData = document.toObject(AdminAddedData.class);
                    if (adminData != null) {
                        String storedUID = adminData.getaAadhaar();
                        String storedPass = adminData.getaPassword();

                        if (uid.equals(storedUID) && password.equals(storedPass)) {
                            onLoginSuccess();
                        } else {
                            onLoginFailure();
                        }
                    }
                } else {
                    Toast.makeText(AdminLogin.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminLogin.this, "Failed to get data: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLoginSuccess() {
        message.setText("");
        message.setBackgroundColor(Color.TRANSPARENT);
        counter = 3;
        progressBar.setVisibility(View.GONE);
        Toast.makeText(AdminLogin.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminLogin.this, AdminHomeActivity.class);
        uname.setText("");
        pword.setText("");
        startActivity(intent);
    }

    private void onLoginFailure() {
        Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
        pword.setError("wrong password...");
        message.setVisibility(View.VISIBLE);
        message.setBackgroundColor(Color.RED);
        counter--;
        message.setText(Integer.toString(counter));

        if (counter == 0) {
            loginButton.setEnabled(false);
            new CountDownTimer(10000, 10) { // Set Timer for 10 seconds
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    loginButton.setEnabled(true);
                    message.setText("");
                    message.setBackgroundColor(Color.TRANSPARENT);
                    counter = 3;
                }
            }.start();
        }

        progressBar.setVisibility(View.GONE);
    }

    // Callback interface for checking email existence
    interface EmailCheckCallback {
        void onEmailExists(boolean exists);
    }
}
