package com.example.user.bustracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main5Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    String TAG="Profile view";
    FirebaseAuth mAuth;
    Spinner spinner1;
    TextView person_name,person_email,person_phone,person_DrivingID,rout,per_pass,user_type,error_mssg;
    FirebaseUser user;
    String userstr;
    String urist;
    String emailstr;
    String streml;
    String streml1,pass,selected;
    char str;
    Intent intent;
    LinearLayout linearLayout,linearLayout1,linearLayout3;
    DatabaseReference mfDatabase,mDataref;
    StorageReference mStorageRef;
    ProgressBar progressBar;
    CircleImageView circleImageView;
    EditText name_edit,phone_edit,driverId_edit,pass_edit;
    boolean editable = true;
    PassDataDriver dataDriver;
    FloatingActionButton floatingActionButton;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        //setContentView(R.layout.app_bar_main5);
       // supportMapFragment = SupportMapFragment.newInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        error_mssg = findViewById(R.id.error);
        floatingActionButton = findViewById(R.id.fab1);
        per_pass = findViewById(R.id.textView231);
        linearLayout = findViewById(R.id.phn);
        linearLayout1 = findViewById(R.id.driver_lay);
        linearLayout3 = findViewById(R.id.rout_layout);
        user_type = findViewById(R.id.textView10);
        mAuth = FirebaseAuth.getInstance();
        person_email = findViewById(R.id.textView23);
        person_name = findViewById(R.id.textView21);
        person_phone = findViewById(R.id.textView25);
        person_DrivingID = findViewById(R.id.textView27);
        rout = findViewById(R.id.textView29);
        progressBar = findViewById(R.id.progressBar5);
        circleImageView = findViewById(R.id.profile_image);
        name_edit = findViewById(R.id.editText2);
        pass_edit = findViewById(R.id.editText5);
        phone_edit = findViewById(R.id.editText6);
        driverId_edit = findViewById(R.id.editText8);
        user = mAuth.getCurrentUser();
        userstr = user.getDisplayName();
        emailstr = user.getEmail();
        str = emailstr.charAt(0);
        Log.d(TAG, "onCreate: "+str);
        streml = emailstr.substring(1);
        streml1 = streml.replace(".com","");
        mfDatabase = FirebaseDatabase.getInstance().getReference().child(userstr).child(streml1).child("imguri");
        mDataref = FirebaseDatabase.getInstance().getReference().child(userstr).child(streml1);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        person_email.setText(streml);
        Log.d(TAG, "onCreate: profile "+streml);
        Log.d(TAG, "onCreate: user type " + userstr);
        spinner1 = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> direction_arr = ArrayAdapter.createFromResource(this,
                R.array.direction_list, android.R.layout.simple_spinner_item);
        direction_arr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(direction_arr);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        CircleImageView imageView;


        public DownLoadImageTask(CircleImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    //email_edit,name_edit,phone_edit,driverId_edit,pass_edit;
    // person_name,person_email,person_phone,person_DrivingID,rout,nav_nam
    public void edit(View view){
        if (editable) {

            driverId_edit.setVisibility(View.VISIBLE);
            driverId_edit.setText(person_DrivingID.getText().toString().trim());
            person_DrivingID.setVisibility(View.GONE);

            pass_edit.setVisibility(View.VISIBLE);
            pass_edit.setText(pass);
            per_pass.setVisibility(View.GONE);

            name_edit.setVisibility(View.VISIBLE);
            name_edit.setText(person_name.getText().toString().trim());
            person_name.setVisibility(View.GONE);

            phone_edit.setVisibility(View.VISIBLE);
            phone_edit.setText(person_phone.getText().toString().trim());
            person_phone.setVisibility(View.GONE);

            spinner1.setVisibility(View.VISIBLE);

                floatingActionButton.setImageResource(R.drawable.tick1);
                editable = false;
        }
        else {

            if (str == 'd'){
                if (person_DrivingID.getText().toString().trim() != null && pass != null && person_name.getText().toString().trim() != null && selected != null) {
                    mDataref.child("name").setValue(name_edit.getText().toString().trim());
                    mDataref.child("password").setValue(pass_edit.getText().toString().trim());
                    mDataref.child("driverID").setValue(driverId_edit.getText().toString().trim());
                    mDataref.child("direction").setValue(selected);
                    Log.d(TAG, "edit: updating driver!");
                    Log.d(TAG, "edit: driver id  " + driverId_edit.getText().toString().trim());
                }
                else {
                    error_mssg.setVisibility(View.VISIBLE);
                    error_mssg.setText("Any info can not be blank!");
                }
            }
            else {
                if (phone_edit.getText().toString().trim() != null && pass != null && person_name.getText().toString().trim() != null) {
                    mDataref.child("name").setValue(name_edit.getText().toString().trim());
                    mDataref.child("password").setValue(pass_edit.getText().toString().trim());
                    mDataref.child("phoneno").setValue(phone_edit.getText().toString().trim());
                    Log.d(TAG, "edit: updating passenger!");
                }
                else {
                    error_mssg.setVisibility(View.VISIBLE);
                    error_mssg.setText("Any info can not be blank!");
                }
            }
            editable = true;
            floatingActionButton.setImageResource(R.drawable.edit);
            Intent intent = new Intent(Main5Activity.this,Main5Activity.class);
            startActivity(intent);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                urist = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+urist);
                if(urist != null) {
                    mStorageRef.child(urist).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: got url of image!");
                            new Main5Activity.DownLoadImageTask(circleImageView).execute(uri.toString());
                            Log.d(TAG, "onSuccess: "+uri.toString());
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                            Toast.makeText(getApplicationContext(),"Can not load image!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    circleImageView.setImageResource(R.drawable.propic);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: something went wrong!");
            }
        };
        mfDatabase.addValueEventListener(eventListener);
        mDataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pass = dataSnapshot.child("password").getValue(String.class);
                if(str == 'd'){
                    person_name.setText(dataSnapshot.child("name").getValue(String.class));
                    rout.setText(dataSnapshot.child("direction").getValue(String.class));
                    person_DrivingID.setText(dataSnapshot.child("driverID").getValue(String.class));
                }
                else  if (str == 'p'){
                    person_name.setText(dataSnapshot.child("name").getValue(String.class));
                    person_phone.setText(dataSnapshot.child("phoneno").getValue(String.class).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(str == 'd') {
            linearLayout.setVisibility(View.GONE);
            user_type.setVisibility(View.VISIBLE);
            user_type.setText("Logged in as Driver");
            Log.d(TAG, "onCreate: Logged as Driver");
        }
        else  if (str == 'p'){
            // mStorageRef = FirebaseStorage.getInstance().getReference();
            linearLayout1.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            user_type.setText("Logged in as Passenger");
            user_type.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreate: Logged in as passenger");
        }
        else {
            Log.d(TAG, "onCreate: it is slow! userstr  "+userstr );
        }
        Log.d(TAG, "onStart: something happened!");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main5, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Log.d(TAG, "onNavigationItemSelected: "+userstr);
            if (str == 'd'){
                intent = new Intent(Main5Activity.this,MapsActivity2.class);
                startActivity(intent);
            }
            else if (str == 'p'){
                intent = new Intent(Main5Activity.this,MapsActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_send) {
            mAuth.signOut();
            fileList();
            Intent intent = new Intent(Main5Activity.this,Main2Activity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Signed out!",Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
                                                                                                                     //map activity ends


}
