package com.example.user.bustracker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;

public class Main4Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser BTuser;
    private static final int result_loead_image = 1;
    private StorageReference mStorageRef;
    ProgressBar progressBar;
    ImageView profile;
    DatabaseReference database;
    EditText fname,lname,email,password,phoneno,conpassw;
    TextView warning;
    String fnames,lnames,emails,passwords,phonenos,TAG="database:  ",username,conpasst,str,uristr;
    private Uri uri;
    PassData passData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        progressBar = findViewById(R.id.progressBar2);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Passenger_img");
        profile = (ImageView) findViewById(R.id.propic);
        email = (EditText) findViewById(R.id.email);
        emails = email.getText().toString().trim();
        database = FirebaseDatabase.getInstance().getReference("Passenger_data");
        fname = (EditText) findViewById(R.id.fname);
        password = (EditText) findViewById(R.id.psswrd);
        warning = findViewById(R.id.warn);
        lname = findViewById(R.id.lname);
        phoneno = findViewById(R.id.phone);
        conpassw = findViewById(R.id.conpass);
        mAuth = FirebaseAuth.getInstance();

    }
    public void back(View view){
        Intent intent = new Intent(this,Main3Activity.class);
        startActivity(intent);
    }

    private String getFileExtention(Uri imguri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imguri));
    }

    public void uploadimg(String e){
        final StorageReference fileref = mStorageRef.child(e).child(e+"."+getFileExtention(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                uristr = taskSnapshot.getMetadata().getPath();


                Log.d(TAG, "onSuccess: Image uploader to fire base");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Main4Activity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    public void next(View view){
        emails = email.getText().toString().trim();
        fnames = fname.getText().toString().trim();
        lnames = lname.getText().toString().trim();
        phonenos = phoneno.getText().toString().trim();
        conpasst = conpassw.getText().toString().trim();
        passwords = password.getText().toString().trim();
        username = fnames + " " + lnames;
        str=emails.replace(".com","");
        Log.d(TAG, "next: button tapped");
        if (passwords.equals(conpasst) && passwords != null) {
            Log.d(TAG, "next: password matched!");
            if (passwords.length() >= 6) {
                Log.d(TAG, "next: length okk!");
                if (emails != null && username != null && passwords != null && phonenos != null) {
                    Log.d(TAG, "next: nothing is null");
                    if (uri != null) {
                        Log.d(TAG, "next: "+uri.toString());
                        Log.d(TAG, "next: "+str);
                        uploadimg(str);             //image uoloading
                    }

                    uristr = "Passenger_img/"+str+"/"+str+"."+getFileExtention(uri);

                    passData = new PassData(username, passwords, phonenos,uristr);
                    Log.d(TAG, "next: "+uristr);
                    database.child(str).setValue(passData);         //database data updating
                    Log.d(TAG, "next: done");

                    mAuth.createUserWithEmailAndPassword("P"+emails, passwords)         //firebase auth
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");

                                        BTuser = mAuth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName("Passenger_data").build();
                                        BTuser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "onComplete: name been set!");
                                                Intent intent = new Intent(Main4Activity.this, Main3Activity.class);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: name not been set!");
                                            }
                                        });



                                    } else {
                                        // If sign in fails, display a message to the user.
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Log.d(TAG, "onComplete: email exist!");
                                            warning.setText("The email exist!");
                                            warning.setVisibility(View.VISIBLE);
                                        } else {
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }
                            });


                } else {
                    warning.setText("Please fill all the required info!");
                    warning.setVisibility(View.VISIBLE);
                    Log.d(TAG, "next: missing element!");
                }
            } else {
                warning.setText("Minimum length of password is 6!");
                Log.d(TAG, "next: length not enough!");
            }
        } else {
            warning.setText("Password does not match or it is empty!");
            warning.setVisibility(View.VISIBLE);
            Log.d(TAG, "next: password doesnt matched!");
        }
    }
   public void upload(View view){
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,result_loead_image);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == result_loead_image && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            profile.setImageURI(uri);
            Log.d(TAG, "onActivityResult: Pic is loading!");

        }


    }
}
