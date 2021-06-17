package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent registration = new Intent(MainActivity.this, RegistrationActivity.class);
                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                if(firebaseAuth.getCurrentUser() != null)
                    startActivity(home);
                else
                    startActivity(registration);
                finish();
            }
        }, 1500);
    }
}