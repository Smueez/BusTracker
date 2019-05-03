package com.example.user.bustracker;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseUser user;
    String usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        final Intent intent = new Intent(this,Main2Activity.class);
        final Intent intent1 = new Intent(this,MapsActivity.class);
        final Intent intent2 = new Intent(this,MapsActivity2.class);


        new CountDownTimer(3000,2000){
            @Override
            public void onFinish() {
                if(auth.getCurrentUser() == null) {
                    startActivity(intent);
                }
                else {
                    user = auth.getCurrentUser();
                    usertype=user.getEmail();
                    if (usertype.charAt(0) == 'd'){
                        startActivity(intent2);
                    }
                    else {
                        startActivity(intent1);
                    }
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                ImageView img = (ImageView) findViewById(R.id.logo);
                img.animate().alpha(0f).setDuration(2000);
            }
        }.start();
    }
}