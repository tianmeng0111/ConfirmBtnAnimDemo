package com.tm.example.confirmbtnanimdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tm.example.confirmbtnanimdemo.view.BtnConfirm;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BtnConfirm btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConfirm = (BtnConfirm) findViewById(R.id.btn_confirm);

//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "onClick: ==============");
//            }
//        });

    }

    public void initial(View view) {
        btnConfirm.setInitial();
    }
}
