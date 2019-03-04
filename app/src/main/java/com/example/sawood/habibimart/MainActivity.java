package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference myref;
    private Button button;
    private SwipeRefreshLayout mswipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // mswipe = (SwipeRefreshLayout) findViewById(R.id.swip);
       // mswipe.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        android.app.Fragment fmanager;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //fmanager = getFragmentManager().findFragmentById(R.id.fragment);
        //android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
         //ft.replace(R.id.fragment,new MainFragment());
        //ft.commit();



    }


}



