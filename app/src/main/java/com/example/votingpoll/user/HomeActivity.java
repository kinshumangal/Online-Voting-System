package com.example.votingpoll.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.votingpoll.R;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    DrawerLayout layDL;
    NavigationView vNV;
    Toolbar toolbar;
    TextView textView;
   // LinearLayout linearLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layDL = findViewById(R.id.layDL);
        vNV = findViewById(R.id.vNV);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.welcomeTxt);
        setSupportActionBar(toolbar);
        //linearLayout = findViewById(R.id.linearHome1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, layDL, toolbar, R.string.open_drawer, R.string.close_drawer);
        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(view -> {
            // Creating a toast to display the message
            //linearLayout.setVisibility(View.GONE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction t1 = fm.beginTransaction();
            TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment();
            t1.replace(R.id.fragmentContainer1, termsAndConditionsFragment);
            t1.addToBackStack(null);
            t1.commit();
        });
        layDL.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            vNV.setCheckedItem(R.id.profile);
        }
        NavClick();
    }

    private void NavClick() {
        vNV.setNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.profile) {
               // linearLayout.setVisibility(View.GONE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction t1 = fm.beginTransaction();
                ProfileFragment profileFragment = new ProfileFragment();
                t1.replace(R.id.fragmentContainer1, profileFragment);
                t1.addToBackStack(null);
                t1.commit();
                textView.setText("");
                layDL.closeDrawer(GravityCompat.START);
            }
            else if (id==R.id.status) {
                //linearLayout.setVisibility(View.GONE);
                Toast.makeText(this, "status", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction t1 = fm.beginTransaction();
                StatusFragment statusFragment = new StatusFragment();
                t1.replace(R.id.fragmentContainer1, statusFragment);
                t1.addToBackStack(null);
                t1.commit();
                textView.setText("");
                layDL.closeDrawer(GravityCompat.START);

            }
            else if (id==R.id.feedback) {
                //linearLayout.setVisibility(View.GONE);
                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction t1 = fm.beginTransaction();
                FeedbackFragment feedbackFragment = new FeedbackFragment();
                t1.replace(R.id.fragmentContainer1, feedbackFragment);
                t1.addToBackStack(null);
                t1.commit();
                textView.setText("");
                layDL.closeDrawer(GravityCompat.START);
            }
            else {
               // linearLayout.setVisibility(View.GONE);
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                layDL.closeDrawer(GravityCompat.START);
                finish();
            }
            layDL.closeDrawer(GravityCompat.START);
            return true;
        });
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