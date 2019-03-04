package com.example.sawood.habibimart;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.Checksum;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;


public class MerchantActivity extends Activity {

    String orderID;
    Button startip;
    int tot;
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
    String respp;

    Dialog dialog;

    View v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_merchant);
       // startip = (Button)findViewById(R.id.start_transaction);
       // ct = getContext();

        tot = getIntent().getExtras().getInt("price");
        totalamt = tot;
        Log.e("price",  ""+tot);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        AlertDialog.Builder builder = new AlertDialog.Builder(getWindow().getContext());
        //View view = getLayoutInflater().inflate(R.layout.progress);
        dba = new Databasehelper(getWindow().getContext());
        builder.setView(R.layout.prgb);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        pd = new ProgressDialog(getWindow().getContext());
        pd.setMessage("placing your order please wait");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

       // startip.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //  public void onClick(View v) {
                generateCheckSum();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
         //   }
      //  });



    }

    protected void onStart(){
        super.onStart();
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public synchronized void onStartTransaction(String checkSum,View view) {
        PaytmPGService service = PaytmPGService.getStagingService();

        orderID = "OrderID" + System.currentTimeMillis();


      //  String checkSum = checksumGeneration("yOVXMl12625462515644", orderID, "CUST_ID?", "Retail", "WAP", "1.00", "WEBSTAGING", null, null, "merchant");//Creating checksum to pass it for transaction.
//        Log.e("cheksum :",checkSum);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", "yOVXMl12625462515644");//Replace your merchant id here.
        paramMap.put("ORDER_ID", orderID);//This is entered by yourself but must be unique. Any unique here unique created by system timestamp.
        paramMap.put("CUST_ID", "CUST_ID?");//Created by yourself.//Add your customer id like CUST123
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");//fix retail
        paramMap.put("CHANNEL_ID", "WAP");//Fix WAP for Mobile App and WEB for website
        paramMap.put("TXN_AMOUNT", "1.00");//Transaction Ammount like 1.00
        paramMap.put("WEBSITE", "WEBSTAGING");//APP_STAGING or WEB_STAGING or your own website host name if you have entered (STAGING for given name)
        paramMap.put("EMAIL", "email_id?");//Email id of customer from whom you want to take payment(Optional)
        paramMap.put("MOBILE_NO", "mobile_number?");//Mobile number of customer from whom you want to take payment(Optional)
        paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderID);//Callback url if you want to set yourself.
        paramMap.put("THEME", "merchant");//DEFAULT merchant
        paramMap.put("CHECKSUMHASH", checkSum);//Checksum generate by given above data need to pass here.

        PaytmOrder order = new PaytmOrder(paramMap);
        //PaytmClientCertificate clientCertificate = new PaytmClientCertificate(password,filename);

        PaytmMerchant Merchant = new PaytmMerchant(
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");
        service.initialize(order, null);



        service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        if (inErrorMessage != null)
                            Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_SHORT).show();
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }


                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Toast.makeText(getApplicationContext(),""+inResponse,Toast.LENGTH_SHORT);

                    }

                    @Override
                    public void networkNotAvailable() {
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                        // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {

                        if (inErrorMessage != null)
                            Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("Tag",inErrorMessage);
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,

                                                      String inErrorMessage, String inFailingUrl) {
                        try {
                            if (inErrorMessage != null)
                                Toast.makeText(getApplicationContext(), inErrorMessage + "inFailing Url : " + inFailingUrl + " iniErrorCode : " + iniErrorCode, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                    }


                });
    }



    private void generateCheckSum() {

        //getting the tax amount first.
        String txnAmount = String.valueOf(tot);

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );

        //creating a call object from the apiService
        Call<checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<checksum>() {
            @Override
            public void onResponse(Call<checksum> call, Response<checksum> response) {

                Log.e("checksumorder",response.body().getOrderId()+ " "+ response.body().getChecksumHash()+ " " +response.body().getPaytStatus());

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<checksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        Log.e("check",checksumHash);
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Toast.makeText(getApplicationContext(), inResponse.toString(), Toast.LENGTH_LONG).show();
                Log.e("res",inResponse.toString());
                respp = inResponse.toString();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // pb.setProgress(15,true);
                    // setDialog();


                    dialog.show();


                }
                else
                {

                    pd.show();
                }
                calculatedelivery();

            }

            @Override
            public void networkNotAvailable() {
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_LONG).show();

            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_LONG).show();
            }



            @Override
            public void onBackPressedCancelTransaction() {
               // Toast.makeText(getApplicationContext(), "Back Pressed", Toast.LENGTH_LONG).show();
                MerchantActivity.super.onBackPressed();

            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Toast.makeText(getApplicationContext(), inErrorMessage + inResponse.toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    public void createorder(String orderID, String name, String email, String address, String mode, String p, String pdone, String st, final String dname) {



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
            add.put("paytmresponse",respp);

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

                startActivity(new Intent(getWindow().getContext(),orderplaced.class));
                finish();

            }
        }, 5000);










    }


    public void order(final String payment_mode, final String payment_done,final String deliveryname){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
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
                    Map<String, Object> add = new HashMap<>();
                    add.put("address",address.toString());
                    add.put("name",name.toString());
                    add.put("Email",email.toString());
                    add.put("phoneno",phone.toString());
                    add.put("paymentmode",payment_mode);
                    add.put("paymentdone",payment_done);
                    add.put("totalamount",totalamt);
                    add.put("status","pending");
                    add.put("deliverymanassigned",deliveryname);
                    add.put("paytmresponse",respp);
                    add.put("orderid",orderID);
                    add.put("otp","1111");
                    db.collection("Orders").document(orderID).set(add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("address stored","success");
                            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //    pb.setProgress(45,true);
                            //}
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
        order("Paytm","yes",name);



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

