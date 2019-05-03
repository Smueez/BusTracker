package com.example.user.bustracker;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main7Activity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    FirebaseUser BTuser;
    private StorageReference mStorageRef;
    EditText firstname, lastname, pass, driverID,cofirmpass,emailxml;
    TextView error;
    String firstnamestr, lastnamestr, passstr, driverIDstr, vehiclenostr,cofirmpassstr,TAG="database:  ",full,emails,uristr,str,direct,selected;
    DatabaseReference databaseReference;
    private final static int result_loead_image=1;
    private Uri uri= null;
    ImageView profile;
    ProgressBar progressBar1;
    Spinner Dir_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        Dir_spinner = findViewById(R.id.spinner);
        progressBar1 = findViewById(R.id.progressBar4);
        profile =findViewById(R.id.propic);
        emailxml = findViewById(R.id.emailID);
        pass= findViewById(R.id.password);
        cofirmpass = findViewById(R.id.conpass);
        firstname = findViewById(R.id.fname);
        lastname = findViewById(R.id.lname);
        driverID =  findViewById(R.id.drid);
        error = findViewById(R.id.errortxt);
        cofirmpass = findViewById(R.id.conpass);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver_data");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Driver_img");

        ArrayAdapter<CharSequence> direction_arr = ArrayAdapter.createFromResource(this,
                R.array.direction_list, android.R.layout.simple_spinner_item);
        direction_arr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Dir_spinner.setAdapter(direction_arr);

        Dir_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        selected = "";
                        break;
                    case 1:
                        selected = "KUET to Shonadanga";
                        break;
                    case 2:
                        selected = "KUET to Dak-Bangla";
                        break;
                    case 3:
                        selected = "KUET to Rupsa";
                        break;
                }
                Log.d(TAG, "onItemSelected: "+selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected = "Unknown";
            }
        });
    }


    public void back(View view){
        Intent intent = new Intent(this,Main6Activity.class);
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
                progressBar1.setVisibility(View.GONE);
                uristr = taskSnapshot.getMetadata().getPath();


                Log.d(TAG, "onSuccess: Image uploader to fire base");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Main7Activity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar1.setVisibility(View.VISIBLE);
            }
        });
    }

    public void next(View view) {
        emails = emailxml.getText().toString().trim();
        driverIDstr = driverID.getText().toString().trim();
        firstnamestr = firstname.getText().toString().trim();
        lastnamestr = lastname.getText().toString().trim();
        passstr = pass.getText().toString().trim();
        cofirmpassstr = cofirmpass.getText().toString().trim();
        full = firstnamestr + " " + lastnamestr;
        str = emails.replace(".com", "");

        Log.d(TAG, "next: "+passstr);
        Log.d(TAG, "next: "+cofirmpassstr);

        if (passstr.equals(cofirmpassstr) && passstr != null) {
            if (passstr.length() >= 6) {
                if (full != null && driverIDstr != null && emails != null && selected != null) {
                    if (uri != null) {
                        uploadimg(str);
                        uristr = "Driver_img/" + str + "/" + str + "." + getFileExtention(uri);
                    }                       //image uoloading
                    else {
                        uristr = "null";
                    }

                        progressBar1.setVisibility(View.VISIBLE);


                        PassDataDriver passDataDriver = new PassDataDriver(full, passstr, driverIDstr, uristr, selected, emails, 0.0, 0.0);
                        Log.d(TAG, "next: selected: " + selected);
                        Log.d(TAG, "next: " + uristr);
                        databaseReference.child(str).setValue(passDataDriver);
                        Log.d(TAG, "next: done");

                    mAuth.createUserWithEmailAndPassword("D" + emails, passstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");

                                BTuser = mAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName("Driver_data").build();
                                BTuser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(TAG, "onComplete: name been set!");
                                        Intent intent = new Intent(Main7Activity.this, Main6Activity.class);
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
                                    error.setText("The email exist!");
                                    error.setVisibility(View.VISIBLE);
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

                } else {
                    error.setText("Please fill all the required info!");
                    error.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getApplicationContext(),"Password must be atleast 6 letters!",Toast.LENGTH_LONG);
            }
        } else {
            error.setText("Password does not matched or it is empty!");
            error.setVisibility(View.VISIBLE);
            progressBar1.setVisibility(View.GONE);
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
