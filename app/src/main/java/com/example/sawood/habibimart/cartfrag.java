package com.example.sawood.habibimart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sawood.habibimart.Databasehelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class cartfrag extends android.support.v4.app.Fragment {
    private  DatabaseReference myref;
    RecyclerView cartrec;
    public cartadapter adapter;
    private List<cartpro> data = new ArrayList<>();
    public String key,titles,images,desc;
    public int pos=0;
    Databasehelper db;
    public int recsize;
    Button check;
    TextView emp;
    public int total;
    android.support.v4.app.Fragment fmanager;
    android.support.v4.app.FragmentTransaction ft;

    //public List<products> data =new ArrayList<>();
   // public products cu = new products();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // super.onCreateView(inflater, container, savedInstanceState);
        final View layout = inflater.inflate(R.layout.cart_frag,container,false);
        cartrec = (RecyclerView) layout.findViewById(R.id.cart_rec);

        check = (Button)layout.findViewById(R.id.checkout);
       // emp = (TextView)layout.findViewById(R.id.empty_view);

        myref = FirebaseDatabase.getInstance().getReference().child("/blog");
      //  Cart activity = (Cart) getActivity();
       // key = activity.getdkey();
        //titles = activity.getTitles();
        db = new Databasehelper(layout.getContext());
        adapter = new cartadapter(getActivity(),getdata());
        cartrec.setHasFixedSize(true);
        //desc = activity.getDesc();
        //images = activity.getImages();
//        Log.e("key:",key);
        //key = getArguments().getString("prod_id");
        Log.e("size",String.valueOf(recsize));
        // adapter.notifyDataSetChanged();
        cartrec.setAdapter(adapter);
        recsize = adapter.getItemCount();
        adapter.notifyDataSetChanged();
        cartrec.setItemAnimator(new DefaultItemAnimator());
        cartrec.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartrec.refreshDrawableState();
        Intent intent = new Intent("datas");
        intent.putExtra("recsize",String.valueOf(recsize));
        // LocalBroadcastManager.getInstance(layout.getContext()).sendBroadcast(intent);

        cartrec.getAdapter().notifyDataSetChanged();

         total = 0;
        for(int i=0;i<data.size();i++){
            final cartpro model = data.get(i);
            total += model.getUpdatedprice();
        }

        check.setEnabled(true);
        check.setVisibility(View.VISIBLE);
        check.setText(new StringBuilder().append("checkout: ").append(total).append(" rs").toString());
        check.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Toast.makeText(getContext(),""+total,Toast.LENGTH_SHORT).show();
                                                 fmanager = getActivity().getSupportFragmentManager().findFragmentById(R.id.cartt);
                                                 ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                 ft.replace(R.id.cartt,new checkout()).addToBackStack(null);
                                                 ft.commit();


                                             }
                                         });











        return layout;

    }

    public List<cartpro> getdata(){
        // List<products> data =new ArrayList<>();
        // products cu = new products();
        // Cart activity = (Cart) getActivity();
       // key = activity.getdkey();
       // myref = FirebaseDatabase.getInstance().getReference().child("/blog");


       // String[] titles = {"hhahdh","bhjjk","bhjn","bhjn","bhjn","bhjn","bhjn","bhjn","bhjn","bhjn","bhjn","bhjn","bhjn"};
        //for(int i=0;i<titles.length;i++)
      //  {
           // products cu = new products();
           // pos = pos+1;
           // cu.setTitle(titles);
           // cu.setDescription(desc);
          //  cu.setPrice("gf");
          //  cu.setImage("http://tatasalt.com/public/front_assets/images/tab-product-img1.png");
          //  data.add(cu);

         data = db.getallproducts();
        // notifyAll();

//         adapter.notifyDataSetChanged();



      //  }



        return data;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // data = db.getallproducts();
//         adapter.notifyDataSetChanged();




    }


    @Override
    public void onResume() {

        super.onResume();
        new AsyncCaller().execute();
    }



    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
       // ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            if(adapter.getItemCount()==0){



              //  emp.setVisibility(View.GONE);
                cartrec.setVisibility(View.GONE);
                fmanager = getActivity().getSupportFragmentManager().findFragmentById(R.id.cartt);
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.cartt,new emptycart());
                ft.commit();

            }
            else{
                cartrec.setVisibility(View.VISIBLE);

            }


            //this method will be running on UI thread
           // pdLoading.setMessage("\tLoading...");
           // pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {


            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

           // pdLoading.dismiss();


        }

    }




}

