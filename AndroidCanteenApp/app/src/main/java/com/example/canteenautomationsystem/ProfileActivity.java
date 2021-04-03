package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declaring Firebase Authentication:

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Declaring toolbar Variable(s):

    private Toolbar user_profile_toolbar;

    // Declaring nav_Drawer Variable(s):

    private DrawerLayout user_profile_drawerLayout;
    private NavigationView user_profile_navigationView;
    TextView user_profile_sign_out,user_profile_about_us;
    View up_nav_head_view;
    private Switch dark_mode_switch;
    SharedPref sharedPref;
    ImageView profile_cart,profile_favourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState()){
            setTheme(R.style.darkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Establishing Connection to Firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        // Initializing toolbar variable(s):

        user_profile_toolbar = findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(user_profile_toolbar);

        // Initializing nav_Drawer variable(s):

        user_profile_drawerLayout = findViewById(R.id.user_profile_drawer_layout);
        user_profile_navigationView = findViewById(R.id.user_profile_nav_view);
        user_profile_sign_out = findViewById(R.id.user_profile_sign_out);
        user_profile_about_us = findViewById(R.id.user_profile_about_us);
        profile_cart = findViewById(R.id.profile_cart);
        profile_favourites = findViewById(R.id.profile_favourites);


        // Profile cart click event(s):

        profile_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_cart.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.blink));

                Intent cart = new Intent(ProfileActivity.this,CartActivity.class);
                cart.putExtra("Order","Cart");
                startActivity(cart);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        // Profile favourites click event(s):

        profile_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_favourites.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.blink));
                startActivity(new Intent(ProfileActivity.this,FavouritesActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        // nav_Drawer template:

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                ProfileActivity.this,
                user_profile_drawerLayout,
                user_profile_toolbar,
                R.string.open,
                R.string.close
        );

        user_profile_drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
       // actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.neonGreen));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(sharedPref.loadNightModeState()){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons_menu_a);
        }
        else{
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons_menu_c);
        }

        user_profile_navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeaturedFragment()).commit();
            user_profile_navigationView.setCheckedItem(R.id.nav_menu_featured);
        }
                // To display User profile Email ID in nav_Drawer:

                up_nav_head_view = user_profile_navigationView.getHeaderView(0);
                final TextView user_profile_email_id = (TextView)up_nav_head_view.findViewById(R.id.user_profile_email_id);
                user_profile_email_id.setText(firebaseUser.getEmail());

                // To display Due Balance:

                final TextView user_profile_due_balance = (TextView)up_nav_head_view.findViewById(R.id.user_profile_due_balance);

                // To display profile logo:

                final LottieAnimationView nav_header_lottie_profile = (LottieAnimationView)up_nav_head_view.findViewById(R.id.nav_header_lottie_profile);
                final ImageView nav_header_logo_img = (ImageView)up_nav_head_view.findViewById(R.id.nav_header_logo_img);

               if(sharedPref.loadNightModeState()){
                    nav_header_lottie_profile.setAnimation(R.raw.navheadphotodark);
                    nav_header_logo_img.setImageResource(R.drawable.lcdarkg);
                }
                else{
                    nav_header_lottie_profile.setAnimation(R.raw.navheadphotolight);
                   nav_header_logo_img.setImageResource(R.drawable.lclightb);
                }

        Query query_due = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query_due.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Checks until required data are fetched:
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    // Get data:
                    String get_due =""+ ds.child("due").getValue();

                    // Set data:
                    user_profile_due_balance.setText(get_due);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        user_profile_navigationView.setItemTextAppearance(R.style.NavigationDrawerStyle);

        // Switch App Mode (Light/Dark):

        dark_mode_switch = (Switch) up_nav_head_view.findViewById(R.id.dark_mode_toggle);
        if(sharedPref.loadNightModeState()){
            dark_mode_switch.setChecked(true);
        }
        dark_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                    restart_app();
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons_menu_a);
                    nav_header_lottie_profile.setAnimation(R.raw.navheadphotodark);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nav_header_logo_img.setImageResource(R.drawable.lcdarkg);
                        }
                    },100);
                }
                else{
                    sharedPref.setNightModeState(false);
                    restart_app();
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons_menu_c);
                    nav_header_lottie_profile.setAnimation(R.raw.navheadphotolight);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nav_header_logo_img.setImageResource(R.drawable.lclightb);
                        }
                    },100);
                }
            }
        });

        // Get and Display Username in navDrawer:

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        final TextView user_profile_username = (TextView)up_nav_head_view.findViewById(R.id.user_profile_username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Checks until required data are fetched:
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    // Get data:
                    String name =""+ ds.child("name").getValue();

                    // Set data:
                    user_profile_username.setText(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // About us Click Event(s):

        user_profile_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_profile_about_us.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.blink));

                final AlertDialog.Builder builder_info = new AlertDialog.Builder(ProfileActivity.this,R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.custom_info_dialog,null);
                builder_info.setView(dialog_view);
                builder_info.setCancelable(false);

                final CardView info_message_card = (CardView)dialog_view.findViewById(R.id.info_message_card);
                final RelativeLayout info_close_rl = (RelativeLayout)dialog_view.findViewById(R.id.info_close_rl);

                final TextView info_close_text = (TextView)dialog_view.findViewById(R.id.info_close_text);
                final TextView info_title_text = (TextView)dialog_view.findViewById(R.id.info_title_text);
                final TextView info_message_text = (TextView)dialog_view.findViewById(R.id.info_message_text);
                final ImageView info_message_icon = (ImageView)dialog_view.findViewById(R.id.info_message_icon);

                info_title_text.setText("About us");
                info_message_text.setText("\nCAS Developed and Owned by :-\n\n@Savage_Coders");
                info_message_text.setGravity(Gravity.CENTER);
                info_message_icon.setImageResource(R.drawable.ic_group_info_50dp);
                info_close_text.setText("Close");

                final AlertDialog dialog_info = builder_info.create();
                if(dialog_info.getWindow() != null){
                    dialog_info.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog_info.show();

                info_close_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_info.dismiss();
                    }
                });

                info_message_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To Block Dialog dismiss
                    }
                });

            }
        });

        // Sign out click event(s) :

        user_profile_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_profile_sign_out.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.blink));
                final CustomDialogProgress progressDialog_sign_out = new CustomDialogProgress(ProfileActivity.this);

                final AlertDialog.Builder builder_alert = new AlertDialog.Builder(ProfileActivity.this,R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.custom_alert_dialog,null);
                builder_alert.setView(dialog_view);
                builder_alert.setCancelable(false);

                final RelativeLayout alert_yes_rl = (RelativeLayout)dialog_view.findViewById(R.id.alert_yes_rl);
                final RelativeLayout alert_no_rl = (RelativeLayout)dialog_view.findViewById(R.id.alert_no_rl);
                final CardView alert_message_card = (CardView)dialog_view.findViewById(R.id.alert_message_card);

                final TextView alert_yes_text = (TextView)dialog_view.findViewById(R.id.alert_yes_text);
                final TextView alert_no_text = (TextView)dialog_view.findViewById(R.id.alert_no_text);
                final TextView alert_title_text = (TextView)dialog_view.findViewById(R.id.alert_title_text);
                final TextView alert_message_text = (TextView)dialog_view.findViewById(R.id.alert_message_text);
                final ImageView alert_message_icon = (ImageView)dialog_view.findViewById(R.id.alert_message_icon);

                alert_title_text.setText("Sign Out");
                alert_message_text.setText("Are you sure want to Sign out from CAS?");
                alert_message_icon.setImageResource(R.drawable.ic_power_settings_alert_dialog_50dp);
                alert_no_text.setText("Stay!");
                alert_yes_text.setText("Sign out");

                final AlertDialog dialog_alert = builder_alert.create();
                if(dialog_alert.getWindow() != null){
                    dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog_alert.show();

                alert_yes_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_alert.dismiss();
                        if(user_profile_drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            user_profile_drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        progressDialog_sign_out.startCustomDialogProgress("Signing out...!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                                Intent prof_intent = new Intent(ProfileActivity.this,LoginActivity.class);
                                prof_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                prof_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(prof_intent);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                progressDialog_sign_out.dismissDialog();
                            }
                        },3000);
                    }
                });

                alert_no_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_alert.dismiss();
                    }
                });

                alert_message_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To Block Dialog dismiss
                    }
                });

            }
        });

    }

    // nav_Drawer menu Click Event(s):

    Fragment fragment = null;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_menu_featured:
                fragment = new FeaturedFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                break;
            case R.id.nav_menu_orders:
                fragment = new OrdersFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrdersFragment()).commit();
                break;
            case R.id.nav_menu_account:
                fragment = new AccountFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                break;
            case R.id.nav_menu_support:
                fragment = new SupportFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SupportFragment()).commit();
                break;

        }
        user_profile_drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // Restart after Switch (Light/Dark) mode state:

    public void restart_app(){
        this.recreate();
    }

    // Back button Click Event(s):

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

            if(user_profile_drawerLayout.isDrawerOpen(GravityCompat.START)) {
                user_profile_drawerLayout.closeDrawer(GravityCompat.START);
            }
            else{
                if(fragment instanceof AccountFragment && count == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    if(fragment instanceof OrdersFragment){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    }
                    if(fragment instanceof SupportFragment){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    }
                }
                else if(fragment instanceof OrdersFragment && count == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    if(fragment instanceof SupportFragment){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    }
                    if(fragment instanceof AccountFragment){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    }
                }
                else if(fragment instanceof SupportFragment && count == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    if(fragment instanceof AccountFragment){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    }
                    if(fragment instanceof OrdersFragment){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                    }
                }
                else if(fragment instanceof FeaturedFragment){
                    finishAffinity();
                    finish();
                }
                else {
                    super.onBackPressed();
                }
            }
    }
}
