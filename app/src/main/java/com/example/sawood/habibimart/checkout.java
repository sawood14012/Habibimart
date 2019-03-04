package com.example.sawood.habibimart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class checkout extends Fragment {
    List<cartpro> dataModels;
    ListView listView;
    Databasehelper db;
    int total_fin;
    TextView tot,add_f,phoneno;
    Button proceed,change,payt;
    String returnString;
    android.support.v4.app.Fragment fmanager;
    android.support.v4.app.FragmentTransaction ft;
    private static CustomlistAdapter adapter;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.checkoutlist,container,false);
        listView=(ListView)layout.findViewById(R.id.list);
        tot =(TextView)layout.findViewById(R.id.total_p);
        proceed=(Button)layout.findViewById(R.id.proceed);
        add_f = (TextView)layout.findViewById(R.id.address_f);
        phoneno =(TextView)layout.findViewById(R.id.phone_c);
        change=(Button)layout.findViewById(R.id.change_addr);
        payt=(Button)layout.findViewById(R.id.paytm_btm);
        db = new Databasehelper(layout.getContext());
        adapter= new CustomlistAdapter(getdata(),getContext());
        listView.setAdapter(adapter);

        total_fin = 0;
        for(int i=0;i<dataModels.size();i++){
            final cartpro model = dataModels.get(i);
            total_fin += model.getUpdatedprice();
        }

        tot.setText("TOTAL AMOUNT: "+total_fin);

        getaddrfromfirebase();
        getphonefromfirebase();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),addressadd.class);
                startActivityForResult(i,0);
            }
        });

      payt.setVisibility(View.INVISIBLE);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("price",total_fin);
                fmanager = getActivity().getSupportFragmentManager().findFragmentById(R.id.cartt);
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                selectpayment s = new selectpayment();
                s.setArguments(bundle);
                ft.replace(R.id.cartt,s).addToBackStack(null);
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

        dataModels = db.getallproducts();
        // notifyAll();

//         adapter.notifyDataSetChanged();



        //  }



        return dataModels;
    }

    public void getaddrfromfirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final StringBuilder address = new StringBuilder("");
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    address.append(doc.get("address"));
                    add_f.setText("Delivering to: "+address.toString());
                    Log.e("address",address.toString());


                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getaddrfromfirebase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {

                // get String data from Intent
                returnString = data.getStringExtra("addr");
                add_f.setText("Delivering to: "+returnString);
                sendaddrtofirebase();

            }
        }
    }

    public void sendaddrtofirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String a = returnString;
        Map<String, Object> add = new HashMap<>();
        add.put("address",a);
        db.collection("users").document(user.getUid()).update(add).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("address stored","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("address storing","failure"+e.toString());
            }
        });


    }


    public void getphonefromfirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final StringBuilder phone = new StringBuilder("");
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    phone.append(doc.get("phone"));
                    phoneno.setText(phone.toString());
                    Log.e("address",phone.toString());


                }
            }
        });


    }


   public void initpaytm(){
        Intent i = new Intent(getContext(),MerchantActivity.class);
        startActivity(i);
   }


}


