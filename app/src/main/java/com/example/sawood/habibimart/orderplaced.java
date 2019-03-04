package com.example.sawood.habibimart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

public class orderplaced extends AppCompatActivity {
    private FirestoreRecyclerAdapter adapter;
    public Button conti,calld;
    public ProgressBar orderprogress;
    public TextView ordsta,deln,delp,eta,oid,tota;
    public RecyclerView orderedr;
    String dname,dphone;
    String dman;
    String orderid,totalamount;
    public GifImageView gf;
    public Boolean eventrun= false;

   TextView cancel;

    @Nullable
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        //View layout = inflater.inflate(R.layout.orderplaced,container,false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderplaced);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gf = (GifImageView) findViewById(R.id.gifo);
        conti = (Button) findViewById(R.id.okbtn);
        ordsta =(TextView)findViewById(R.id.order_s);
        deln=(TextView)findViewById(R.id.deliveryman_n);
        delp=(TextView)findViewById(R.id.deliveryman_p);
        eta=(TextView)findViewById(R.id.time_r);
        calld=(Button) findViewById(R.id.call_d);
        orderedr=(RecyclerView)findViewById(R.id.ordrec);
        oid =(TextView)findViewById(R.id.orderid);
        tota=(TextView)findViewById(R.id.tot);
        cancel=(TextView)findViewById(R.id.cancelorder);

        if ((getIntent().getExtras().get("orderid"))!=null){
            orderid = getIntent().getExtras().getString("orderid");
            Log.e("orderid",orderid);
            totalamount = String.valueOf(getIntent().getExtras().getInt("totalamount"));
            oid.setText(orderid);
            tota.setText(totalamount);
            cancel.setVisibility(View.GONE);

        }
        else if (getIntent().getExtras().getStringArrayList("ongoingorers").size()>0){
            ArrayList<String> arrayList = getIntent().getExtras().getStringArrayList("ongoingorers");
            Log.e("array",""+arrayList);
            orderid = arrayList.get(0);
            gettotalamount(orderid);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tota.setText(totalamount);
                }
            },2000);
            oid.setText(orderid);

            cancel.setVisibility(View.GONE);



        }




        orderedr.setHasFixedSize(true);
        orderedr.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        //adapter.startListening();

       // orderprogress = (ProgressBar)findViewById(R.id.progresorder);
       // orderprogress.setVisibility(View.VISIBLE);

       // settimeline();
       ;

       // new AsyncCaller().execute();

        FirebaseFirestore dba = FirebaseFirestore.getInstance();
        Query query = dba.collection("Orders").document(orderid).collection("products");
        FirestoreRecyclerOptions<cartpro> response = new FirestoreRecyclerOptions.Builder<cartpro>().setQuery(query,cartpro.class).build();

        adapter = new FirestoreRecyclerAdapter<cartpro, myviewholder>(response) {
            @Override
            protected void onBindViewHolder(final myviewholder holde, int position, final cartpro mode) {
                holde.setTitle("Product: "+mode.getTitle());
                Log.e("get",mode.getTitle());
                holde.setDescription("description: "+mode.getDescription());
                holde.setImage(mode.getImage());
                holde.setprice("Price: "+mode.getPrice());
                holde.setUprice(mode.getUpdatedprice());
                holde.setQty(mode.getQty());
                // holde.setCname("customer name: "+mode.getCustomername());
                //holde.setCaddress("delivery address: "+mode.getCustomeraddress());
                //holde.setCphone("phone no: "+mode.getPhone());
                //holde.setPaymode("Payment mode: "+mode.getPaymode());
                //holde.setEmail("customer email: "+mode.getEmail());
                //holde.setOrderno(mode.getOrderid());
               // holde.setPaymentd("payment done: "+mode.getPaymentdone());
                //holde.setStat("Order Status: "+mode.getStatus());



            }

            @NonNull
            @Override
            public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.orderedcard, parent, false);
                return new myviewholder(view);
            }



            @Override
            public void onDataChanged() {
                super.onDataChanged();

            }
        };
        //holder.setadapterchild(adapter);
        adapter.notifyDataSetChanged();


        orderedr.setAdapter(adapter);
        if (orderid.length()>0){
            eventrun = true;
        }
        dbstatusup(orderid);
        //  adapter.stopListening();


        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
               // getActivity().onBackPressed();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                HashMap<String,Object> add = new HashMap<>();
                add.put("status","canceled");
                db.collection("Orders").document(orderid).update(add).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("storing","complete");
                    }
                });
            }
        });

        calld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number =  dphone;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    // Should we show an explanation?
                    // if (ActivityCompat.shouldShowRequestPermissionRationale(orders.this,
                    //       Manifest.permission.CALL_PHONE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    //} else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(orderplaced.this,
                            new String[]{Manifest.permission.CALL_PHONE}, 1);


                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                    //}
                }
                else{
                    startActivity(intent);

                }
            }
        });



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
    public void onBackPressed() {

        finish();
    }


public void settimeline(){
    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();

// Create new timeline row (Row Id)
    TimelineRow myRow = new TimelineRow(0);

// To set the row Date (optional)
    myRow.setDate(new Date());
// To set the row Title (optional)
    myRow.setTitle("Title");
// To set the row Description (optional)
    myRow.setDescription("Description");
// To set the row bitmap image (optional)
    myRow.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
// To set row Below Line Color (optional)
    myRow.setBellowLineColor(Color.argb(255, 0, 0, 0));
// To set row Below Line Size in dp (optional)
    myRow.setBellowLineSize(6);
// To set row Image Size in dp (optional)
    myRow.setImageSize(40);
// To set background color of the row image (optional)
    myRow.setBackgroundColor(Color.argb(255, 0, 0, 0));
// To set the Background Size of the row image in dp (optional)
    myRow.setBackgroundSize(60);
// To set row Date text color (optional)
    myRow.setDateColor(Color.argb(255, 0, 0, 0));
// To set row Title text color (optional)
    myRow.setTitleColor(Color.argb(255, 0, 0, 0));
// To set row Description text color (optional)
    myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));

// Add the new row to the list
    timelineRowsList.add(myRow);

// Create the Timeline Adapter
    ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(getApplicationContext(), 0, timelineRowsList,
            //if true, list will be sorted by date
            false);

// Get the ListView and Bind it with the Timeline Adapter
   // ListView myListView = (ListView) findViewById(R.id.timeline_listView);
   // myListView.setAdapter(myAdapter);
}

public void dbstatusup(final String orderi){
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    if (eventrun) {
        final DocumentReference docRef = db.collection("Orders").document(orderi);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                //  if (!orderi.isEmpty()){
                if (e != null) {
                    Log.w("habibimart", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("habibimart", "Current data: " + documentSnapshot.getData());
                    Log.e("orderid", "" + orderi);
                    String stat = documentSnapshot.get("status").toString();
                    dman = documentSnapshot.get("deliverymanassigned").toString();
                    getdeldetails(dman);


                    if (stat.equals("pending")) {
                        gf.setImageResource(R.drawable.lod);
                        ordsta.setText("your order has been placed.");
                        eta.setVisibility(View.GONE);
                        cancel.setVisibility(View.VISIBLE);


                    } else if (stat.equals("confirmed")) {
                        eta.setVisibility(View.VISIBLE);
                        geteta(dman);
                        gf.setImageResource(R.drawable.lgb);
                        ordsta.setText(R.string.ordconf);
                        cancel.setVisibility(View.GONE);
                    } else if (stat.equals("outfordelivery")) {
                        eta.setVisibility(View.VISIBLE);
                        geteta(dman);
                        gf.setImageResource(R.drawable.scooter);
                        ordsta.setText(R.string.outford);
                        cancel.setVisibility(View.GONE);
                    } else if (stat.equals("canceled")) {
                        eventrun= false;

                        cancelord(orderi);

                    }

                } else {
                    Log.d("habibimart", "Current data: null");
                }

            }


            // }
        });
    }

}



    public void getdeldetails(String d){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("delivery").document(d).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    dname = doc.get("name").toString();
                    deln.setText("Your Delivery Man is "+dname);
                    dphone = doc.get("phoneno").toString();
                    delp.setText("His Phone No is "+dphone);
                }
            }
        });


    }

    public void geteta(String d){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("delivery").document(d);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                String et = documentSnapshot.get("ETA").toString();
                eta.setText(et+" minutes remaining");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }




    class myviewholder extends RecyclerView.ViewHolder {
        public TextView textView_title, textView_decription, text_price, uprice, qty, cuname, caddress, email, paymode, cphone, orderno, paymentd, stat;
        public ImageView imageView;
        public View mView;

        public myviewholder(View itemView) {
            super(itemView);


            mView = itemView;
            textView_title = (TextView) itemView.findViewById(R.id.catname);
            textView_decription = (TextView) itemView.findViewById(R.id.offer);
            imageView = (ImageView) itemView.findViewById(R.id.thumbnail);
            text_price = (TextView) itemView.findViewById(R.id.catdesc);
            uprice = (TextView) itemView.findViewById(R.id.total);
            qty = (TextView) itemView.findViewById(R.id.qty);
            // cuname= (TextView) itemView.findViewById(R.id.cname);
            //caddress= (TextView) itemView.findViewById(R.id.caddr);
            //email= (TextView) itemView.findViewById(R.id.email);
            //paymode= (TextView) itemView.findViewById(R.id.paymode);
            //cphone= (TextView) itemView.findViewById(R.id.cp);
            //orderno= (TextView) itemView.findViewById(R.id.orderids);
            //paymentd = (TextView)itemView.findViewById(R.id.payd);
            //stat=(TextView)itemView.findViewById(R.id.status);
        }

        public void setTitle(String title) {
            textView_title.setText(title + "");
        }

        public void setDescription(String description) {
            textView_decription.setText(description);
        }

        public void setprice(String price) {
            text_price.setText(price);
        }

        public void setImage(String image) {
            Picasso.with(mView.getContext())
                    .load(image)
                    .into(imageView);
        }

        public void setUprice(int up) {
            uprice.setText("Total price: " + String.valueOf(up));
        }

        public void setQty(int qtyq) {
            qty.setText("Qty: " + String.valueOf(qtyq));
        }

        public void setCname(String cname) {
            cuname.setText(cname);

        }

        public void setCaddress(String cadd) {
            caddress.setText(cadd);
        }

        public void setEmail(String em) {
            email.setText(em);
        }

        public void setPaymode(String m) {

            paymode.setText(m);
        }

        public void setCphone(String cp) {
            cphone.setText(cp);
        }

        public void setOrderno(String ordern) {
            orderno.setText(ordern);
        }

        public void setPaymentd(String payment) {
            paymentd.setText(payment);
        }

        public void setStat(String sta) {
            stat.setText(sta);
        }

    }

    public void cancelord(final String Ordid){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        del(Ordid,dman);
        Log.e("dman",dman);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Orders").document(Ordid).collection("products").whereEqualTo("orderid", Ordid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()){
                    doc.getReference().delete();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failure",e.toString());
            }
        });
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("Orders").document(Ordid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection("users").document(user.getUid()).collection("Ongoing").document(Ordid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failure",e.toString());
                    }
                });
            }
        },2000);


    }

    public void del(String id,String deli) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("delivery").document(deli).collection("dqueue").whereEqualTo("orderid", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        doc.getReference().delete();
                    }
                }

            }
        });


    }

    public void gettotalamount(String o){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.e("orderid",o);
        db.collection("Orders").document(o).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                totalamount = doc.get("totalamount").toString();
                Log.e("totalamt",totalamount);
            }
        });
    }



}
