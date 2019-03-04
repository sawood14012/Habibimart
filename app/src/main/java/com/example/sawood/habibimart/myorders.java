package com.example.sawood.habibimart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class myorders extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView=(RecyclerView) findViewById(R.id.myoder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = db.collection("users").document(user.getUid()).collection("myorders");

        FirestoreRecyclerOptions<cartpro> response = new FirestoreRecyclerOptions.Builder<cartpro>().setQuery(query,cartpro.class).setLifecycleOwner(this).build();

        adapter = new FirestoreRecyclerAdapter<cartpro, myviewholder>(response) {
            @Override
            protected void onBindViewHolder(final myviewholder holder, int position, final cartpro model) {
                holder.setTitle(model.getTitle());
                holder.setDescription(model.getDescription());
                holder.setImage(model.getImage());
                holder.setprice(model.getPrice());
                holder.setUprice(model.getUpdatedprice());
                holder.setQty(model.getQty());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),singlepord.class);
                        i.putExtra("prod_id",model.getProdid());
                        startActivity(i);
                    }
                });


            }

            @NonNull
            @Override
            public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cartcard, parent, false);
                return new myviewholder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);







    }

    class myviewholder extends RecyclerView.ViewHolder{
        public TextView textView_title,textView_decription,text_price,uprice,qty;
        public ImageView imageView;
        public View mView;
        public myviewholder(View itemView) {
            super(itemView);



            mView=itemView;
            textView_title = (TextView)itemView.findViewById(R.id.catname);
            textView_decription = (TextView) itemView.findViewById(R.id.offer);
            imageView=(ImageView)itemView.findViewById(R.id.thumbnail);
            text_price = (TextView) itemView.findViewById(R.id.catdesc);
            uprice = (TextView) itemView.findViewById(R.id.total);
            qty = (TextView) itemView.findViewById(R.id.qty);
        }
        public void setTitle(String title)
        {
            textView_title.setText(title+"");
        }
        public void setDescription(String description)
        {
            textView_decription.setText(description);
        }
        public void  setprice(String price){text_price.setText(price);}
        public void setImage(String image)
        {
            Picasso.with(mView.getContext())
                    .load(image)
                    .into(imageView);
        }
        public void setUprice(int up){uprice.setText(String.valueOf(up));}
        public void setQty(int qtyq){qty.setText(String.valueOf(qtyq));}


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void onBackPressed() {
        finish();
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
}
