package com.example.sawood.habibimart;

import com.google.firebase.database.FirebaseDatabase;

public class myfire extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }


}
