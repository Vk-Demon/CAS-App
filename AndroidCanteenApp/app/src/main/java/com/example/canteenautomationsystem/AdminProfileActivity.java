package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenautomationsystem.Common.Common;
import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.Model.Request;
import com.example.canteenautomationsystem.ViewHolder.OrderViewHolder;
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import www.sanju.motiontoast.MotionToast;

public class AdminProfileActivity extends AppCompatActivity {

   // TextView admin_profile_email;
    ImageView admin_profile_sign_out;
    public RecyclerView admin_request_orders_list;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference requests,get_user;

    Double user_due;
    Integer get_status_code;
    private Switch admin_dark_mode_switch;

    FirebaseRecyclerOptions<Request> options;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

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
        setContentView(R.layout.activity_admin_profile);

       // admin_profile_email = findViewById(R.id.admin_profile_email);
        admin_profile_sign_out = findViewById(R.id.admin_profile_sign_out);
        admin_dark_mode_switch = findViewById(R.id.admin_dark_mode_toggle);

        if(sharedPref.loadNightModeState()){
            admin_dark_mode_switch.setChecked(true);
        }
        admin_dark_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                    restart_app();
                }
                else{
                    sharedPref.setNightModeState(false);
                    restart_app();
                }
            }
        });

        // Establishing connection to firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        admin_request_orders_list = (RecyclerView)findViewById(R.id.admin_request_orders_list);
        admin_request_orders_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        admin_request_orders_list.setLayoutManager(layoutManager);

        get_user = database.getReference("Users");

        load_request_orders();

        //assert firebaseUser != null;
        //admin_profile_email.setText(firebaseUser.getEmail());

        // Admin Profile Sign out:

        admin_profile_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               admin_profile_sign_out.startAnimation(AnimationUtils.loadAnimation(AdminProfileActivity.this,R.anim.blink));

                final CustomDialogProgress progressDialog_sign_out = new CustomDialogProgress(AdminProfileActivity.this);

                final AlertDialog.Builder builder_alert = new AlertDialog.Builder(AdminProfileActivity.this,R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(AdminProfileActivity.this).inflate(R.layout.custom_alert_dialog,null);
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

                alert_title_text.setText("Sign out");
                alert_message_text.setText("Are you sure want to Sign out?");
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
                        progressDialog_sign_out.startCustomDialogProgress("Signing out...!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog_alert.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                Intent admin_prof_intent = new Intent(AdminProfileActivity.this,LoginActivity.class);
                                admin_prof_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                admin_prof_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(admin_prof_intent);
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

    private void load_request_orders(){

        options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, final int i, @NonNull final Request request) {
                orderViewHolder.admin_request_orders_id.setText(String.format("Order ID : %s",adapter.getRef(i).getKey()));
                orderViewHolder.admin_request_orders_date.setText(String.format("Ordered on : %s",request.getDate()));
                orderViewHolder.admin_request_orders_status.setText(String.format("Status : %s",convertCodeToStatus(request.getStatus())));
                orderViewHolder.admin_request_orders_name.setText(String.format("Name : %s",request.getName()));
                orderViewHolder.admin_request_orders_phone.setText(String.format("Phone no : %s",request.getPhone()));
                orderViewHolder.admin_request_orders_email.setText(String.format("Email ID : %s",request.getEmail()));
                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                       final int pos = position;
                       if(!isLongClick){
                           orderViewHolder.admin_request_orders_view_order_details_ll.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent apa = new Intent(AdminProfileActivity.this,AdminOrderDetailActivity.class);
                                   Common.currentRequest = request;
                                   apa.putExtra("OrderId",adapter.getRef(pos).getKey());
                                   startActivity(apa);
                               }
                           });
                       }
                       else{
                           Toast.makeText(AdminProfileActivity.this, "Order "+adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                       }

                    }
                });
                orderViewHolder.admin_request_orders_change_status_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String get_status = request.getStatus();

                        final CustomDialogProgress progressDialog_change_status = new CustomDialogProgress(AdminProfileActivity.this);

                        final AlertDialog.Builder builder_alert = new AlertDialog.Builder(AdminProfileActivity.this,R.style.AlertDialogTheme);
                        View dialog_view = LayoutInflater.from(AdminProfileActivity.this).inflate(R.layout.custom_alert_dialog,null);
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

                        alert_title_text.setText("Change Status");
                        alert_message_text.setText("Do you wish to change the status of order?");
                        alert_message_icon.setImageResource(R.drawable.ic_info_info_50dp);
                        alert_no_text.setText("Hold");
                        alert_yes_text.setText("Ready");

                        final AlertDialog dialog_alert = builder_alert.create();
                        if(dialog_alert.getWindow() != null){
                            dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        dialog_alert.show();

                        alert_yes_rl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_alert.dismiss();
                                progressDialog_change_status.startCustomDialogProgress("Changing order status...");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog_alert.dismiss();

                                        Query query = requests.orderByChild(Objects.requireNonNull(adapter.getRef(i).getKey()));
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    String st = ""+ ds.child("status").getValue();
                                                    get_status_code = Integer.valueOf(st);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        // Changing/updating status code:

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                requests.child(Objects.requireNonNull(adapter.getRef(i).getKey())).child("status").setValue(String.valueOf(1));
                                                progressDialog_change_status.dismissDialog();
                                            }
                                        },5000);

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
                orderViewHolder.admin_request_orders_delete_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final CustomDialogProgress progressDialog_delete_order = new CustomDialogProgress(AdminProfileActivity.this);

                        final AlertDialog.Builder builder_alert = new AlertDialog.Builder(AdminProfileActivity.this,R.style.AlertDialogTheme);
                        View dialog_view = LayoutInflater.from(AdminProfileActivity.this).inflate(R.layout.custom_alert_dialog,null);
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

                        alert_title_text.setText("Delete Order");
                        alert_message_text.setText("Do you wish to delete the order request?");
                        alert_message_icon.setImageResource(R.drawable.ic_delete_alert_50dp);
                        alert_no_text.setText("Don't delete");
                        alert_yes_text.setText("Delete order");

                        final AlertDialog dialog_alert = builder_alert.create();
                        if(dialog_alert.getWindow() != null){
                            dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        dialog_alert.show();

                        alert_yes_rl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_alert.dismiss();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog_alert.dismiss();

                                        if(adapter.getItem(i).getStatus().equals("0") || (adapter.getItem(i).getStatus().equals("1"))){
                                            progressDialog_delete_order.startCustomDialogProgress("Deleting order...");

                                            Query query = get_user.orderByChild("email").equalTo(request.getEmail());
                                            query.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        String due = ""+ ds.child("due").getValue();
                                                        user_due = Double.valueOf(due);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            // Get due balance for the ordered item:

                                            Locale locale = new Locale("hi","IN");
                                            NumberFormat cfj = NumberFormat.getCurrencyInstance(locale);
                                            Number number = null;
                                            try
                                            {
                                                number = cfj.parse(request.getTotal());
                                            }
                                            catch (ParseException e)
                                            {
                                                Toast.makeText(AdminProfileActivity.this, "Due: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            assert number != null;
                                            final double order_due = number.doubleValue();

                                            // Get due balance of the user:

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final double due_cancel = user_due - order_due;
                                                    get_user.child(request.getUid()).child("due").setValue(String.valueOf(due_cancel));
                                                    delete_order(adapter.getRef(i).getKey());
                                                    progressDialog_delete_order.dismissDialog();
                                                }
                                            },5000);

                                        }
                                        else{
                                            MotionToast.Companion.darkColorToast(AdminProfileActivity.this,"You cannot delete the order",
                                                    MotionToast.TOAST_WARNING,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(AdminProfileActivity.this, R.font.helvetica_regular));
                                        }

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

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View apa_ovh_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.admin_order_requests_layout,parent,false);
                return new OrderViewHolder(apa_ovh_view);
            }
        };
        admin_request_orders_list.setAdapter(adapter);
        adapter.startListening();
    }
    private String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Order Placed";
        }
        else if(status.equals("1")){
            return "Order Ready";
        }
        else {
            return "Order Cancelled";
        }
    }

    private void delete_order(final String key){
        requests.child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MotionToast.Companion.darkColorToast(AdminProfileActivity.this,new StringBuilder("Order ")
                                .append(key)
                                .append(" has been deleted").toString(),
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(AdminProfileActivity.this, R.font.helvetica_regular));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MotionToast.Companion.darkColorToast(AdminProfileActivity.this,""+e.getMessage(),
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(AdminProfileActivity.this, R.font.helvetica_regular));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminProfileActivity.this,AdminProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    // Restart after Switch (Light/Dark) mode state:

    public void restart_app(){
        this.recreate();
    }

}
