package com.example.sawood.habibimart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.internal.cache.DiskLruCache;

public class selectpayment extends android.support.v4.app.Fragment {
    private RadioGroup selpay;
    private RadioButton paymode;
    private Button proceed;
    int totalamt;
    Databasehelper dba;
    public String clubkey;
    public String c;
    public List<String> keys = new ArrayList<>();
    public List<String> prot = new ArrayList<>();
    Map<String, Object> proddata = new HashMap<>();
    Map<String, Map> pro = new HashMap<>();
    Context ct;
    ProgressDialog pd;
    android.support.v4.app.Fragment fmanager;
    android.support.v4.app.FragmentTransaction ft;
    int documentcountfordelivery;
    int queuecount;
    List<Integer> countlist = new ArrayList<>();
    List<Integer> whichisempty = new ArrayList<>();
    Map<String,Integer> frequency= new HashMap<>();
    ProgressBar pb;

    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.selectpayment,container,false);
        totalamt = getArguments().getInt("price");
        selpay = (RadioGroup) layout.findViewById(R.id.paygroup);
        proceed = (Button) layout.findViewById(R.id.prc);
        pb = (ProgressBar) layout.findViewById(R.id.progress);
        dba = new Databasehelper(layout.getContext());
        ct=layout.getContext();
        pb.setVisibility(View.INVISIBLE);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = selpay.getCheckedRadioButtonId();
                Log.e("id",""+id);
                paymode = (RadioButton) layout.findViewById(id);
               // Toast.makeText(getContext(),""+paymode.getText()+totalamt,Toast.LENGTH_SHORT).show();
                String mode = paymode.getText().toString();
                if (mode.equals("paytm")){
                    initpaytm();

                }
                else if(mode.equals("COD")){
                  //  pb.setVisibility(View.VISIBLE);
                 //   pb = new ProgressBar(getContext(),null,android.R.attr.;

                  //  getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    //        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                   // pd.show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                       // pb.setProgress(15,true);
                       // setDialog();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        //View view = getLayoutInflater().inflate(R.layout.progress);
                        builder.setView(R.layout.prgb);
                        dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();


                    }
                    else
                    {
                        pd = new ProgressDialog(getContext());
                        pd.setMessage("placing your order please wait");
                        pd.setCanceledOnTouchOutside(false);
                        pd.setCancelable(false);
                        pd.show();
                    }
                    calculatedelivery();
                   // new  AsyncCaller().execute();

                }

            }
        });

        return layout;
    }

    public void initpaytm(){
        Intent i = new Intent(getContext(),MerchantActivity.class);
        i.putExtra("price",totalamt);
        startActivity(i);
        getActivity().finish();
    }

    public void createorder(String orderID, String name, String email, String address, String mode, String p, String pdone, String st, final String dname) {


final String oridfsts = orderID;
        //ProgressDialog pd = new ProgressDialog(getContext());



        // pd.show();

         List<cartpro> data = new ArrayList<>();
         data = dba.getallproducts();
         int s = data.size();
         Log.e("size",""+s);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        for (final cartpro model : data) {
            final Map<String, Object> add = new HashMap<>();

            add.put("title", model.getTitle());


            add.put("price",model.getPrice());
            add.put("updatedprice", model.getUpdatedprice());
            add.put("qty",model.getQty());
            add.put("prodid",model.getProdid());
            add.put("customername",name);
            add.put("customeraddress",address);
            add.put("email",email);
            add.put("paymode",mode);
            add.put("image",model.getImage());
            add.put("orderid",orderID);
            add.put("phone",p);
            add.put("paymentdone",pdone);
            add.put("status",st);
            add.put("deliverymanassigned",dname);
            add.put("description",model.getDescription());

            db.collection("Orders").document(orderID).collection("products").document(model.getProdid()).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                     //   pb.setProgress(80,true);
                    //}

                    String finalOrderID = "OrderID" + System.currentTimeMillis();
                    db.collection("orderedproducts").document(finalOrderID).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("address stored","success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("address storing","failure"+e.toString());
                            pd.dismiss();
                        }
                    });
                    Log.e("address stored","success");
                    db.collection("users").document(user.getUid()).collection("myorders").document(model.getProdid()).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("address stored","success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("address storing","failure"+e.toString());
                            pd.dismiss();

                        }
                    });
                    db.collection("delivery").document(dname).collection("dqueue").document(finalOrderID).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("address stored","success");
                          //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //    pb.setProgress(100,true);
                             //   getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            //}
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("address storing","failure"+e.toString());
                            pd.dismiss();

                        }
                    });
                    //calculatedelivery();
                    dba.delete(model.getTitle());


                   // fmanager = getActivity().getSupportFragmentManager().findFragmentById(R.id.cartt);
                    //ft = getActivity().getSupportFragmentManager().beginTransaction();

                    //ft.replace(R.id.cartt,new orderplaced()).addToBackStack(null);
                    //ft.commit();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("address storing","failure"+e.toString());
                }
            });


        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dialog.dismiss();
                }
                else {
                    pd.dismiss();
                }

                Intent i = new Intent(getContext(),orderplaced.class);
                i.putExtra("orderid",oridfsts);
                i.putExtra("totalamount",totalamt);
                startActivity(i);
                getActivity().finish();

            }
        }, 5000);










    }


    public void order(final String payment_mode, final String payment_done,final String deliveryname){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
       final String orderID = "OrderID" + System.currentTimeMillis();
        final StringBuilder phone = new StringBuilder("");
        final StringBuilder name = new StringBuilder("");
        final StringBuilder address = new StringBuilder("");
        final StringBuilder email = new StringBuilder("");

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    phone.append(doc.get("phone"));
                    name.append(doc.get("Name"));
                    address.append(doc.get("address"));;
                    email.append(doc.get("Email"));;
                   // phoneno.setText(phone.toString());
                    Log.e("address",phone.toString());
                    final Map<String, Object> add = new HashMap<>();
                    add.put("address",address.toString());
                    add.put("name",name.toString());
                    add.put("email",email.toString());
                    add.put("phoneno",phone.toString());
                    add.put("paymentmode",payment_mode);
                    add.put("paymentdone",payment_done);
                    add.put("totalamount",totalamt);
                    add.put("status","pending");
                    add.put("deliverymanassigned",deliveryname);
                    add.put("orderid",orderID);
                    add.put("otp","1111");
                    db.collection("Orders").document(orderID).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("address stored","success");
                          //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //    pb.setProgress(45,true);
                            //}
                            db.collection("users").document(user.getUid()).collection("Ongoing").document(orderID).set(add).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("address stored","success");
                                }
                            });
                            createorder(orderID,name.toString(),email.toString(),address.toString(),payment_mode.toString(),phone.toString(),payment_done,"pending",deliveryname);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("address storing","failure"+e.toString());
                        }
                    });


                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("address storing","failure"+e.toString());
            }
        });


    }


    public String getidsofprods(String p){



        DatabaseReference myref = FirebaseDatabase.getInstance().getReference();
        myref.child("/blog")
                .orderByChild("title")
                .equalTo(p)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            clubkey = childSnapshot.getKey();
                            Log.e("key",clubkey);
                            keys.add(clubkey);

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }


                });

        return clubkey;

    }

    public void calculatedelivery(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        getdocumentscount();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Log.e("LOG",""+documentcountfordelivery);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        calculatequeue();

                    }
                }, 1000);




            }
        }, 1000);




    }

    public int getdocumentscount(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("delivery")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()){
                                                   int count=0;
                                                   for (DocumentSnapshot document : task.getResult()) {
                                                       count++;

                                                   }
                                                   documentcountfordelivery = count;
                                                   Log.e("log",""+count);

                                               }
                                               else {
                                                   Log.d("error", "Error getting documents: ", task.getException());
                                               }


                                           }
                                       }
                );

        return documentcountfordelivery;

    }

    public void calculatequeue(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(int i=1;i<=documentcountfordelivery;i++){
            final int finalI = i;
            db.collection("delivery").document("deliveryman"+i).collection("dqueue")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        int cou=0;
                        if (task.getResult().isEmpty()){
                            Log.e("ramc",""+finalI);
                            whichisempty.add(finalI);

                        }
                        else{
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()){
                                    cou++;

                                }
                                else{
                                    Log.e("ramc","nothing in queue");
                                }











                            }
                            frequency.put("deliveryman"+finalI,cou);
                            countlist.add(cou);

                        }





                        queuecount=cou;
                        Log.e("count",countlist.toString());
                        Log.e("which is empty ",whichisempty.toString());
                        Log.e("frequency",frequency.toString());



                    }
                    else {
                        Log.e("error", "Error getting documents: ", task.getException());
                    }
                }
            });

       }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
               // pd.dismiss();
                if (whichisempty.isEmpty()){
                    Log.e("nothing is","empty");
                    getmin();

                }
                else{
                    Log.e(" is","empty");
                    int siz = whichisempty.size();
                    if (siz == 1){
                        addorderand_assigndman("deliveryman"+whichisempty.get(0));
                    }
                    else
                    {
                        int ran = randInt(0,siz-1);
                        addorderand_assigndman("deliveryman"+whichisempty.get(ran));
                    }






                }


            }
        }, 1500);



    }
    public void getmin(){
        List<String> n = new ArrayList<>();
        Map<String,Integer> m = new TreeMap<>(frequency);
        // int min= Collections.min(frequency.values());
        Map.Entry<String, Integer> min = null;
        for (Map.Entry<String, Integer> entry : m.entrySet()) {
            if (min == null || min.getValue() >= entry.getValue()) {
                if (min == null||min.getValue()>entry.getValue()){
                    min = entry;
                    n.clear();
                }

                    n.add(entry.getKey());
                   // n.add(min.getKey());
                   // min = entry;


            }
            Log.e("min",min.getKey()+":"+min.getValue());

        }

        Log.e("min",min.getKey()+":"+min.getValue());
        Log.e("minarry",n.toString());
        int sizeofmin = n.size();
        if (sizeofmin==1){
            Log.e("selected delivery man",n.get(0));
            addorderand_assigndman(n.get(0));
        }
        else {
            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusiveint randomNum = rand.nextInt((max - mi) + 1) + min;
            int randomNum = randInt(0,sizeofmin-1);
            Log.e("selected delivery man",n.get(randomNum));
            addorderand_assigndman(n.get(randomNum));

        }
        Log.e("sizeof min",""+sizeofmin);

    }

    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        //
        // In particular, do NOT do 'Random rand = new Random()' here or you
        // will get not very good / not very random results.
        Random rand = new Random();
        Log.e("min and max",""+min+max);

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum;
        randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void addorderand_assigndman(String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          //  pb.setProgress(30,true);
        //}
        order("COD","NO",name);



    }

    public void setDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //View view = getLayoutInflater().inflate(R.layout.progress);
        builder.setView(R.layout.prgb);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        // ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();






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

            // pd.dismiss();



        }

    }










}
