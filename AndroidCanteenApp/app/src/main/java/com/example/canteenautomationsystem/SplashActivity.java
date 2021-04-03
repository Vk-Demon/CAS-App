package com.example.canteenautomationsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.canteenautomationsystem.Common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    // Declaring variable(s):

    RelativeLayout rltsplash;
    ImageView casimg;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Initializing variable(s):

        rltsplash = findViewById(R.id.rltsplash);
        casimg = findViewById(R.id.casimg);

        // Creating Splash Screen:

        Animation anim = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.splash_transition);
        casimg.startAnimation(anim);


        if(ConnectionManager.checkConnection(getBaseContext())){
            if(firebaseUser != null && firebaseUser.isEmailVerified()){
                if(firebaseUser.getEmail().equals(Common.ADMIN_EMAIL)){
                    final Intent v = new Intent(SplashActivity.this, AdminProfileActivity.class);
                    Thread timer = new Thread() {
                        public void run() {
                            try {
                                sleep(2200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                startActivity(v);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }
                    };
                    timer.start();
                }
                else {
                    final Intent p = new Intent(SplashActivity.this, ProfileActivity.class);
                    Thread timer = new Thread() {
                        public void run() {
                            try {
                                sleep(2200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                startActivity(p);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }
                    };
                    timer.start();
                }
            }
            else {

               if(sharedPref.loadStartActivityPager()){
                   final Intent i = new Intent(SplashActivity.this, StartActivity.class);
                   Thread timer = new Thread() {
                       public void run() {
                           try {
                               sleep(2200);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           } finally {
                               startActivity(i);
                               finish();
                               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                           }
                       }
                   };
                   timer.start();
               }
               else{
                   final Intent pa = new Intent(SplashActivity.this, LoginActivity.class);
                   Thread timer = new Thread() {
                       public void run() {
                           try {
                               sleep(2200);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           } finally {
                               startActivity(pa);
                               finish();
                               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                           }
                       }
                   };
                   timer.start();
               }
            }
        }
        else{
            final Intent icn = new Intent(SplashActivity.this, InternetConnectivityActivity.class);
            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(2200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(icn);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }
            };
            timer.start();
        }

    }
}
