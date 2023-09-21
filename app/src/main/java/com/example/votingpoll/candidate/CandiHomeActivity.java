package com.example.votingpoll.candidate;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votingpoll.R;
import com.google.android.material.navigation.NavigationView;

public class CandiHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candi_home);

        // Initialize views
        drawerLayout = findViewById(R.id.layDL);
        navigationView = findViewById(R.id.vNV);
        welcomeTextView = findViewById(R.id.welcomeTxt);

        // Set up the navigation drawer and toolbar
        setupNavigationDrawer();

        // Set the initial fragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.profile);
            replaceFragment(new CandiProfile());
        }

        // Handle navigation item clicks
        setNavigationItemClick();
    }

    private void setupNavigationDrawer() {
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, findViewById(R.id.toolbar), R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setNavigationItemClick() {
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.profile) {
                fragment = new CandiProfile();
            } else if (itemId == R.id.plan) {
                fragment = new AddPlansFragment();
            } else if (itemId == R.id.feedback) {
                fragment = new CandiFeedbackFragment();
            } else if (itemId == R.id.logout) {
                logout();
            }

            if (fragment != null) {
                replaceFragment(fragment);
                welcomeTextView.setText(""); // Clear welcome text
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer1, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logout() {
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), CandiLogin.class);
        startActivity(intent);
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
}
