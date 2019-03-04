package com.example.sawood.habibimart;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class catViewholder extends RecyclerView.ViewHolder {

    View mview;
    TextView catename;
    TextView catedesc;
    TextView offers;
    ImageView cateimage;

    public catViewholder(View itemView) {
        super(itemView);
        mview=itemView;
        catename = (TextView)itemView.findViewById(R.id.catname);
        catedesc = (TextView)itemView.findViewById(R.id.catdesc);
        offers = (TextView)itemView.findViewById(R.id.offer);
        cateimage = (ImageView)itemView.findViewById(R.id.cathumb);
    }

    public void setCatename(String catname)
    {
        catename.setText(catname);
    }
    public void setCatedesc(String catdesc)
    {
        catedesc.setText(catdesc);
    }
    public void  setOffers(String offer){offers.setText("upto "+offer+"% off");}
    public void setCateimage(String catimage)
    {
        Picasso.with(mview.getContext())
                .load(catimage)
                .into(cateimage);
    }
}
