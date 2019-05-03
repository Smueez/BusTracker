package com.example.user.bustracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class Main6Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText eml,pss;
    String emls,psst,TAG="app logs: ";
    DatabaseReference databaseReference;
    TextView warnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        eml = findViewById(R.id.emaill);
        pss = findViewById(R.id.password);
        warnt = findViewById(R.id.textView4);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver_dara");
    }

    public void userSingIn(String e, String p){

        mAuth.signInWithEmailAndPassword("D"+e,p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    finish();
                    Intent intent = new Intent(Main6Activity.this, MapsActivity2.class);
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

    public void back(View view){
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
    public void signup(View view){
        Intent intent = new Intent(this,Main7Activity.class);
        startActivity(intent);
    }
    public void next(View view){
        emls=eml.getText().toString().trim();
        psst=pss.getText().toString().trim();
        userSingIn(emls,psst);
    }
}
