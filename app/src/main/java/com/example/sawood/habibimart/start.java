package com.example.sawood.habibimart;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class start extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, android.location.LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SwipeRefreshLayout mswipe;
    public android.app.Fragment fmanager;
    public android.app.FragmentTransaction ft;
    private android.support.v7.widget.SearchView mSearch;
    public DatabaseReference myref;
    public MainFragment m;
    LocationManager locationManager;
    public TextView delivery_text;
    Location mloc;
    private FusedLocationProviderClient mFusedLocationClient;
    public TextView add_address;
    Geocoder geocoder;
    public List<Address> addresses;
    public String address;
    public String returnString;
    FirebaseFirestore db;
    GoogleApiClient mcapi = null;
    String selP;
    final int RESOLVE_HINT = 26;
    public LinearLayout gotord;
    //final Handler handler = new Handler();
    //final Runnable updateResults = null;
    public Snackbar sknc;
    public View v;


   ArrayList<String> ongoingorders =null;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_navi);
         v = findViewById(R.id.view);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("/blog");

        ongoingorders=new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mswipe = (SwipeRefreshLayout) findViewById(R.id.swip);
        gotord=(LinearLayout)findViewById(R.id.ordlyt);
        gotord.setVisibility(View.GONE);
        add_address = (TextView) findViewById(R.id.loctex);
        Log.e("get add", "ongoing");
        add_address.setText(getaddrfromfirebase());

        mcapi = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "online", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "offline", Toast.LENGTH_SHORT).show();
        }

        if (getaddrfromfirebase().isEmpty()) {
            getLocation();
        } else {
            add_address.setText(getaddrfromfirebase());
        }


        //  sendaddrtofirebase_infuture();


        //  mSearch.setLayoutParams(new ActionBar.LayoutParams(Gravity.LEFT))

        m = new MainFragment();
        // public final android.app.Fragment fmanager;
        fmanager = getFragmentManager().findFragmentById(R.id.frg);
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frg, m);
        ft.commit();

        mswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                waittoload();
                //sendaddrtofirebase_infuture();


                //mswipe.setRefreshing(false);
            }
        });


        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }


        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), addressadd.class);
                startActivityForResult(i, 0);


            }
        });

      //  final Runnable updateResults = new Runnable() {
        //    public void run() {
                getonorder();

                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ongoingorders.size()>0){
                            gotord.setVisibility(View.GONE);

                            Snackbar.make(v,"go to your order",Snackbar.LENGTH_INDEFINITE).setBehavior(new NoSwipeBehavior()).setAction(R.string.snack, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(),orderplaced.class);
                                    intent.putStringArrayListExtra("ongoingorers",ongoingorders);
                                    startActivity(intent);
                                }
                            })
                                    .show();
                        }
                        else {
                            gotord.setVisibility(View.GONE);
                        }
                    }
                },2000);

          //  }
      //  };



        gotord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String[] stringArray = ongoingorders.toArray(new String[0]);
                Intent intent = new Intent(getApplicationContext(),orderplaced.class);
                intent.putStringArrayListExtra("ongoingorers",ongoingorders);
                startActivity(intent);
            }
        });
//
//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                // This callback will be invoked in two situations:
//                // 1 - Instant verification. In some cases the phone number can be instantly
//                //     verified without needing to send or enter a verification code.
//                // 2 - Auto-retrieval. On some devices Google Play services can automatically
//                //     detect the incoming verification SMS and perform verification without
//                //     user action.
//                Log.d("d", "onVerificationCompleted:" + credential);
//
//                //  signInWithPhoneAuthCredential(credential);
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.w("d", "onVerificationFailed", e);
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                    // ...
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                    // ...
//                }
//
//                // Show a message and update the UI
//                // ...
//            }
//
//            @Override
//            public void onCodeSent(String verificationId,
//                                   PhoneAuthProvider.ForceResendingToken token) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.d("d", "onCodeSent:" + verificationId);
//
//                // Save verification ID and resending token so we can use them later
//                String mVerificationId = verificationId;
//                PhoneAuthProvider.ForceResendingToken mResendToken = token;
//
//                // ...
//            }
//        };
//        //  PhoneAuthActivity.java
//

        // getaddrfromfirebase();
        // getLocation();

        if (!checkpin()) {
            Toast.makeText(getApplicationContext(), "we dont deliver to this loc", Toast.LENGTH_SHORT).show();
        }


        //  sendaddrtofirebase();
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String mPhoneNumber = tMgr.getLine1Number();

        Log.e("phoneno",mPhoneNumber);




    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getaddrfromfirebase();
        if(add_address.getText().toString().isEmpty()){
            getLocation();
        }

        if (!checkpin()){
            Toast.makeText(getApplicationContext(),"we dont deliver to this loc",Toast.LENGTH_SHORT).show();
        }
        //c//heckdoc();
//        Log.e("return",returnString);
           // sendaddrtofirebase();
        getonorder();

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ongoingorders.size()>0){
                    Log.e("ongo",ongoingorders.toString());
                    gotord.setVisibility(View.GONE);

                    Snackbar.make(v,"go to your order",Snackbar.LENGTH_INDEFINITE).setBehavior(new NoSwipeBehavior()).setAction(R.string.snack, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),orderplaced.class);
                            intent.putStringArrayListExtra("ongoingorers",ongoingorders);
                            startActivity(intent);
                        }
                    })
                            .show();
                }
                else {
                    gotord.setVisibility(View.GONE);
                }
            }
        },2000);

    }

    private void search(String s) {

        Bundle bundle = new Bundle();
        Log.e("str",s);
        bundle.putString("str",s);
        search sb = new search();

        sb.setArguments(bundle);
        String ab = sb.getArguments().getString("str");
        Log.e("ff",ab);

            fmanager = getFragmentManager().findFragmentById(R.id.frg);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            getFragmentManager().popBackStack();
            ft.replace(R.id.frg, sb);
            ft.addToBackStack(null);
            ft.commit();


    }


    public void ontextchangesearch(String s){

          Bundle bundle = new Bundle();
          Log.e("str", s);
          bundle.putString("str", s);
          search sb = new search();

          sb.setArguments(bundle);
          String ab = sb.getArguments().getString("str");
          Log.e("ff", ab);
              fmanager = getFragmentManager().findFragmentById(R.id.frg);
              final FragmentTransaction ft = getFragmentManager().beginTransaction();
              ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
              getFragmentManager().popBackStack();
              ft.replace(R.id.frg, sb);
              ft.addToBackStack(null);
              ft.commit();



    }

    public void waittoload() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
              //  mswipe.setRefreshing(true);

                // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                // Toast.makeText(this,"loading..",Toast.LENGTH_SHORT).show();
                Log.e("loading:","loadded");
            }

            public void onFinish() {
                // mTextField.setText("done!");
                // recyclerView.setVisibility(View.VISIBLE);

                fmanager = getFragmentManager().findFragmentById(R.id.frg);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                getFragmentManager().popBackStack();
                ft.replace(R.id.frg,new MainFragment());
                ft.addToBackStack(null);
                ft.commit();
                mswipe.setRefreshing(false);

            }
        }.start();

    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       int s = menu.findItem(R.id.action_search).getOrder();
        Log.e(String.valueOf(s),"hh");



        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mt = menu.findItem(R.id.action_search);
        mSearch=(SearchView)
                MenuItemCompat.getActionView(mt);
        mSearch.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mt = menu.findItem(R.id.action_search);
        mSearch=(SearchView)
                MenuItemCompat.getActionView(mt);
       // mt.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("srt",query);
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ontextchangesearch(newText);
                return false;
            }



        });

        mSearch.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
             @Override
             public void onViewAttachedToWindow(View v) {

             }

             @Override
             public void onViewDetachedFromWindow(View v) {

                 fmanager = getFragmentManager().findFragmentById(R.id.frg);
                 final FragmentTransaction ft = getFragmentManager().beginTransaction();
                 ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                 getFragmentManager().popBackStack();
                 //ft.remove(fmanager);
                 ft.replace(R.id.frg,m);
                 ft.addToBackStack(null);
                 ft.commit();
                 Log.e("wrok","isworking");

             }
         });

//        mSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();

//        mSearch.setSearchableInfo(
  //              searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.action_search:
                // mSearch.getQuery();


                 break;
            case R.id.crt_btn:
                Intent i = new Intent(getApplicationContext(),Cart.class);
                startActivity(i);
                break;


        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.category) {
            Intent i = new Intent(this,categories.class);
            startActivity(i);

            // Handle the camera action
        } else if (id == R.id.myorders) {
            startActivity(new Intent(this,myorders.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, loginreg.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                returnString = data.getStringExtra("addr");
                Log.e("strsucc",returnString);

                // set text view with string
               // TextView addtext = (TextView) findViewById(R.id.loctext);
                add_address.setText(returnString);
            }


        }
        else if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                selP = credential.getId();
                Log.e("cred",selP);

                startPhoneNumberVerification(selP);




                // <-- will need to process phone number string
            }
            else {
                Toast.makeText(getApplicationContext(),"none selected",Toast.LENGTH_SHORT).show();
            }

    }}

     public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 8000, 5,  this);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0);
        //add_addr.setText(address);
        //loctext.setText(address);

        if(add_address.getText().length()==0){
            add_address.setText(address);
       // sendaddrtofirebase(address);
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
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //handler.post(updateResults);

        getonorder();

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ongoingorders.size()>0){
                    gotord.setVisibility(View.GONE);
                    Snackbar.make(v,"go to your order",Snackbar.LENGTH_INDEFINITE).setBehavior(new NoSwipeBehavior()).setAction(R.string.snack, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),orderplaced.class);
                            intent.putStringArrayListExtra("ongoingorers",ongoingorders);
                            startActivity(intent);
                        }
                    })
                            .show();
                }
                else
                {
                    gotord.setVisibility(View.GONE);
                }
            }
        },2000);
        //sendaddrtofirebase();
       // if (!checkpin()){
          //  Toast.makeText(getApplicationContext(),"we dont deliver to this loc",Toast.LENGTH_SHORT).show();
        //}



    }


   public void sendaddrtofirebase_infuture(){


                db = FirebaseFirestore.getInstance();
                FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String a = add_address.getText().toString().trim();
                while (!a.isEmpty()) {
                    Map<String, Object> add = new HashMap<>();
                    add.put("address", a);
                    db.collection("users").document(user.getUid()).update(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("address stored", "success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("address storing", "failure" + e.toString());
                        }
                    });
                }




   }

    public String getaddrfromfirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final StringBuilder addre = new StringBuilder("");
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    addre.append(doc.get("address"));
                    add_address.setText(addre.toString());
                    Log.e("address",addre.toString());


                }
            }
        });

        return addre.toString();


    }

    public boolean checkpin(){
        String checkpast = add_address.getText().toString();
        Log.e("string to check",checkpast);

        return checkpast.contains("562160");

    }

    public void getlocfuture(){
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("waiting to get","location");

            }

            @Override
            public void onFinish() {
                getLocation();

            }
        };
    }

    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mcapi, hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        // PhoneAuthProvider.getInstance().verifyPhoneNumber(
        //       phoneNumber,        // Phone number to verify
        //     60,                 // Timeout duration
        //   TimeUnit.SECONDS,   // Unit of timeout
        //  this,               // Activity (for callback binding)
        // mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        // mVerificationInProgress = true;
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Map<String, Object> add = new HashMap<>();
        add.put("phone", phoneNumber);
        db.collection("users").document(user.getUid()).update(add).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("address stored", "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("address storing", "failure" + e.toString());
            }
        });




    }

    public void getonorder(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).collection("Ongoing")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ongoingorders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document!=null){
                            Log.d("androidbash", document.getId() + " => " + document.getData());
                            ongoingorders.add(document.getId());
                            Log.e("android",ongoingorders.toString());

                        }
                        else {
                            gotord.setVisibility(View.GONE);
                        }

                    }
                } else {
                    Log.d("androidbash", "Error getting documents: ", task.getException());
                }
            }
        });




    }

    class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {

        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }



    }
