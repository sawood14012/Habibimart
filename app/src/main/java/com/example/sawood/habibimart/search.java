package com.example.sawood.habibimart;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class search extends Fragment {

    public RecyclerView recyclerView;
    private DatabaseReference myref;
    public SwipeRefreshLayout mswipe;
    public String key;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {



       // return super.onCreateView(inflater, container, savedInstanceState);

        final View view=inflater.inflate(R.layout.activity_main,container,false);


        if (getArguments() != null) {
            key = getArguments().getString("str");}
         // key="f";

        // final  View view = super.onCreateView(inflater,group,b);
        // mswipe = (SwipeRefreshLayout)view.findViewById(R.id.swip);
        recyclerView=(RecyclerView) view.findViewById(R.id.pro);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        myref= FirebaseDatabase.getInstance().getReference().child("/blog");
        Log.e("key",key);
        key = toTitleCase(key);


        Query firebase_search = myref.orderByChild("title").startAt(key).endAt(key + "\uf8ff");
        FirebaseRecyclerAdapter<products,proViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<products,proViewHolder>(
                products.class,
                R.layout.card,
                proViewHolder.class,
                firebase_search
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

         if(key.length()!=0){
            recyclerView.setAdapter(recyclerAdapter);}

        // recyclerView.setVisibility(View.INVISIBLE);
        return view;
    }


    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}
