package com.example.sawood.habibimart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class singlepord extends AppCompatActivity {

    String key;
    private DatabaseReference myref;
    private TextView single_ptitle;
    private TextView Single_desc;
    private ImageView single_image;
    public Button minus;
    public Button plus;
    public TextView quan;
    public Button add_to_cart;
    public String simg;
    public Databasehelper db;
    public String title;
    public  String desc,pric;
    public TextView price;
    public String qty;
    public int totalprice;
    public int updatedprice;
    public int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepord);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        single_ptitle = (TextView)findViewById(R.id.single_tit);
        Single_desc = (TextView)findViewById(R.id.single_desc);
        single_image = (ImageView) findViewById(R.id.single_pimg);
        quan = (TextView) findViewById(R.id.qty);
        add_to_cart = (Button)findViewById(R.id.add_cart);
        minus = (Button) findViewById(R.id.minus);
        plus = (Button) findViewById(R.id.plus);
        quan.setText("1");
        price = (TextView)findViewById(R.id.singleprice);

        myref = FirebaseDatabase.getInstance().getReference().child("/blog");
        key = getIntent().getExtras().getString("prod_id");

        myref.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 title = (String) dataSnapshot.child("title").getValue();
                 single_ptitle.setText(title);
                 desc = (String) dataSnapshot.child("description").getValue();
                 Single_desc.setText(desc);
                 simg = (String)dataSnapshot.child("image").getValue();
                 pric = (String)dataSnapshot.child("price").getValue();
                 price.setText(pric);
                updatedprice = Integer.parseInt(pric);

                totalprice = updatedprice * quantity;
                Picasso.with(getApplicationContext()).load(simg).into(single_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
                updatedprice = Integer.parseInt(pric);
                // if(quantity <0){quantity = 1;}
                if(quantity>0) {
                    totalprice = updatedprice * quantity;
                    Log.e("up", String.valueOf(totalprice));

                }

            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
                 updatedprice = Integer.parseInt(pric);
                // if(quantity <0){quantity = 1;}
                if(quantity>0){
                    totalprice = updatedprice * quantity;
                    Log.e("up",String.valueOf(totalprice));
                }
            }
        });





        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(getApplicationContext(),Cart.class);
                //i.putExtra("prod_id",key);
                //i.putExtra("prod_title",single_ptitle.getText());
                //i.putExtra("prod_desc",Single_desc.getText());
                //i.putExtra("prod_img",simg);
                db = new Databasehelper(getApplicationContext());
                long id = db.insertprod(title,desc,pric,simg,totalprice,quantity,key);
                Toast.makeText(getApplicationContext(),"PRODUCT ADDED TO CART",Toast.LENGTH_SHORT).show();
                Log.e("fgh",simg);
                Log.e("fgh",key);
                if (id == -1){
                    db.update(title,desc,pric,simg,totalprice,quantity);

                }
               // Bundle  b = new Bundle();
               // b.putString("prod_id",key);
               // cartfrag c = new cartfrag();
              //  c.setArguments(b);
          //      startActivity(i);
            }
        });




    }

    public void decrement(){
        quantity = quantity - 1;
        qty = String.valueOf(quantity);
        if (quantity<=0)
        {
            quantity = 1;
            quan.setText("1");
        }
        else {
            quan.setText(qty);


        }



    }

    public void increment(){
        if (quantity<1){
            quantity = 1;
        }
        quantity = quantity + 1;
        qty = String.valueOf(quantity);


        quan.setText(qty);
    }

    @Override
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
