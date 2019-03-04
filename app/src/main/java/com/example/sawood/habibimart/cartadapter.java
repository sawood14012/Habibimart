package com.example.sawood.habibimart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class cartadapter extends RecyclerView.Adapter<cartadapter.myviewholder> {
    private final LayoutInflater inflater = null;
    List<cartpro> data ;
    Context mcontext;
    Databasehelper db;
   DatabaseReference myref= FirebaseDatabase.getInstance().getReference().child("/blog");
   public View v;






    public cartadapter(Context context,List<cartpro> data) {
        this.data= data;
        this.mcontext = context;
        // inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartcard,parent,false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myviewholder holder, final int position) {
        myref.orderByChild("title");
        final cartpro model = data.get(position);
       // final String key = myref.getRef(position).getKey();
        holder.setTitle(model.getTitle());
        holder.setDescription(model.getDescription());
        holder.setImage(model.getImage());
        holder.setprice(model.getPrice());
        holder.setUprice(model.getUpdatedprice());
        holder.setQty(model.getQty());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mcontext,singlepord.class);
                i.putExtra("prod_id",model.getProdid());
                mcontext.startActivity(i);

            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mcontext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mcontext);
                }
                builder.setTitle("Delete product")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton(R.string.del, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                 // String itemLabel = data.get(position);
                                 db = new Databasehelper(mcontext);
                                 db.delete(model.getTitle());
                                 data.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,data.size());
                                notifyDataSetChanged();
                                Log.e("size of",String.valueOf(data.size()));
                                cartfrag ct = new cartfrag();
                                ((AppCompatActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.cartt, new cartfrag()).commit();
                                Log.e("replace frag","complete");







                                // Show the removed item label`enter code here`
                                Toast.makeText(mcontext,"Item Removed",Toast.LENGTH_SHORT).show();



                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;

            }
        });



    }










    @Override
    public int getItemCount() {

        Log.e("s",String.valueOf(data.size()));
       // notifyDataSetChanged();
        return data.size();
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












}
