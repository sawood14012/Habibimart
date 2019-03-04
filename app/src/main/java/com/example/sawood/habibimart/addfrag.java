package com.example.sawood.habibimart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class addfrag extends android.app.Fragment {

    RecyclerView address_rec;
    private addressadapter addressadapter;
    List<addressstore> data = new ArrayList<>();
    dbhelperadddress db;
    onholderclick on;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.addr_frag,container,false);
        address_rec = (RecyclerView) layout.findViewById(R.id.addr_rec);
        db = new dbhelperadddress(layout.getContext());
        addressadapter = new addressadapter(getActivity(),getdata());
        address_rec.setHasFixedSize(true);
        addressadapter.notifyDataSetChanged();
        address_rec.setAdapter(addressadapter);
        address_rec.setItemAnimator(new DefaultItemAnimator());
        address_rec.setLayoutManager(new LinearLayoutManager(getActivity()));
        address_rec.refreshDrawableState();
        return layout;
    }


    public List<addressstore> getdata()
    {
        data = db.getalladdress();
        return data;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // data = db.getallproducts();
        // adapter.notifyDataSetChanged();
    }
}
