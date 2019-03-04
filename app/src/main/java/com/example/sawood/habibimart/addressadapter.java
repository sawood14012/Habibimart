package com.example.sawood.habibimart;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class addressadapter extends RecyclerView.Adapter<addressadapter.myvholder> {

    private final LayoutInflater inflater = null;
    private onholderclick onholderclick;
    List<addressstore> data;
    Context mcontext;
    DatabaseReference myref= FirebaseDatabase.getInstance().getReference().child("/blog");
    public View v;
    FragmentTransaction ft;
    Fragment fmanager;
    DataSnapshot dataSnapshot;
    dbhelperadddress db;

    public addressadapter(Context context,List<addressstore> data) {
        this.data = data;
        this.mcontext = context;

    }



    @NonNull

    @Override
    public myvholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adrlist,parent,false);

        return new myvholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myvholder holder, final int position) {
        final addressstore model = data.get(position);

        holder.setaddres(model.getAddress());
        holder.rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new dbhelperadddress(mcontext);
                db.delete(model.getAddress());
                data.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,data.size());
                // Show the removed item label`enter code here`
                Toast.makeText(mcontext,"Address Removed",Toast.LENGTH_SHORT).show();

            }
        });

        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String s = holder.getaddress();
                Log.e("s",s);
                Intent intent = new Intent("custom-message");
                intent.putExtra("address",s);
                LocalBroadcastManager.getInstance(mcontext).sendBroadcast(intent);
            }
        });



    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class myvholder extends RecyclerView.ViewHolder{
        public TextView addres;
        public View mview;
        public ImageButton rem;

        public myvholder(View itemView) {

            super(itemView);

            mview = itemView;
            addres = (TextView)itemView.findViewById(R.id.addres);
            rem =(ImageButton)itemView.findViewById(R.id.remove_ad);

        }

        public void setaddres(String addr)
        {
            addres.setText(addr);
        }

        public String getaddress(){

             String s = addres.getText().toString();
             return s;
        }
    }

   // TextView addres = (TextView) v.findViewById(R.id.addres);



}
