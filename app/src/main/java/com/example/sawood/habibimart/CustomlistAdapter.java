package com.example.sawood.habibimart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomlistAdapter extends ArrayAdapter<cartpro> {
     private List<cartpro> data;
     Context context;
    private static class ViewHolder {
        TextView item_name;
        TextView item_price;
        TextView item_uprice;
        TextView item_qty;
    }

    CustomlistAdapter(List<cartpro> data,Context con){
        super(con,R.layout.chkout,data);
        this.data = data;
        this.context = con;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        cartpro dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chkout, parent, false);
            viewHolder.item_name = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.item_price = (TextView) convertView.findViewById(R.id.item_price);
            viewHolder.item_qty = (TextView) convertView.findViewById(R.id.item_quantity);
            viewHolder.item_uprice = (TextView) convertView.findViewById(R.id.item_total_price);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.item_name.setText(dataModel.getTitle());
        viewHolder.item_price.setText("Price: "+dataModel.getPrice());
        viewHolder.item_qty.setText("Quantity: "+String.valueOf(dataModel.getQty()));
        viewHolder.item_uprice.setText("Total Price: "+String.valueOf(dataModel.getUpdatedprice()));

        // Return the completed view to render on screen
        return convertView;
    }
}


