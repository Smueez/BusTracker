package com.example.user.bustracker;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main3Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText eml,pss;
    String emls,psst,TAG="app logs: ";
    DatabaseReference databaseReference;
    PassData passData;
    TextView warnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mAuth = FirebaseAuth.getInstance();
        eml = findViewById(R.id.email);
        pss = findViewById(R.id.passwrd1);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Passenger_data");

        passData = new PassData();
        warnt = findViewById(R.id.warn);
    }


    public void userSingIn(String e, String p){

        mAuth.signInWithEmailAndPassword("P"+e,p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    finish();
                    Intent intent = new Intent(Main3Activity.this, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    // If sign in fails, display a message to the user.
                    warnt.setText("Email or Password doesn't matched!\nor Sign up!");
                    Log.w(TAG, "signInWithEmail:failure", task.getException());

                }
            }
        });

    }

    public void signup(View view){
        Intent intent = new Intent(this,Main4Activity.class);
        startActivity(intent);
    }
    public void back(View view){
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }

    public void next(View view){
        emls = eml.getText().toString().trim();
        psst = pss.getText().toString().trim();
        //str = emls.replace(".com","");
        Log.d(TAG, "next: next pressed!");
        userSingIn(emls,psst);

    }
}
