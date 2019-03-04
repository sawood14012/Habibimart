package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupfrag extends Fragment {

    private static final String TAG = "a";
    EditText user_name;
    EditText user_email;
    EditText phoneno;
    EditText user_password;
    EditText conf_psswd;
    Button signupbtn;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    SignInButton googlebtn;
    GoogleSignInClient mGoogleSignInClient;
    public static int RC_SIGN_IN=1;
    String n,e;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signupfrag,container,false);
        user_name = (EditText)view.findViewById(R.id.name);
        user_email= (EditText)view.findViewById(R.id.emailid);
        phoneno=(EditText)view.findViewById(R.id.phone);
        user_password=(EditText)view.findViewById(R.id.psswd);
        conf_psswd=(EditText)view.findViewById(R.id.retypepasswd);
        signupbtn=(Button)view.findViewById(R.id.login2);
        firebaseAuth = FirebaseAuth.getInstance();
       // googlebtn=(SignInButton)view.findViewById(R.id.signInButton);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

      //  googlebtn.setOnClickListener(new View.OnClickListener() {
          //  @Override
        //    public void onClick(View v) {
          //      signIn();
            //}
        //});

       // googlebtn.setVisibility(View.INVISIBLE);




        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        return view;
    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void signup()
    {
        if (validateForm()) {
            final ProgressDialog prd = ProgressDialog.show(getView().getContext(), " Signing Up..! ", "Please wait");
            String u_email = user_email.getText().toString().trim();
            String u_pass = user_password.getText().toString().trim();
            firebaseAuth.createUserWithEmailAndPassword(u_email, u_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.e("UID",UID);
                        storedata(UID);
                        //sendEmailVerification();
                        prd.dismiss();
                        // firebaseAuth.signOut();
                        //  sendUserData();
                        getActivity().finish();

                        //sendEmailVerification();

                        //  Toast.makeText(signup.this, "Successfully Registered, Upload complete!", Toast.LENGTH_LONG).show();
                        // l.signIn(user_email,user_password);
                        // firebaseAuth.signOut();

                        startActivity(new Intent(getActivity(), start.class));

                    } else {
                        Toast.makeText(getActivity(), "Registration Failed:" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        prd.dismiss();
                    }

                }
            });
        }

    }


    private boolean validateForm() {
        boolean valid = true;

        String userEmail = user_email.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            user_email.setError("Required.");
            valid = false;
        } else {
            user_email.setError(null);
        }

        String userPassword = user_password.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            user_password.setError("Required.");
            valid = false;
        } else {
            user_password.setError(null);
        }

        String userconfPassword = conf_psswd.getText().toString();
        if (TextUtils.isEmpty(userconfPassword)) {
            conf_psswd.setError("Required.");
            valid = false;
        } else {
            conf_psswd.setError(null);
        }
        if(user_password.getText().toString().equals(conf_psswd.getText().toString()))
        {
            conf_psswd.setError(null);

        }
        else {
            conf_psswd.setError("Passwords do not match");
            valid = false;
        }
        String phone = phoneno.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneno.setError("Required.");
            valid = false;
        } else {
            phoneno.setError(null);
        }

        String nam = user_name.getText().toString();
        if (TextUtils.isEmpty(nam)) {
            user_name.setError("Required.");
            valid = false;
        } else {
            user_name.setError(null);
        }



        return valid;
    }

    public void storedata(String uid){

        db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("Name",user_name.getText().toString());
        user.put("Email",user_email.getText().toString());
        user.put("phone",phoneno.getText().toString());
        user.put("passwd",user_password.getText().toString());
        db.collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               // Toast.makeText(getActivity(),"")
                Log.e("storing data","successful");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("storing data","unsuccessful");

            }
        });


    }



    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // sendUserData();
                        Toast.makeText(getActivity(), "Successfully Registered, Verification mail has been sent to your Email please verify it.", Toast.LENGTH_SHORT).show();
                        //firebaseAuth.signOut();
                        // finish();
                        // startActivity(new Intent(signup.this, Login.class));
                    }else{
                        Toast.makeText(getActivity(), "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.e("Uid",user.getUid());
                            Log.e("Name",user.getDisplayName());
                            Log.e("Uid",user.getEmail());

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

}
