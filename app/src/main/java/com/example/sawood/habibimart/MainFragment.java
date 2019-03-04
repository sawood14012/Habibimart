package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.logging.Handler;

public class MainFragment extends Fragment {

    public RecyclerView recyclerView;
    private DatabaseReference myref;
    public SwipeRefreshLayout mswipe;
    DatabaseReference mDatabase;
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle b)
    {
        final View view=inflater.inflate(R.layout.activity_main,group,false);
       // final  View view = super.onCreateView(inflater,group,b);
       // mswipe = (SwipeRefreshLayout)view.findViewById(R.id.swip);

        recyclerView=(RecyclerView) view.findViewById(R.id.pro);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        myref= FirebaseDatabase.getInstance().getReference().child("/blog");
        myref.keepSynced(true);





        FirebaseRecyclerAdapter<products,proViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<products,proViewHolder>(
                products.class,
                R.layout.card,
                proViewHolder.class,
                myref
        ) {
            @Override
            protected void populateViewHolder(proViewHolder viewHolder, products model, int position) {

                 final String key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(model.getImage());
                viewHolder.setprice(model.getPrice());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(view.getContext(),singlepord.class);
                        i.putExtra("prod_id",key);
                        startActivity(i);
                    }
                });
            }
        };
        DividerItemDecoration itemDecor = new DividerItemDecoration(view.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(recyclerAdapter);
       // recyclerView.setVisibility(View.INVISIBLE);
        return view;
    }

 /*   @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        waittoload();
        //recyclerView.setVisibility(view.INVISIBLE);
        mswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                    //recyclerView.setVisibility(View.INVISIBLE);
                    waittoload();
                //Toast.makeText(view.getContext(), "refreshed", Toast.LENGTH_LONG).show();

                }


               // mswipe.setRefreshing(false);


        });



    }*/

/*
    public void waittoload() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                mswipe.setRefreshing(true);

               // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
               // Toast.makeText(this,"loading..",Toast.LENGTH_SHORT).show();
                Log.e("loading:","loadded");
            }

            public void onFinish() {
               // mTextField.setText("done!");
               // recyclerView.setVisibility(View.VISIBLE);

                mswipe.setRefreshing(false);

            }
        }.start();

    }*/
}
