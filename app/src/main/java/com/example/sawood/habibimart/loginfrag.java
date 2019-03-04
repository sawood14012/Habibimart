package com.example.sawood.habibimart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.*;

public class loginfrag extends Fragment {

    private static final int RC_SIGN_IN = 1;
    TextView forgotpassword;
    EditText email;
    EditText password;
    Button Login;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView signup;
    Fragment fmanager;
    FragmentTransaction ft;
    FirebaseFirestore db;
    SignInButton googlebtn;
    GoogleSignInClient mGoogleSignInClient;
    public int val = 0;



    private GoogleApiClient mCredentialsApiClient;
    private static final int RC_HINT = 1000;



    private static final String TAG = "AndroidBash";
    private FirebaseAuth.AuthStateListener mAuthState;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view;
        view = inflater.inflate(R.layout.loginfrag, container, false);
        forgotpassword = (TextView) view.findViewById(R.id.forgotpass);
        email = (EditText) view.findViewById(R.id.emailid);
        password = (EditText) view.findViewById(R.id.password);
        Login = (Button) view.findViewById(R.id.login);
        signup = (TextView) view.findViewById(R.id.textView2);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(view.getContext());

        FirebaseUser m = firebaseAuth.getCurrentUser();


        if (m != null) {
            Log.d("android", "signed in");
            Intent i = new Intent(view.getContext(), start.class);
            startActivity(i);
            getActivity().finish();
        }

        googlebtn = (SignInButton) view.findViewById(R.id.signInButton);

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIng();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);



        mAuthState = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d("android", "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d("android", "onAuthStateChanged:signed_out");
                }

            }
        };

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = email.getText().toString().trim();
                String user_pass = password.getText().toString().trim();
                signIn(user_email, user_pass, view);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmanager = getFragmentManager().findFragmentById(R.id.loginregfrag);
                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.rev_in, R.animator.rev_out, R.animator.rev_in, R.animator.rev_out);
                ft.replace(R.id.loginregfrag, new signupfrag());
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        return view;
    }

    private void signIng() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signIn(String email, String password, final View v) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        final ProgressDialog prd = ProgressDialog.show(v.getContext(), "Logging you in ", "Please wait");
        // checkEmailVerification();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        // checkEmailVerification();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            prd.setMessage("invalid credentials");


                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    prd.dismiss();


                                }
                            }, 800);


                            // getActivity().finish();
                            //  Toast.makeText(v.getContext(), "Authentication failed." +task.getException().toString(),
                            //        Toast.LENGTH_SHORT).show();
                        } else {
                            //  checkEmailVerification();
                            Intent intent = new Intent(v.getContext(), start.class);
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            intent.putExtra("user_id", uid);
                            prd.dismiss();
                            getActivity().finish();
                            startActivity(intent);

                        }

                        // progressDialog.dismiss();
                    }
                });
        //
    }


    private boolean validateForm() {
        boolean valid = true;

        String userEmail = email.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String userPassword = password.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthState != null) {
            firebaseAuth.removeAuthStateListener(mAuthState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    public void storedata(final FirebaseUser uid) {
        //  int res = geteverythingfromfirebase();
        String mPhoneNumber = null;
        db = FirebaseFirestore.getInstance();
        Log.e("res", String.valueOf(val));



        Map<String, Object> user = new HashMap<>();
               user.put("Name", uid.getDisplayName());
               user.put("Email", uid.getEmail());
               user.put("phone", mPhoneNumber);
               user.put("passwd", "passwd");
               user.put("address", "");
               db.collection("users").document(uid.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       // Toast.makeText(getActivity(),"")
                       Log.e("storing data", "successful");

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.e("storing data", "unsuccessful");

                   }
               });










    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        Log.e("Uid",""+ acct.getPhotoUrl());

        // [START_EXCLUDE silent]
        //showProgressDialog();
        final ProgressDialog prd = ProgressDialog.show(getView().getContext(), " Signing Up..! ", "Please wait");
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           // geteverythingfromfirebase();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //storedata(user);
                           // addRealtimeUpdate(user);
                            geteverythingfromfirebase(user);
                            Log.e("Uid",user.getUid());
                            Log.e("Name",user.getDisplayName());
                            Log.e("Uid",user.getEmail());
                           // user.getPhoneNumber();
                           // storedata(user);
                            getActivity().finish();

                           // if(db.collection("users").docume
                              //addRealtimeUpdate(user);


                            startActivity(new Intent(getActivity(), start.class));
                            // updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        prd.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }






    public void geteverythingfromfirebase(final FirebaseUser user){

        db = FirebaseFirestore.getInstance();


        final DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.e(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.e(TAG, "Current data: null");
                    Log.e("user",user.getUid());
                    storedata(user);
                }
            }
        });



    }





public String getPhone() {
    TelephonyManager phoneMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        return "";
    }
    return phoneMgr.getLine1Number();
}

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)){
            Toast.makeText(getActivity(), "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Phone number: " + getPhone());
                } else {
                    Toast.makeText(getActivity(),"Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }




}


