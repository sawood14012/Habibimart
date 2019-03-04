package com.example.sawood.habibimart;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class emptycart extends android.support.v4.app.Fragment {

    public Button conti;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View layout = inflater.inflate(R.layout.cart_empty,container,false);
        conti = (Button) layout.findViewById(R.id.conti);

        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });


        return layout;
    }


}
