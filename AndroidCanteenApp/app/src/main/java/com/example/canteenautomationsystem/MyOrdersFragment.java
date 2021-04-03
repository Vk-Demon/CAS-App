package com.example.canteenautomationsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import www.sanju.motiontoast.MotionToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {

    public RecyclerView my_orders_list;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference requests,get_user;

    FirebaseRecyclerOptions<Request> options;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    String get_name,get_phone,get_email;
    Double user_due;
    LottieAnimationView lottie_list_order;
    FButton lottie_my_orders_below_shop_cart;
    SharedPref sharedPref;

    public MyOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mo_root_view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        // Establishing connection to firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        lottie_list_order = (LottieAnimationView)mo_root_view.findViewById(R.id.lottie_list_order);
        lottie_my_orders_below_shop_cart = (FButton)mo_root_view.findViewById(R.id.lottie_my_orders_below_shop_cart);

        sharedPref = new SharedPref(getActivity());
        if(sharedPref.loadNightModeState()){
            lottie_list_order.setAnimation(R.raw.emptydark);
            lottie_my_orders_below_shop_cart.setButtonColor(Color.parseColor("#90EE90"));
            lottie_my_orders_below_shop_cart.setShadowColor(Color.parseColor("#90CE90"));
        }
        else{
            lottie_list_order.setAnimation(R.raw.emptylight);
            lottie_my_orders_below_shop_cart.setButtonColor(Color.parseColor("#87CEFA"));
            lottie_my_orders_below_shop_cart.setShadowColor(Color.parseColor("#87AEFA"));
        }

        lottie_my_orders_below_shop_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                int count = fm.getBackStackEntryCount();
                for(int i=0; i<count; ++i){
                    fm.popBackStack();
                }
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new FeaturedFragment()).commit();
                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        my_orders_list = (RecyclerView)mo_root_view.findViewById(R.id.my_orders_list);
        my_orders_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        my_orders_list.setLayoutManager(layoutManager);

        get_user = database.getReference("Users");
        Query query = get_user.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String due = "" + ds.child("due").getValue();

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

        load_orders(get_email);

        check_orders_are_empty();

        return mo_root_view;
    }

    private void load_orders(final String get_email) {

        Query user_req = requests.orderByChild("email").equalTo(firebaseUser.getEmail());

        options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(user_req,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, final int i, @NonNull final Request request) {
                orderViewHolder.my_orders_id.setText(String.format("Order ID : %s",adapter.getRef(i).getKey()));
                orderViewHolder.my_orders_date.setText(String.format("Ordered on : %s",request.getDate()));
                orderViewHolder.my_orders_status.setText(String.format("Status : %s",convertCodeToStatus(request.getStatus())));
                orderViewHolder.my_orders_name.setText(String.format("Name : %s",get_name));
                orderViewHolder.my_orders_phone.setText(String.format("Phone no : %s",get_phone));
                orderViewHolder.my_orders_email.setText(String.format("Email ID : %s",firebaseUser.getEmail()));
                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        final int pos = position;
                        if(!isLongClick){
                            orderViewHolder.my_orders_view_order_details_ll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent oda = new Intent(getActivity(),MyOrderDetailActivity.class);
                                    Common.currentRequest = request;
                                    oda.putExtra("OrderId",adapter.getRef(pos).getKey());
                                    startActivity(oda);
                                }
                            });
                        }
                        else{
                            MDToast.makeText(getActivity(),"Order "+adapter.getRef(position).getKey(),MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                        }
                    }
                });
                orderViewHolder.my_orders_cancel_order_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ConnectionManager.checkConnection(getActivity().getBaseContext())){

                            final CustomDialogProgress mof = new CustomDialogProgress(getActivity());

                            final AlertDialog.Builder builder_alert = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                            View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alert_dialog,null);
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

                            alert_title_text.setText("Cancel Order");
                            alert_message_text.setText("Do you wish to cancel your order?");
                            alert_message_icon.setImageResource(R.drawable.ic_remove_shopping_cart_alert_dialog_50dp);
                            alert_no_text.setText("Don't cancel");
                            alert_yes_text.setText("Cancel Order");

                            final AlertDialog dialog_alert = builder_alert.create();
                            if(dialog_alert.getWindow() != null){
                                dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                            }
                            dialog_alert.show();

                            alert_yes_rl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_alert.dismiss();
                                    if(adapter.getItem(i).getStatus().equals("0")){

                                        mof.startCustomDialogProgress("Cancelling order...");

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
                                            Toast.makeText(getActivity(), "Due: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        assert number != null;
                                        double order_due = number.doubleValue();

                                        // Get due balance of the user:

                                        double due_cancel = user_due - order_due;

                                        // Update due balance:

                                        get_user.child(firebaseUser.getUid()).child("due").setValue(String.valueOf(due_cancel));

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Cancel Order:
                                                delete_order(adapter.getRef(i).getKey());
                                                mof.dismissDialog();

                                            }
                                        },3000);
                                    }
                                    else{
                                        MotionToast.Companion.createColorToast(getActivity(),"Your order is ready! Collect your order at our premises. (Cancel order time expired)",
                                                MotionToast.TOAST_WARNING,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                    }
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
                        else{
                            checkConnectionStatus();
                        }
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mo_ovh_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.orders_layout,parent,false);
                return new OrderViewHolder(mo_ovh_view);
            }
        };
        my_orders_list.setAdapter(adapter);
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
                final OrderCancelDialog order_cancelled_dialog = new OrderCancelDialog(getActivity());

                order_cancelled_dialog.startOrderCancelDialog(key);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        order_cancelled_dialog.dismissDialog();
                        // Refresh Orders list:
                        load_orders(get_email);
                        check_orders_are_empty();

                    }
                },2600);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MotionToast.Companion.createColorToast(getActivity(),""+e.getMessage(),
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
            }
        });
    }

    public void check_orders_are_empty(){
        Query check = requests.orderByChild("email").equalTo(firebaseUser.getEmail());
        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    my_orders_list.setVisibility(View.VISIBLE);
                    lottie_my_orders_below_shop_cart.setVisibility(View.INVISIBLE);
                }
                else {
                    my_orders_list.setVisibility(View.INVISIBLE);
                    lottie_my_orders_below_shop_cart.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }

    // Check Internet Connectivity:

    public void checkConnectionStatus(){
        final AlertDialog.Builder builder_info = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_info_dialog,null);
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
                if(ConnectionManager.checkConnection(getActivity().getBaseContext())){
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
