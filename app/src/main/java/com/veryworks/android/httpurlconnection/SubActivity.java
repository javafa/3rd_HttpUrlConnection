package com.veryworks.android.httpurlconnection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SubActivity extends AppCompatActivity implements TaskInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Task.newTask(this);
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void postExecute(String result) {
        // button..... progresss...
    }
}