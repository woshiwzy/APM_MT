package com.sand.apm.mt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}