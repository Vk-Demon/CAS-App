package com.example.canteenautomationsystem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class StartActivity extends AppCompatActivity {

    private ViewPager startviewPager;
    private LinearLayout startdotlayer;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView[] mDots;
    private FrameLayout gsbtnstart;
    private ConstraintLayout rltstart;
    private AnimationDrawable animationDrawable;
    SharedPref sharedPref;

    //private int mCurrentpages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startviewPager = findViewById(R.id.startviewPager);
        startdotlayer = findViewById(R.id.startdotlayer);
        gsbtnstart = findViewById(R.id.gsbtnstart);
        rltstart = findViewById(R.id.rltstart);

        animationDrawable = (AnimationDrawable) rltstart.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(1800);
        animationDrawable.start();

        viewPagerAdapter = new ViewPagerAdapter(StartActivity.this);

        startviewPager.setPageTransformer(true,new ViewPagerZoomAnimation());

        startviewPager.setAdapter(viewPagerAdapter);

        addDotsIndicator(0);

        startviewPager.addOnPageChangeListener(viewListener);

          // Next Button Click Events:

        gsbtnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startviewPager.setCurrentItem(mCurrentpages + 1);
                sharedPref.setStartActivityPager(false);
                startActivity(new Intent(StartActivity.this,LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finishAffinity();
            }
        });

        /*// Back Button Click Events:

        backbtnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startviewPager.setCurrentItem(mCurrentpages - 1);
            }
        });*/


    }

    public void addDotsIndicator(int position){
        mDots = new TextView[4];
        startdotlayer.removeAllViews();

        for (int i=0 ; i<mDots.length; i++){
            mDots[i] = new TextView(StartActivity.this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.black));
            startdotlayer.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.brightOrange));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            if(position == mDots.length-1){
                gsbtnstart.setEnabled(true);
                gsbtnstart.setVisibility(View.VISIBLE);
            }
            else{
                gsbtnstart.setEnabled(false);
                gsbtnstart.setVisibility(View.INVISIBLE);
            }
            /*mCurrentpages = position;
            if(position == 0 ){
                nextbtnstart.setEnabled(true);
                backbtnstart.setEnabled(false);
                backbtnstart.setVisibility(View.INVISIBLE);
                nextbtnstart.setText("Next");
                backbtnstart.setText("");
            }
            else if(position == mDots.length - 1){
                nextbtnstart.setEnabled(true);
                backbtnstart.setEnabled(true);
                backbtnstart.setVisibility(View.VISIBLE);
                nextbtnstart.setText("Get Started");
                backbtnstart.setText("Back");
            }
            else {
                nextbtnstart.setEnabled(true);
                backbtnstart.setEnabled(true);
                backbtnstart.setVisibility(View.VISIBLE);
                nextbtnstart.setText("Next");
                backbtnstart.setText("Back");
            }*/
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


   /* @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }*/
}
