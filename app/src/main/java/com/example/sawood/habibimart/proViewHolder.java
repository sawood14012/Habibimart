package com.example.sawood.habibimart;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class proViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView textView_title;
    TextView textView_decription;
    ImageView imageView;
    TextView text_price;
    public proViewHolder(View itemView){
        super(itemView);
        mView=itemView;
        textView_title = (TextView)itemView.findViewById(R.id.catname);
        textView_decription = (TextView) itemView.findViewById(R.id.offer);
        imageView=(ImageView)itemView.findViewById(R.id.thumbnail);
        text_price = (TextView) itemView.findViewById(R.id.catdesc);
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
}
