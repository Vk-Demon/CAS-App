package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.Model.Favourites;
import com.example.canteenautomationsystem.ViewHolder.FavouritesMenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Objects;

public class FavouritesActivity extends AppCompatActivity {

    // Declaring variables:

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference favourites_ref;
    RecyclerView recycler_favourites_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Favourites> options;
    FirebaseRecyclerAdapter<Favourites, FavouritesMenuViewHolder> adapter;
    ProgressBar favourites_progress_bar;
    SwipeRefreshLayout favourites_swipe_refresh;
    ImageView favourites_cart,my_favourite_image;
    TextView my_favourite_text,my_favourites_text_brief;

    SharedPref sharedPref;

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
        setContentView(R.layout.activity_favourites);

        favourites_progress_bar = findViewById(R.id.favourites_progress_bar);
        favourites_swipe_refresh = findViewById(R.id.favourites_swipe_refresh);
        favourites_cart = findViewById(R.id.favourites_cart);
        my_favourite_image = findViewById(R.id.my_favourites_image);
        my_favourite_text = findViewById(R.id.my_favourites_text);
        my_favourites_text_brief = findViewById(R.id.my_favourites_text_brief);

        // Establishing connection to firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        favourites_ref = database.getReference("Favourites");

        // Favourites cart click event(s):

        favourites_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favourites_cart.startAnimation(AnimationUtils.loadAnimation(FavouritesActivity.this,R.anim.blink));
                startActivity(new Intent(FavouritesActivity.this,CartActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        // Load menu:

        recycler_favourites_menu = findViewById(R.id.recycler_favourites_menu);
        layoutManager = new LinearLayoutManager(FavouritesActivity.this);
        recycler_favourites_menu.setLayoutManager(layoutManager);

        check_fav_are_empty();

        // Swipe Refresh Listener:

        favourites_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onStart();
                        favourites_swipe_refresh.setRefreshing(false);
                    }
                },1500);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query_fav = favourites_ref.orderByChild("email").equalTo(firebaseUser.getEmail());

        options = new FirebaseRecyclerOptions.Builder<Favourites>()
                .setQuery(query_fav,Favourites.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Favourites, FavouritesMenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FavouritesMenuViewHolder menuViewHolder, final int i, @NonNull final Favourites favourites) {

                menuViewHolder.favourites_menu_name.setText(favourites.getItem());
                Picasso.get().load(favourites.getImage()).into(menuViewHolder.favourites_menu_image);

                favourites_progress_bar.setVisibility(View.INVISIBLE);

                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        // Start new activity (Common for all favourite item):

                        if(favourites.getCatname().equals("Snacks")){
                            Intent favourites_item = new Intent(FavouritesActivity.this,SnacksItemActivity.class);
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(FavouritesActivity.this,menuViewHolder.favourites_menu_image, Objects.requireNonNull(ViewCompat.getTransitionName(menuViewHolder.favourites_menu_image)));
                            favourites_item.putExtra("SnacksId",favourites.getCatid());
                            startActivity(favourites_item,optionsCompat.toBundle());
                        }
                        else if(favourites.getCatname().equals("Meals")){
                            Intent favourites_item = new Intent(FavouritesActivity.this,MealsItemActivity.class);
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(FavouritesActivity.this,menuViewHolder.favourites_menu_image, Objects.requireNonNull(ViewCompat.getTransitionName(menuViewHolder.favourites_menu_image)));
                            favourites_item.putExtra("MealsId",favourites.getCatid());
                            startActivity(favourites_item,optionsCompat.toBundle());
                        }
                        else{
                            Intent favourites_item = new Intent(FavouritesActivity.this,BeveragesItemActivity.class);
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(FavouritesActivity.this,menuViewHolder.favourites_menu_image, Objects.requireNonNull(ViewCompat.getTransitionName(menuViewHolder.favourites_menu_image)));
                            favourites_item.putExtra("BeveragesId",favourites.getCatid());
                            startActivity(favourites_item,optionsCompat.toBundle());
                        }

                    }
                });

                menuViewHolder.favourites_menu_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(ConnectionManager.checkConnection(getBaseContext())){
                            delete_fav(adapter.getRef(i).getKey());
                        }
                        else{
                            checkConnectionStatus();
                        }

                    }
                });

                menuViewHolder.favourites_menu_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(ConnectionManager.checkConnection(getBaseContext())){
                            show_item_info(favourites.getCatname(),favourites.getItem(),favourites.getPrice(),favourites.getDiscount());
                        }
                        else{
                            checkConnectionStatus();
                        }

                    }
                });

            }

            @NonNull
            @Override
            public FavouritesMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View ff_mvh_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.favourites_menu_item,parent,false);
                return new FavouritesMenuViewHolder(ff_mvh_view);
            }
        };

        recycler_favourites_menu.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // Delete item from favourites:

    private void delete_fav(final String key){

        final CustomDialogProgress progressDialog_delete_fav = new CustomDialogProgress(FavouritesActivity.this);

        final AlertDialog.Builder builder_alert = new AlertDialog.Builder(FavouritesActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(FavouritesActivity.this).inflate(R.layout.custom_alert_dialog,null);
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

        alert_title_text.setText("Delete item");
        alert_message_text.setText("This will delete the current item from your favourites!");
        alert_message_icon.setImageResource(R.drawable.ic_delete_alert_50dp);
        alert_no_text.setText("Don't delete");
        alert_yes_text.setText("Delete");

        final AlertDialog dialog_alert = builder_alert.create();
        if(dialog_alert.getWindow() != null){
            dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_alert.show();

        alert_yes_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_alert.dismiss();
                progressDialog_delete_fav.startCustomDialogProgress("Deleting item...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog_delete_fav.dismissDialog();
                        favourites_ref.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final RemoveFromFavDialog removeFromFavDialog = new RemoveFromFavDialog(FavouritesActivity.this);
                                removeFromFavDialog.startRemoveFromFavDialog("Item removed from your favourites!");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeFromFavDialog.dismissDialog();
                                    }
                                },2000);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                final RemoveFromFavDialog removeFromFavDialog = new RemoveFromFavDialog(FavouritesActivity.this);
                                removeFromFavDialog.startRemoveFromFavDialog(e.getMessage());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeFromFavDialog.dismissDialog();
                                    }
                                },2000);
                            }
                        });

                        // Refresh fav list:
                        onStart();
                        check_fav_are_empty();

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

    // Display current item info:

    private void show_item_info(String cat_name, String item_name,String item_price, String item_discount){

        final AlertDialog.Builder builder_info = new AlertDialog.Builder(FavouritesActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(FavouritesActivity.this).inflate(R.layout.custom_info_dialog,null);
        builder_info.setView(dialog_view);
        builder_info.setCancelable(false);

        final CardView info_message_card = (CardView)dialog_view.findViewById(R.id.info_message_card);
        final RelativeLayout info_close_rl = (RelativeLayout)dialog_view.findViewById(R.id.info_close_rl);

        final TextView info_close_text = (TextView)dialog_view.findViewById(R.id.info_close_text);
        final TextView info_title_text = (TextView)dialog_view.findViewById(R.id.info_title_text);
        final TextView info_message_text = (TextView)dialog_view.findViewById(R.id.info_message_text);
        final ImageView info_message_icon = (ImageView)dialog_view.findViewById(R.id.info_message_icon);

        info_title_text.setText("Item info");
        info_message_text.setText(String.format("Name : %s\n\nPrice : ₹%s.00\n\nDiscount : ₹%s.00",item_name,item_price,item_discount));
        //info_message_text.setGravity(Gravity.CENTER);
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

    // Check Internet Connectivity:

    public void checkConnectionStatus(){
        final AlertDialog.Builder builder_info = new AlertDialog.Builder(FavouritesActivity.this,R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(FavouritesActivity.this).inflate(R.layout.custom_info_dialog,null);
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
                if(ConnectionManager.checkConnection(FavouritesActivity.this.getBaseContext())){
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

    public void check_fav_are_empty(){

        favourites_progress_bar.setVisibility(View.VISIBLE);

        Query check = favourites_ref.orderByChild("email").equalTo(firebaseUser.getEmail());
        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    recycler_favourites_menu.setVisibility(View.VISIBLE);
                    my_favourite_image.setVisibility(View.INVISIBLE);
                    my_favourite_text.setVisibility(View.INVISIBLE);
                    my_favourites_text_brief.setVisibility(View.INVISIBLE);
                }
                else {
                    recycler_favourites_menu.setVisibility(View.INVISIBLE);
                    favourites_progress_bar.setVisibility(View.INVISIBLE);
                    my_favourite_image.setVisibility(View.VISIBLE);
                    my_favourite_text.setVisibility(View.VISIBLE);
                    my_favourites_text_brief.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
