package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenautomationsystem.Database.Database;
import com.example.canteenautomationsystem.Model.Favourites;
import com.example.canteenautomationsystem.Model.Meals;
import com.example.canteenautomationsystem.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import info.hoang8f.widget.FButton;
import it.sephiroth.android.library.numberpicker.NumberPicker;
import www.sanju.motiontoast.MotionToast;

public class MealsItemActivity extends AppCompatActivity {

    // Declaring variable(s):

    TextView meals_menu_item_name,meals_menu_item_price,meals_menu_item_total_amt;
    ImageView meals_menu_item_image;
    CollapsingToolbarLayout meals_item_collapse_layout;
    FloatingActionButton meals_item_float_btn;
    NumberPicker meals_menu_item_quantity;
    FButton meals_add_to_fav,meals_place_order;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference meals_item_ref,favourites;

    SharedPref sharedPref;

    Meals current_meals;

    String meals_id="";
    Integer miq_total_global;
    String miq_quantity;

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
        setContentView(R.layout.activity_meals_item);

        // Establishing connection to firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        meals_item_ref = database.getReference("Meals");
        favourites = database.getReference("Favourites");

        // Init view:

        meals_item_collapse_layout = (CollapsingToolbarLayout)findViewById(R.id.meals_item_collapse_layout);
        meals_item_float_btn = (FloatingActionButton)findViewById(R.id.meals_item_float_btn);
        meals_menu_item_quantity = (NumberPicker) findViewById(R.id.meals_menu_item_quantity);
        meals_menu_item_price = (TextView)findViewById(R.id.meals_menu_item_price);
        meals_menu_item_name = (TextView)findViewById(R.id.meals_menu_item_name);
        meals_menu_item_image = (ImageView)findViewById(R.id.meals_menu_item_image);
        meals_add_to_fav = (FButton)findViewById(R.id.meals_add_to_fav);
        meals_place_order = (FButton)findViewById(R.id.meals_place_order);
        meals_menu_item_total_amt = (TextView)findViewById(R.id.meals_menu_item_total_amt);

        meals_item_collapse_layout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        meals_item_collapse_layout.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        meals_add_to_fav.setCornerRadius(20);
        meals_add_to_fav.setShadowEnabled(true);
        meals_add_to_fav.setShadowHeight(12);

        if(sharedPref.loadNightModeState()){
            meals_add_to_fav.setButtonColor(Color.parseColor("#39FF14"));
            meals_add_to_fav.setShadowColor(Color.parseColor("#39DF14"));

            meals_place_order.setButtonColor(Color.parseColor("#39FF14"));
            meals_place_order.setShadowColor(Color.parseColor("#39DF14"));
        }
        else{
            meals_add_to_fav.setButtonColor(Color.parseColor("#87CEFA"));
            meals_add_to_fav.setShadowColor(Color.parseColor("#87AEFA"));

            meals_place_order.setButtonColor(Color.parseColor("#87CEFA"));
            meals_place_order.setShadowColor(Color.parseColor("#87AEFA"));
        }

        // To get meals_id from the meals menu:
        if(getIntent() != null)
            meals_id = getIntent().getStringExtra("MealsId");

        assert meals_id != null;
        if(!meals_id.isEmpty()) {
            getDetailMeals(meals_id);
        }

        meals_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ConnectionManager.checkConnection(getBaseContext())){

                    final String email_cat_id_check = firebaseUser.getEmail() + "-" + current_meals.getName();

                    final Query query_fav = favourites.orderByChild("emailcat").equalTo(email_cat_id_check);
                    query_fav.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(MealsItemActivity.this);
                                infoAsToastDialog.startInfoAsToastDialog("Info","Item already available in your favourites!");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        infoAsToastDialog.dismissDialog();
                                    }
                                },2000);
                            }
                            else {

                                final AddToFavDialog addToFavDialog = new AddToFavDialog(MealsItemActivity.this);
                                addToFavDialog.startAddToFavDialog();
                                add_to_favourites();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        addToFavDialog.dismissDialog();
                                    }
                                },2000);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    checkConnectionStatus();
                }

            }
        });

        meals_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   direct_place_order();
            }
        });

        final AddToCartDialog dialog_add_to_cart = new AddToCartDialog(MealsItemActivity.this);
        meals_item_float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(meals_menu_item_quantity.getProgress() > 0  &&  meals_menu_item_quantity != null  &&  meals_menu_item_quantity.getProgress() <50){

                  dialog_add_to_cart.startAddToCartDialog();

                    new Database(getBaseContext()).addToCart(new Order(
                        meals_id,
                        current_meals.getName(),
                        miq_quantity,
                        current_meals.getPrice(),
                        current_meals.getDiscount()
                ));

                new Handler().postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          dialog_add_to_cart.dismissDialog();
                          finish();
                      }
                },2200);
              }
              else{
                  if(meals_menu_item_quantity.getProgress() > 50){
                      final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(MealsItemActivity.this);
                      infoAsToastDialog.startInfoAsToastDialog("Warning","Item quantity exceeded its limit!");
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              infoAsToastDialog.dismissDialog();
                          }
                      },2000);
                  }
                  else{
                      final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(MealsItemActivity.this);
                      infoAsToastDialog.startInfoAsToastDialog("Warning","Item quantity should have a valid positive number!");
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              infoAsToastDialog.dismissDialog();
                          }
                      },2000);
                  }
              }
            }
        });

    }

    // Place order (direct):

    private void direct_place_order(){

        if(ConnectionManager.checkConnection(getBaseContext())){
            final CustomDialogProgress customDialogProgress = new CustomDialogProgress(MealsItemActivity.this);
            if(meals_menu_item_quantity.getProgress() > 0  &&  meals_menu_item_quantity != null  &&  meals_menu_item_quantity.getProgress() <50){
                customDialogProgress.startCustomDialogProgress("One more step...");
                // Delete previous items in the cart:
                new Database(getBaseContext()).cleanCart();

                // Adding current item to the cart:
                new Database(getBaseContext()).addToCart(new Order(
                        meals_id,
                        current_meals.getName(),
                        miq_quantity,
                        current_meals.getPrice(),
                        current_meals.getDiscount()
                ));


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customDialogProgress.dismissDialog();
                        Intent snacks_item = new Intent(MealsItemActivity.this,CartActivity.class);
                        snacks_item.putExtra("Order",current_meals.getImage());
                        startActivity(snacks_item);
                    }
                },2000);

            }
            else{
                if(meals_menu_item_quantity.getProgress() > 50){
                    final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(MealsItemActivity.this);
                    infoAsToastDialog.startInfoAsToastDialog("Warning","Item quantity exceeded its limit!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoAsToastDialog.dismissDialog();
                        }
                    },2000);
                }
                else{
                    final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(MealsItemActivity.this);
                    infoAsToastDialog.startInfoAsToastDialog("Warning","Item quantity should have a valid positive number!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoAsToastDialog.dismissDialog();
                        }
                    },2000);
                }
            }
        }
        else{
            checkConnectionStatus();
        }

    }

    private void getDetailMeals(String meals_id) {

        meals_item_ref.child(meals_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_meals = dataSnapshot.getValue(Meals.class);

                assert current_meals != null;
                Picasso.get().load(current_meals.getImage()).into(meals_menu_item_image);

                meals_item_collapse_layout.setTitle(current_meals.getName());
                meals_menu_item_name.setText(current_meals.getName());
                meals_menu_item_price.setText(current_meals.getPrice());

                final int meals_item_current_quantity = Integer.parseInt(current_meals.getPrice());

                meals_menu_item_quantity.setNumberPickerChangeListener(new NumberPicker.OnNumberPickerChangeListener() {
                    @Override
                    public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
                        int miq_total = meals_item_current_quantity * i;
                        meals_menu_item_total_amt.setText(String.valueOf(miq_total));
                        miq_total_global = miq_total;
                        miq_quantity = String.valueOf(i);
                    }

                    @Override
                    public void onStartTrackingTouch(@NotNull NumberPicker numberPicker) {

                    }

                    @Override
                    public void onStopTrackingTouch(@NotNull NumberPicker numberPicker) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Add to favourites:

    private void add_to_favourites(){

        // Creating unique key for particular user:

        final String email_cat_id = firebaseUser.getEmail() + "-" + current_meals.getName();

        // Creating fav id:

        Random rand = new Random();
        final String rand_id = String.valueOf(rand.nextInt(999));
        final String comp_name = "cas";
        final String sep = "-";
        final String fav = "fav";
        final String get_time_id = String.valueOf(rand.nextInt(9999));
        final String get_fav_id = comp_name + sep + fav + sep + get_time_id + sep + rand_id;

        // Creating Favourites list in firebase database:

        Favourites favourite = new Favourites(
                firebaseUser.getEmail(),
                email_cat_id,
                meals_id,
                "Meals",
                current_meals.getName(),
                current_meals.getImage(),
                current_meals.getPrice(),
                current_meals.getDiscount()
        );

        // Submit to firebase ( We will use System.CurrentMilli ) to key:

        favourites.child(get_fav_id).setValue(favourite);

    }

    // Clear Focus when clicked outside inputField:

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof TextInputEditText || v instanceof CheckBox) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Check Internet Connectivity:

    public void checkConnectionStatus(){
        final AlertDialog.Builder builder_info = new AlertDialog.Builder(MealsItemActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(MealsItemActivity.this).inflate(R.layout.custom_info_dialog,null);
        builder_info.setView(dialog_view);
        builder_info.setCancelable(false);

        final CardView info_message_card = (CardView)dialog_view.findViewById(R.id.info_message_card);
        final RelativeLayout info_close_rl = (RelativeLayout)dialog_view.findViewById(R.id.info_close_rl);

        final TextView info_close_text = (TextView)dialog_view.findViewById(R.id.info_close_text);
        final TextView info_title_text = (TextView)dialog_view.findViewById(R.id.info_title_text);
        final TextView info_message_text = (TextView)dialog_view.findViewById(R.id.info_message_text);
        final ImageView info_message_icon = (ImageView)dialog_view.findViewById(R.id.info_message_icon);

        info_title_text.setText("Network Issue");
        info_message_text.setText("\n\nPlease Check Your Internet Connection \nand Try Again");
        info_message_text.setGravity(Gravity.CENTER);
        info_message_icon.setImageResource(R.drawable.ic_signal_wifi_off_info_50dp);
        info_close_text.setText("Retry");

        final AlertDialog dialog_info = builder_info.create();
        if(dialog_info.getWindow() != null){
            dialog_info.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_info.show();

        info_close_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionManager.checkConnection(MealsItemActivity.this.getBaseContext())){
                    dialog_info.dismiss();
                }
                else{
                    checkConnectionStatus();
                }
            }
        });

        info_message_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To Block Dialog dismiss
            }
        });
    }

}
