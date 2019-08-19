package com.example.resumeparser2;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ResumeParser2 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
