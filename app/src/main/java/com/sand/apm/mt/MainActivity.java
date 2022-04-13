package com.sand.apm.mt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.orhanobut.logger.Logger;


/**
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d("Hello its beging..");
    }

}