package com.example.user.bustracker;

import android.Manifest;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    Location location;
    LocationManager locationManager;
    Double lat=0.0, lng=0.0;
    String provider,locality;
    DatabaseReference databaseReference,databaseReference1;
    FirebaseUser Fuser;
    FirebaseAuth mAuth;
    String demail,sdeml,sdeml1,usertype,uristr,TAG="App content!";
    ProgressBar progressBar;
    StorageReference storageReference;
    CircleImageView circleImageView1;
    PassDataDriver driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressBar = findViewById(R.id.progressBar3);
        circleImageView1 = (CircleImageView) findViewById(R.id.profile_image);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        
        mAuth = FirebaseAuth.getInstance();
        Fuser = mAuth.getCurrentUser();
        demail = Fuser.getEmail();
        usertype = Fuser.getDisplayName();
        sdeml = demail.substring(1);
        sdeml1 = sdeml.replace(".com","");
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: "+usertype);
        Log.d(TAG, "onCreate: "+sdeml1);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(usertype).child(sdeml1).child("imguri");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child(usertype).child(sdeml1);
        storageReference = FirebaseStorage.getInstance().getReference();
        updateLatLng(lat,lng);

    }
    public void updateLatLng(double lt, double ln){
        databaseReference1.child("lat").setValue(lt);
        databaseReference1.child("lng").setValue(ln);
        Log.d(TAG, "updateLatLng: "+String.valueOf(lt));
        Log.d(TAG, "updateLatLng: "+String.valueOf(ln));
    }
    public void profile(View view) {            //pic profile button
        Intent intent = new Intent(MapsActivity2.this,Main5Activity.class);
        startActivity(intent);
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

    //@Override


    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uristr = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+uristr);
                if(uristr != null) {
                    storageReference.child(uristr).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: got url of image!");
                            new MapsActivity2.DownLoadImageTask(circleImageView1).execute(uri.toString());
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
                    circleImageView1.setImageResource(R.drawable.propic);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Something went wrong!");
            }
        };
        databaseReference.addValueEventListener(eventListener);
    }

    public void locate(View view) {              //location button
        lat = location.getLatitude();
        lng= location.getLongitude();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Your location1").icon(BitmapDescriptorFactory.fromResource(R.drawable.mybus)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(lat, lng),15));
        Toast.makeText(getApplicationContext(),lat.toString()+ " "+lng.toString(),Toast.LENGTH_LONG).show();
        updateLatLng(lat,lng);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        lat = location.getLatitude();
        lng= location.getLongitude();

        LatLng userLocation = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.mybus)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
        updateLatLng(lat,lng);

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng= location.getLongitude();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Your location").icon(BitmapDescriptorFactory.fromResource(R.drawable.mybus))) ;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(lat, lng),15));
        updateLatLng(lat,lng);

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
