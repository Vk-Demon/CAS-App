package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.canteenautomationsystem.Database.Database;
import com.example.canteenautomationsystem.Model.Favourites;
import com.example.canteenautomationsystem.Model.Order;
import com.example.canteenautomationsystem.Model.Snacks;
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

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import info.hoang8f.widget.FButton;
import it.sephiroth.android.library.numberpicker.NumberPicker;
import www.sanju.motiontoast.MotionToast;

public class SnacksItemActivity extends AppCompatActivity {

    // Declaring variable(s):

    TextView snacks_menu_item_name,snacks_menu_item_price,snacks_menu_item_total_amt;
    ImageView snacks_menu_item_image;
    CollapsingToolbarLayout snacks_item_collapse_layout;
    FloatingActionButton snacks_item_float_btn;
    NumberPicker snacks_menu_item_quantity;
    FButton snacks_add_to_fav,snacks_place_order;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference snacks_item_ref,favourites;
    SharedPref sharedPref;

    String snacks_id="";
    Snacks current_snacks;
    Integer siq_total_global;
    String  siq_quantity;

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
        setContentView(R.layout.activity_snacks_item);

        // Establishing connection to firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        snacks_item_ref = database.getReference("Snacks");
        favourites = database.getReference("Favourites");

        // Init view:

        snacks_item_collapse_layout = (CollapsingToolbarLayout)findViewById(R.id.snacks_item_collapse_layout);
        snacks_item_float_btn = (FloatingActionButton)findViewById(R.id.snacks_item_float_btn);
        snacks_menu_item_quantity = (NumberPicker) findViewById(R.id.snacks_menu_item_quantity);
        snacks_menu_item_price = (TextView)findViewById(R.id.snacks_menu_item_price);
        snacks_menu_item_name = (TextView)findViewById(R.id.snacks_menu_item_name);
        snacks_menu_item_image = (ImageView)findViewById(R.id.snacks_menu_item_image);
        snacks_add_to_fav = (FButton)findViewById(R.id.snacks_add_to_fav);
        snacks_place_order = (FButton)findViewById(R.id.snacks_place_order);
        snacks_menu_item_total_amt = (TextView)findViewById(R.id.snacks_menu_item_total_amt);

        snacks_item_collapse_layout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        snacks_item_collapse_layout.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        snacks_add_to_fav.setCornerRadius(20);
        snacks_add_to_fav.setShadowEnabled(true);
        snacks_add_to_fav.setShadowHeight(12);

        snacks_place_order.setCornerRadius(20);
        snacks_place_order.setShadowEnabled(true);
        snacks_place_order.setShadowHeight(12);

        if(sharedPref.loadNightModeState()){
            snacks_add_to_fav.setButtonColor(Color.parseColor("#39FF14"));
            snacks_add_to_fav.setShadowColor(Color.parseColor("#39DF14"));

            snacks_place_order.setButtonColor(Color.parseColor("#39FF14"));
            snacks_place_order.setShadowColor(Color.parseColor("#39DF14"));
        }
        else{
            snacks_add_to_fav.setButtonColor(Color.parseColor("#87CEFA"));
            snacks_add_to_fav.setShadowColor(Color.parseColor("#87AEFA"));

            snacks_place_order.setButtonColor(Color.parseColor("#87CEFA"));
            snacks_place_order.setShadowColor(Color.parseColor("#87AEFA"));
        }

        // To get snacks_id from the snacks menu:
        if(getIntent() != null)
            snacks_id = getIntent().getStringExtra("SnacksId");

        assert snacks_id != null;
        if(!snacks_id.isEmpty()) {
            getDetailSnacks(snacks_id);
        }


        snacks_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ConnectionManager.checkConnection(getBaseContext())){

                    final String email_cat_id_check = firebaseUser.getEmail() + "-" + current_snacks.getName();

                    final Query query_fav = favourites.orderByChild("emailcat").equalTo(email_cat_id_check);
                    query_fav.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(SnacksItemActivity.this);
                                infoAsToastDialog.startInfoAsToastDialog("Info","Item already available in your favourites!");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        infoAsToastDialog.dismissDialog();
                                    }
                                },2000);
                            }
                            else {

                                final AddToFavDialog addToFavDialog = new AddToFavDialog(SnacksItemActivity.this);
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

        snacks_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direct_place_order();
            }
        });

        final AddToCartDialog dialog_add_to_cart = new AddToCartDialog(SnacksItemActivity.this);
        snacks_item_float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(snacks_menu_item_quantity.getProgress() > 0  && snacks_menu_item_quantity != null  && snacks_menu_item_quantity.getProgress() < 50){

                    dialog_add_to_cart.startAddToCartDialog();

                   new Database(getBaseContext()).addToCart(new Order(
                            snacks_id,
                            current_snacks.getName(),
                            siq_quantity,
                            current_snacks.getPrice(),
                            current_snacks.getDiscount()
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
                    if(snacks_menu_item_quantity.getProgress() > 50){
                        final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(SnacksItemActivity.this);
                        infoAsToastDialog.startInfoAsToastDialog("Warning","Item quantity exceeded its limit!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                infoAsToastDialog.dismissDialog();
                            }
                        },2000);
                    }
                    else{
                        final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(SnacksItemActivity.this);
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
            final CustomDialogProgress customDialogProgress = new CustomDialogProgress(SnacksItemActivity.this);
            if(snacks_menu_item_quantity.getProgress() > 0  && snacks_menu_item_quantity != null  && snacks_menu_item_quantity.getProgress() < 50){
                customDialogProgress.startCustomDialogProgress("One more step...");
                // Delete previous items in the cart:
                new Database(getBaseContext()).cleanCart();

                // Adding current item to the cart:
                new Database(getBaseContext()).addToCart(new Order(
                        snacks_id,
                        current_snacks.getName(),
                        siq_quantity,
                        current_snacks.getPrice(),
                        current_snacks.getDiscount()
                ));


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customDialogProgress.dismissDialog();
                        Intent snacks_item = new Intent(SnacksItemActivity.this,CartActivity.class);
                        snacks_item.putExtra("Order",current_snacks.getImage());
                        startActivity(snacks_item);
                    }
                },2000);

            }
            else{
                if(snacks_menu_item_quantity.getProgress() > 50){
                    final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(SnacksItemActivity.this);
                    infoAsToastDialog.startInfoAsToastDialog("Warning","Item quantity exceeded its limit!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoAsToastDialog.dismissDialog();
                        }
                    },2000);
                }
                else{
                    final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(SnacksItemActivity.this);
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

    private void getDetailSnacks(String snacks_id) {

        snacks_item_ref.child(snacks_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_snacks = dataSnapshot.getValue(Snacks.class);

                assert current_snacks != null;
                Picasso.get().load(current_snacks.getImage()).into(snacks_menu_item_image);

                snacks_item_collapse_layout.setTitle(current_snacks.getName());
                snacks_menu_item_name.setText(current_snacks.getName());
                snacks_menu_item_price.setText(current_snacks.getPrice());

               final int snacks_item_current_quantity = Integer.parseInt(current_snacks.getPrice());

                snacks_menu_item_quantity.setNumberPickerChangeListener(new NumberPicker.OnNumberPickerChangeListener() {
                    @Override
                    public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
                        int siq_total = snacks_item_current_quantity * i;
                        snacks_menu_item_total_amt.setText(String.valueOf(siq_total));
                        siq_total_global = siq_total;
                        siq_quantity = String.valueOf(i);
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

        final String email_cat_id = firebaseUser.getEmail() + "-" + current_snacks.getName();

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
                snacks_id,
                "Snacks",
                current_snacks.getName(),
                current_snacks.getImage(),
                current_snacks.getPrice(),
                current_snacks.getDiscount()
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
        final AlertDialog.Builder builder_info = new AlertDialog.Builder(SnacksItemActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(SnacksItemActivity.this).inflate(R.layout.custom_info_dialog,null);
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
                if(ConnectionManager.checkConnection(SnacksItemActivity.this.getBaseContext())){
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
