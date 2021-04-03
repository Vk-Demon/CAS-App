package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.canteenautomationsystem.Common.Common;
import com.example.canteenautomationsystem.Database.Database;
import com.example.canteenautomationsystem.Model.Order;
import com.example.canteenautomationsystem.Model.Request;
import com.example.canteenautomationsystem.Model.Users;
import com.example.canteenautomationsystem.ViewHolder.CartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import www.sanju.motiontoast.MotionToast;

public class CartActivity extends AppCompatActivity {

    RecyclerView list_cart;
    RecyclerView.LayoutManager layoutManager;

    TextView cart_total_amt;
    FButton cart_place_order_btn,lottie_below_shop_cart;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    LottieAnimationView lottie_list_cart;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference requests,get_user;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    SharedPref sharedPref;
    Double cart_amt,user_due;
    ImageView cart_info_dialog;
    Toolbar my_cart_toolbar;
    RelativeLayout my_cart_rl;
    TextView lottie_below_text;

    String get_name,get_phone,get_email,dateTime,order_req;

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
        setContentView(R.layout.activity_cart);

        // Establishing connection to firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy, hh:mm:ss a");
        dateTime = simpleDateFormat.format(calendar.getTime());

        // Init view:

        list_cart = (RecyclerView)findViewById(R.id.list_cart);
        list_cart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list_cart.setLayoutManager(layoutManager);

        cart_total_amt = (TextView)findViewById(R.id.cart_total_amt);
        cart_place_order_btn = (FButton)findViewById(R.id.cart_place_order_btn);
        lottie_list_cart = (LottieAnimationView)findViewById(R.id.lottie_list_cart);
        cart_info_dialog = (ImageView)findViewById(R.id.cart_info_dialog);
        my_cart_toolbar = (Toolbar)findViewById(R.id.my_cart_toolbar);
        my_cart_rl = (RelativeLayout)findViewById(R.id.my_cart_rl);
        lottie_below_text = (TextView)findViewById(R.id.lottie_below_text);
        lottie_below_shop_cart = (FButton)findViewById(R.id.lottie_below_shop_cart);


        cart_place_order_btn.setShadowEnabled(true);
        cart_place_order_btn.setShadowHeight(10);

        if(sharedPref.loadNightModeState()){
            cart_place_order_btn.setButtonColor(Color.parseColor("#39FF14"));
            cart_place_order_btn.setShadowColor(Color.parseColor("#39DF14"));
            lottie_below_shop_cart.setButtonColor(Color.parseColor("#90EE90"));
            lottie_below_shop_cart.setShadowColor(Color.parseColor("#90CE90"));
            lottie_list_cart.setAnimation(R.raw.emptydark);
        }
        else{
            cart_place_order_btn.setButtonColor(Color.parseColor("#87CEFA"));
            cart_place_order_btn.setShadowColor(Color.parseColor("#87AEFA"));
            lottie_below_shop_cart.setButtonColor(Color.parseColor("#87CEFA"));
            lottie_below_shop_cart.setShadowColor(Color.parseColor("#87AEFA"));
            lottie_list_cart.setAnimation(R.raw.emptylight);
        }

        cart_info_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart_info_func();
            }
        });

        get_user = database.getReference("Users");
        Query query = get_user.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String due = ""+ ds.child("due").getValue();

                    get_name = name;
                    get_email = email;
                    get_phone = phone;
                    user_due = Double.valueOf(due);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cart_place_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String c_t_amt = cart_total_amt.getText().toString();
                if(!c_t_amt.equals("₹0.00")){
                    if(ConnectionManager.checkConnection(getBaseContext())){
                        show_place_order_alert_dialog();
                    }
                    else{
                        checkConnectionStatus();
                    }
                }
                else{
                    final InfoAsToastDialog infoAsToastDialog = new InfoAsToastDialog(CartActivity.this);
                    infoAsToastDialog.startInfoAsToastDialog("Info","Your cart is empty!\nAdd some items to cart!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoAsToastDialog.dismissDialog();
                        }
                    },2000);
                }
            }
        });

        lottie_below_shop_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,ProfileActivity.class));
                finishAffinity();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        // To get snacks_id from the snacks menu:
        if(getIntent() != null)
            order_req = getIntent().getStringExtra("Order");

        assert order_req != null;
        if(!order_req.equals("Cart")) {
            if(ConnectionManager.checkConnection(getBaseContext())){
                my_cart_toolbar.setVisibility(View.INVISIBLE);
                my_cart_rl.setVisibility(View.INVISIBLE);
                lottie_list_cart.setVisibility(View.INVISIBLE);
                lottie_below_text.setVisibility(View.INVISIBLE);
                lottie_below_shop_cart.setVisibility(View.INVISIBLE);
                loadListFood();
                check_cart_is_empty();
                direct_place_order();
            }
            else{
                checkConnectionStatus();
            }
        }
        else{
            loadListFood();
            check_cart_is_empty();
        }

    }

    private void show_place_order_alert_dialog(){

        final OrderPlacedDialog order_success_dialog = new OrderPlacedDialog(CartActivity.this);
        final CustomDialogProgress pb = new CustomDialogProgress(CartActivity.this);
        final AlertDialog.Builder builder_alert = new AlertDialog.Builder(CartActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(CartActivity.this).inflate(R.layout.custom_alert_dialog,null);
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

        alert_title_text.setText("Place Order");
        alert_message_text.setText("Would you like to confirm your order?");
        alert_message_icon.setImageResource(R.drawable.ic_shopping_cart_alert_dialog_50dp);
        alert_no_text.setText("Cancel");
        alert_yes_text.setText("Place order");

        final AlertDialog dialog_alert = builder_alert.create();
        if(dialog_alert.getWindow() != null){
            dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_alert.show();

        alert_yes_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_alert.dismiss();
                check_cart_is_empty();
                pb.startCustomDialogProgress("Placing your order...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Create new request:

                        Random rand = new Random();
                        final String rand_id = String.valueOf(rand.nextInt(999));
                        final String comp_name = "cas";
                        final String sep = "-";
                        final String get_time_id = String.valueOf(rand.nextInt(9999));
                        final String get_req_id = comp_name + sep + get_time_id + sep + rand_id;

                        Request request = new Request(
                                get_req_id,
                                get_phone,
                                get_name,
                                get_email,
                                firebaseUser.getUid(),
                                dateTime,
                                cart_total_amt.getText().toString(),
                                cart
                        );

                        // Submit to firebase ( We will use System.CurrentMilli ) to key:
                        requests.child(get_req_id)
                                .setValue(request);

                        // Delete cart:
                        new Database(getBaseContext()).cleanCart();

                        // Update due balance:

                        get_user.child(firebaseUser.getUid()).child("due").setValue(String.valueOf(cart_amt + user_due));
                        pb.dismissDialog();
                        order_success_dialog.startOrderPlacedDialog();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                order_success_dialog.dismissDialog();
                                finish();
                            }
                        },2800);
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

    private void direct_place_order(){

        final OrderPlacedDialog order_success_dialog = new OrderPlacedDialog(CartActivity.this);
        final CustomDialogProgress pb = new CustomDialogProgress(CartActivity.this);
        final AlertDialog.Builder builder_alert = new AlertDialog.Builder(CartActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(CartActivity.this).inflate(R.layout.custom_alert_dialog,null);
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

        final String tot = cart_total_amt.getText().toString();

        alert_title_text.setText("Place Order");
        alert_message_text.setText("Would you like to buy this item for "+tot+"?");
        Picasso.get().load(order_req).into(alert_message_icon);
        alert_message_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        alert_no_text.setText("Cancel");
        alert_yes_text.setText("Place order");

        final AlertDialog dialog_alert = builder_alert.create();
        if(dialog_alert.getWindow() != null){
            dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_alert.show();

        alert_yes_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_alert.dismiss();
                pb.startCustomDialogProgress("Placing your order...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Create new request:

                        Random rand = new Random();
                        final String rand_id = String.valueOf(rand.nextInt(999));
                        final String comp_name = "cas";
                        final String sep = "-";
                        final String get_time_id = String.valueOf(rand.nextInt(9999));
                        final String get_req_id = comp_name + sep + get_time_id + sep + rand_id;

                        Request request = new Request(
                                get_req_id,
                                get_phone,
                                get_name,
                                get_email,
                                firebaseUser.getUid(),
                                dateTime,
                                cart_total_amt.getText().toString(),
                                cart
                        );

                        // Submit to firebase ( We will use System.CurrentMilli ) to key:
                        requests.child(get_req_id)
                                .setValue(request);

                        // Delete cart:
                        new Database(getBaseContext()).cleanCart();

                        // Update due balance:

                        get_user.child(firebaseUser.getUid()).child("due").setValue(String.valueOf(cart_amt + user_due));
                        pb.dismissDialog();
                        order_success_dialog.startOrderPlacedDialog();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                order_success_dialog.dismissDialog();
                                finish();
                            }
                        },2800);
                    }
                },3000);
            }
        });

        alert_no_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_alert.dismiss();
                // Delete cart:
                new Database(getBaseContext()).cleanCart();
                finish();
            }
        });

        alert_message_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To Block Dialog dismiss
            }
        });

    }

    private void loadListFood() {

    cart = new Database(this).getCarts();
    adapter = new CartAdapter(cart,this);
    adapter.notifyDataSetChanged();
    list_cart.setAdapter(adapter);

    // Calculate total price:
    int total = 0;
    for (Order order:cart)
        total+=(Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("hi","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        cart_total_amt.setText(fmt.format(total));

        cart_amt = (double) total;

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Delete"))
            deleteCart(item.getOrder());
        return true;
    }
    private void deleteCart(int position){
        // We will remove item at List<Orders> by position:
        cart.remove(position);
        // After that , we will delete all old data from SQLite:
        new Database(this).cleanCart();
        // We will update new data from List<Order> to SQLite
        for (Order item:cart)
            new Database(this).addToCart(item);
        // Refresh:
        loadListFood();
        // Status of cart:
        check_cart_is_empty();
    }

    // To check whether the cart is empty:

    public void check_cart_is_empty(){
        final String l_amt = cart_total_amt.getText().toString();
        if(l_amt.equals("₹0.00"))
        {
            list_cart.setVisibility(View.INVISIBLE);
            lottie_below_shop_cart.setVisibility(View.VISIBLE);
        }
        else{
            list_cart.setVisibility(View.VISIBLE);
            lottie_below_shop_cart.setVisibility(View.INVISIBLE);
        }
    }

    // Check Internet Connectivity:

    public void checkConnectionStatus(){

        final AlertDialog.Builder builder_info = new AlertDialog.Builder(CartActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(CartActivity.this).inflate(R.layout.custom_info_dialog,null);
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
                if(ConnectionManager.checkConnection(getBaseContext())){
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

    // Cart dialog (info):

    public void cart_info_func(){
         final AlertDialog.Builder builder_info = new AlertDialog.Builder(CartActivity.this,R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(CartActivity.this).inflate(R.layout.custom_info_dialog,null);
                builder_info.setView(dialog_view);
                builder_info.setCancelable(false);

                final CardView info_message_card = (CardView)dialog_view.findViewById(R.id.info_message_card);
                final RelativeLayout info_close_rl = (RelativeLayout)dialog_view.findViewById(R.id.info_close_rl);

                final TextView info_close_text = (TextView)dialog_view.findViewById(R.id.info_close_text);
                final TextView info_title_text = (TextView)dialog_view.findViewById(R.id.info_title_text);
                final TextView info_message_text = (TextView)dialog_view.findViewById(R.id.info_message_text);
                final ImageView info_message_icon = (ImageView)dialog_view.findViewById(R.id.info_message_icon);

                info_title_text.setText("Info");
                info_message_text.setText("\nDelete item from cart:\n\nLong press the cart item to view the delete button");
                info_message_icon.setImageResource(R.drawable.ic_info_info_50dp);
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

  /*  public Boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }*/

}
