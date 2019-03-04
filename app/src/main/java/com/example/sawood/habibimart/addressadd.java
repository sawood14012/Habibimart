package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class addressadd extends AppCompatActivity implements android.location.LocationListener{

    public EditText add_addr;
    public TextView loctext;
    public String s;

    public Button addbt;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    public LocationManager locationManager;
    String latitude,longitude;

    public Button getlo;
    dbhelperadddress db;
    Fragment fmanager;
    FragmentTransaction ft;

   public Geocoder geocoder;
    public List<Address> addresses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressadd);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        add_addr = (EditText)findViewById(R.id.add_addr);
        getlo = (Button)findViewById(R.id.getlo);
        loctext =(TextView)findViewById(R.id.loctex);
        addbt = (Button) findViewById(R.id.add_bt);


          if (add_addr.getText().toString().trim().equals("")){
              addbt.setEnabled(false);
          } else {
              addbt.setEnabled(true);
          }

        add_addr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    addbt.setEnabled(false);
                } else {
                    addbt.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        // addbt = (Button)findViewById(R.id.add_bt);


        fmanager = getFragmentManager().findFragmentById(R.id.addressbook);
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.addressbook,new addfrag());
        ft.commit();


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));



        addbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendaddrtofirebase();

                s =  add_addr.getText().toString();
                Log.e("str",s);
                db =new dbhelperadddress(getApplicationContext());
                db.add_address(s);


                Intent i = new Intent();
                i.putExtra("addr",s);
                setResult(RESULT_OK,i);
                finish();
            }
        });



        getlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String addres = intent.getStringExtra("address");
            add_addr.setText(addres);

            Toast.makeText(addressadd.this,addres +" ",Toast.LENGTH_SHORT).show();
        }
    };




    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5,  this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
          geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            longitude=String.valueOf(location.getLongitude());
            latitude=String.valueOf(location.getLatitude());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        add_addr.setText(address);
        //loctext.setText(address);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                // Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void sendaddrtofirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String a = add_addr.getText().toString().trim();
        Log.e("address",a);
        Map<String, Object> add = new HashMap<>();

        add.put("address",a);
        add.put("latitude",latitude);
        add.put("longitude",longitude);
        db.collection("users").document(user.getUid()).update(add).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("address stored","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("address storing","failure"+e.toString());
            }
        });


    }



}
