package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class Cart extends AppCompatActivity {
android.support.v4.app.Fragment fmanager;
android.support.v4.app.FragmentTransaction ft;
SwipeRefreshLayout mswipe;
public String key,titles,images,desc;
public int size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
      //  mswipe = (SwipeRefreshLayout)findView
        // ById(R.id.crt_swipe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessage,
                new IntentFilter("datas"));
//        key = getIntent().getExtras().getString("prod_id");
  //      titles = getIntent().getExtras().getString("prod_title");
    //    images = getIntent().getExtras().getString("prod_img");
      //  desc = getIntent().getExtras().getString("prod_desc");
       // Log.e("key",key);
        //Bundle  b = new Bundle();
        //b.putString("prod_id",key);
        //b.putString("prod_title",titles);
        //b.putString("prod_desc",desc);
        //b.putString("prod_img",images);

        //cartfrag c = new cartfrag();
        //c.setArguments(b);

        Log.e("size in air",String.valueOf(size));
        fmanager = getSupportFragmentManager().findFragmentById(R.id.cartt);
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.cartt,new cartfrag());
        ft.commit();

       // Log.e("updated size",String.valueOf(f.recsize));
      //  f.adapter.notifyDataSetChanged();








    }








    public BroadcastReceiver mMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String recsize = intent.getStringExtra("recsize");

            size = Integer.parseInt(recsize);


           // Toast.makeText(addressadd.this,addres +" ",Toast.LENGTH_SHORT).show();
        }
    };


    public String getdkey(){
        return key;
    }

    public String getTitles(){
        return titles;
    }
    public String getDesc(){
        return desc;
    }
    public String getImages(){
        return images;
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
            //finish();
        }
        else {
            getFragmentManager().popBackStack();
        }

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




    @Override
    protected void onResume() {
        super.onResume();
       // check();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
