package com.example.canteenautomationsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import www.sanju.motiontoast.MotionToast;

public class InternetConnectivityActivity extends AppCompatActivity {

    FrameLayout icn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connectivity);

        icn_retry = findViewById(R.id.icn_retry);

        icn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionManager.checkConnection(getBaseContext())){
                    startActivity(new Intent(InternetConnectivityActivity.this,SplashActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else{
                    MotionToast.Companion.darkColorToast(InternetConnectivityActivity.this, "Still not connected!",
                            MotionToast.TOAST_NO_INTERNET,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(InternetConnectivityActivity.this, R.font.helvetica_regular));
                }
            }
        });

    }
}
