package com.example.sawood.habibimart;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.Arrays;

public class catefrag extends Fragment{

    public RecyclerView recyclerView;
    private DatabaseReference myref;


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle b)
    {
        final View view=inflater.inflate(R.layout.catefrag,group,false);
        // final  View view = super.onCreateView(inflater,group,b);
        // mswipe = (SwipeRefreshLayout)view.findViewById(R.id.swip);
        recyclerView=(RecyclerView) view.findViewById(R.id.cater);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        myref= FirebaseDatabase.getInstance().getReference().child("/cate");
        FirebaseRecyclerAdapter<categoriesdata,catViewholder> recyclerAdapter=new FirebaseRecyclerAdapter<categoriesdata,catViewholder>(
                categoriesdata.class,
                R.layout.catecard,
                catViewholder.class,
                myref
        ) {
            @Override
            protected void populateViewHolder(catViewholder viewHolder, final categoriesdata model, int position) {

                final String key = getRef(position).getKey();
                viewHolder.setCatename(model.getCatname());
                viewHolder.setCatedesc(model.getCatdesc());
                viewHolder.setCateimage(model.getCatimage());
                viewHolder.setOffers(model.getOffer());
               // viewHolder.setTitle(model.getTitle());
                //viewHolder.setDescription(model.getDescription());
                //viewHolder.setImage(model.getImage());
                //viewHolder.setprice(model.getPrice());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp = model.getCatdesc();
                        String temp1 = model.getCatname();
                        String[] subbary = temp.split(",");
                        Log.e("array", Arrays.toString(subbary));

                        Log.e("temp",temp);
                        Intent i = new Intent(view.getContext(),tabcat.class);
                        i.putExtra("strarry",subbary);
                        i.putExtra("subname",temp1);
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

}
