package com.example.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.votingpoll.R;

public class MainActivity extends AppCompatActivity {
    TextView t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t=findViewById(R.id.textviewsplash);
        t.animate().translationX(1000).setDuration(1000).setStartDelay(2500);

        Thread thread= new Thread(() -> {
            try{
                Thread.sleep(4000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {

                Intent intent=new Intent(MainActivity.this, Welcome.class);
                startActivity(intent);
                finish();
            }
        });
        thread.start();
    }
}