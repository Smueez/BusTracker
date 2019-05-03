package com.example.user.bustracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider,memail,streml,Usertype,streml1,urist,driver_emailstr;
    FirebaseUser eml;
    Double lat = 0.0;
    Double lng = 0.0;
    Double lat1 = 0.0;
    Double lng1 = 0.0;
    Double latD=0.0;
    Double lngD=0.0,distance;
    Location location;
    EditText editText;
    TextView distence_text;
    ArrayList<Marker> markers = new ArrayList<>();
    String TAG = "Bust Tracke",driver_email;
    FirebaseAuth mAuth;
    StorageReference mStorageRef;
    private DatabaseReference mfDatabase,mdriverdatabase;
    ProgressBar progressBar;
    CircleImageView circleImageView;
    RelativeLayout linearLayout;
    //RelativeLayout relativeLayout = findViewById(R.id.mainlayout);
    FloatingActionButton floatingActionButton;
    ListView listView;
    List<PassDataDriver> passDataDriverList;
    Handler handler;
    FloatingActionButton imageButton;

    //Marker mSearchobj;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);              //rotation off
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listView = findViewById(R.id.buslist);
        progressBar = findViewById(R.id.progressBar);
        imageButton = findViewById(R.id.floatingActionButton10);
        handler = new Handler();
        mAuth = FirebaseAuth.getInstance();
        eml = mAuth.getCurrentUser();
        memail = eml.getEmail();
        distence_text = findViewById(R.id.textView9);
        streml = memail.substring(1);
        streml1 = streml.replace(".com","");
        Usertype = eml.getDisplayName();
        Log.d(TAG, "onCreate: oncreate method is running!");
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: "+Usertype);
        Log.d(TAG, "onCreate: "+streml1);
        mfDatabase = FirebaseDatabase.getInstance().getReference().child(Usertype).child(streml1).child("imguri");
        mdriverdatabase = FirebaseDatabase.getInstance().getReference().child("Driver_data");
        linearLayout = findViewById(R.id.listlayout);
        floatingActionButton = findViewById(R.id.floatingActionButton9);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        passDataDriverList = new ArrayList<>();
        editText = (EditText) findViewById(R.id.editText);
        //init();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    
                    performSearch();
                    return true;
                }
                return false;
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
    @Override
    protected void onStart() {                                                                                  //onStart function
        super.onStart();
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
                            new DownLoadImageTask(circleImageView).execute(uri.toString());
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: something went wrong!");
            }
        };
        mfDatabase.addValueEventListener(eventListener);
        Log.d(TAG, "onStart: "+urist);
        mdriverdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange driver: done!");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    PassDataDriver passDataDriver = ds.getValue(PassDataDriver.class);
                    passDataDriverList.add(passDataDriver);
                }

                Bus_list driver_adapter = new Bus_list(MapsActivity.this,passDataDriverList);
                listView.setAdapter(driver_adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clicked item
                driver_email = passDataDriverList.get(position).getEmailId();
                linearLayout.setVisibility(View.GONE);
                imageButton.setVisibility(View.GONE);
                floatingActionButton.setAlpha((float) 1.00);
                Log.d(TAG, "onItemClick: "+passDataDriverList.get(position).getEmailId());

            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: tracking");
                if (driver_email != null) {
                    driver_emailstr = driver_email.replace(".com","");
                    ValueEventListener eventListener1 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            latD = dataSnapshot.child(driver_emailstr).child("lat").getValue(Double.class);
                            lngD = dataSnapshot.child(driver_emailstr).child("lng").getValue(Double.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    Log.d(TAG, "onLocationChanged: "+driver_emailstr);
                    mdriverdatabase.addValueEventListener(eventListener1);
                    Log.d(TAG, "onLocationChanged: "+latD.toString()+"\n"+lngD.toString());
                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(latD,lngD)).icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon))));

                }
            }
        },1000);
    }

    public void hide_Keayboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Log.d(TAG, "performSearch: keyboard hiden!");
        }

    }

    public void performSearch(){
        String name = editText.getText().toString();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List <Address> list = geocoder.getFromLocationName(name,1);
            Log.i("Location info", "onLocationChanged: Done!");
            if (list != null && list.size() > 0){
                lat1=  list.get(0).getLatitude();
                lng1= list.get(0).getLongitude();
                Log.i("Searched adress info", "geoLocate: "+lat1+"\n"+lng1);
                //mSearchobj = mMap.addMarker(new MarkerOptions().position(new LatLng(lat1,lng1)).title("Your Destination"));
                markers.add( mMap.addMarker(new MarkerOptions().position(new LatLng(lat1,lng1)).title("Your Destination")));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(lat1, lng1),15));
            }
            //hide_Keayboard();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Geocoder errors", "geoLocate: " + e.getMessage());
        }

    }

    public void findbus(View view){
        linearLayout.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
        floatingActionButton.setAlpha((float) 0.2);
    }
    public void setlistview(View view){
        imageButton.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        floatingActionButton.setAlpha((float) 1.00);
    }

    public void profile(View view) {            //pic profile button
        Intent intent = new Intent(MapsActivity.this,Main5Activity.class);
        startActivity(intent);
    }

    public void locate(View view) {              //location button
        lat = location.getLatitude();
        lng= location.getLongitude();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Your location1").icon(BitmapDescriptorFactory.fromResource(R.drawable.personloc)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(lat, lng),15));
        Toast.makeText(getApplicationContext(),"Your location updated!",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       //mMap1 = googleMap;
        lat = location.getLatitude();
        lng= location.getLongitude();
        LatLng userLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.personloc)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
        }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        locationManager.requestLocationUpdates(provider, 100, 1, this);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }
    private Double meterDistanceBetweenPoints(Double lat_a, Double lng_a, Double lat_b, Double lng_b) {

        Location selected_location=new Location("locationA");
        selected_location.setLatitude(lat_a);
        selected_location.setLongitude(lng_a);
        Location near_locations=new Location("locationB");
        near_locations.setLatitude(lat_b);
        near_locations.setLongitude(lng_b);
        double distance=selected_location.distanceTo(near_locations);

        return distance;
    }
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng= location.getLongitude();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.personloc))) ;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(lat, lng),15));
        if (latD != 0.0 && lngD != 0.0) {
            markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(latD, lngD)).icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon))));
            distance =  meterDistanceBetweenPoints(lat,lng,latD,lngD);
            distence_text.setVisibility(View.VISIBLE);
            String x= new DecimalFormat("##.##").format(distance);
            float distime = (float) (distance / 666.6667);
            String t= new DecimalFormat("##.##").format(Math.ceil(distime));

            distence_text.setText("Your bus is: "+x+" Meters away\nArrival time approximately: "+t+" Min(s)");
            Log.d(TAG, "onLocationChanged: Bus location set!");
        }
        Log.d(TAG, "onLocationChanged: done!");
        Log.d(TAG, "onLocationChanged: "+driver_email);

        if (driver_email != null) {
            driver_emailstr = driver_email.replace(".com","");
            ValueEventListener eventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    latD = dataSnapshot.child(driver_emailstr).child("lat").getValue(Double.class);
                    lngD = dataSnapshot.child(driver_emailstr).child("lng").getValue(Double.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            Log.d(TAG, "onLocationChanged: "+driver_emailstr);
            mdriverdatabase.addValueEventListener(eventListener1);
            Log.d(TAG, "onLocationChanged: "+latD.toString()+"\n"+lngD.toString());


            }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
