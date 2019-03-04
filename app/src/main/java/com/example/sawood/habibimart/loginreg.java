package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

public class loginreg extends AppCompatActivity {
    Fragment fmanager;
    FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loginreg);
        fmanager = getFragmentManager().findFragmentById(R.id.loginregfrag);
        ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);
        ft.add(R.id.loginregfrag,new loginfrag());
        ft.commit();
    }


}
