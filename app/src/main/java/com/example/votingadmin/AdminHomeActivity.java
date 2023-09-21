package com.example.votingadmin;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingadmin.firestore.FirestoreDeleteData;
import com.example.votingadmin.firestore.FirestoreUpdateData;
import com.example.votingadmin.handlingcandidates.AddCandidates;
import com.example.votingadmin.handlingpoll.AdapterContest;
import com.example.votingadmin.handlingpoll.AddContest;
import com.example.votingadmin.handlingpoll.ContestClass;
import com.example.votingadmin.handlingusers.AddUser;
import com.example.votingadmin.handlingusers.ViewTermsAndConditionFragment;
import com.example.votingpoll.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    DrawerLayout layDL;
    NavigationView vNV;
    FragmentManager fm = getSupportFragmentManager();

    private ArrayList<ContestClass> myListData;
    private AdapterContest myListAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Bottom layout
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Initialize other views and setup the navigation drawer
        layDL = findViewById(R.id.layDL);
        vNV = findViewById(R.id.vNV);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, layDL, toolbar, R.string.open_drawer, R.string.close_drawer);
        layDL.addDrawerListener(toggle);
        toggle.syncState();

        // Setup RecyclerView for contests
        RecyclerView voteRV = findViewById(R.id.your_recycler_view_id11);
        voteRV.setHasFixedSize(true);
        voteRV.setLayoutManager(new LinearLayoutManager(this));

        // Initializing our variable for Firestore and getting its instance
        db = FirebaseFirestore.getInstance();

        // Creating our new array list for contests
        myListData = new ArrayList<>();
        myListAdapter = new AdapterContest(this, myListData);
        voteRV.setAdapter(myListAdapter);

        // Fetch contest data from Firestore
        fetchContestDataFromFirestore();

        if (savedInstanceState == null) {
            vNV.setCheckedItem(R.id.adduser1);
        }

        setupNavigationClickListeners();
    }

    private void fetchContestDataFromFirestore() {
        db.collection("contestData").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            ContestClass c = d.toObject(ContestClass.class);
                            if (c != null) {
                                c.setConId(d.getId());
                                myListData.add(c);
                            }
                        }
                        myListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminHomeActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminHomeActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int idd = item.getItemId();
        if (idd == R.id.adminprofile) {
            loadFragment(new AdminProfileFragment());
            return true;
        } else if (idd == R.id.viewfeedback) {
            loadFragment(new ViewFeedbackFragment());
            return true;
        } else if (idd == R.id.viewTC) {
            loadFragment(new ViewTermsAndConditionFragment());
            return true;
        }
        return false;
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        fm.beginTransaction()
                .replace(R.id.fragmentContainer1, fragment)
                .addToBackStack(null)
                .commit();
        layDL.closeDrawer(GravityCompat.START);
    }

    private void setupNavigationClickListeners() {
        vNV.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.addpoll) {
                loadFragment(new AddCandidates());
            } else if (id == R.id.results) {
                loadFragment(new ViewResultsFragment());
            } else if (id == R.id.adduser1) {
                loadFragment(new AddUser());
            } else if (id == R.id.addInfo) {
                loadFragment(new AddTermsAndConditionsFragment());
            } else if (id == R.id.addContest) {
                loadFragment(new AddContest());
            } else if (id == R.id.logout) {
                handleLogout();
            }
            layDL.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void handleLogout() {
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), AdminLogin.class);
        startActivity(intent);
        layDL.closeDrawer(GravityCompat.START);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void showDeletePopup(final ContestClass contest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete the contest with ID: " + contest.getConId() + "?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle delete action here
                myListData.remove(contest);
                myListAdapter.notifyDataSetChanged();

                // Delete the document in contestData and update the fields
                db.collection("contestData").document(contest.getConId()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminHomeActivity.this, "Contest deleted: " + contest.getConId(), Toast.LENGTH_SHORT).show();
                                    FirestoreDeleteData firestoreDeleteData = new FirestoreDeleteData();
                                    firestoreDeleteData.deleteAllDocumentsInCollection("CandiData");
                                    FirestoreUpdateData firestoreUpdateData = new FirestoreUpdateData();
                                    firestoreUpdateData.updateFieldForAllDocuments("UserData", "vote", "Not voted");
                                } else {
                                    Toast.makeText(AdminHomeActivity.this, "Contest not deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the exception
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
