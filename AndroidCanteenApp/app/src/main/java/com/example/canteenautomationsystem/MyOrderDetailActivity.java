package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.canteenautomationsystem.Common.Common;
import com.example.canteenautomationsystem.Model.Request;
import com.example.canteenautomationsystem.ViewHolder.OrderDetailAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyOrderDetailActivity extends AppCompatActivity {

    TextView my_orders_detail_id,my_orders_detail_date,my_orders_detail_name,my_orders_detail_phone,my_orders_detail_email,my_orders_detail_status,my_orders_detail_total_amt,my_orders_detail_item_total_amt,my_orders_detail_item_total;
    String order_id_value="";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference requests;

    RecyclerView list_foods;
    RecyclerView.LayoutManager layoutManager;

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
        setContentView(R.layout.activity_my_order_detail);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Initializing variables:

        my_orders_detail_id = findViewById(R.id.my_orders_detail_id);
        my_orders_detail_email = findViewById(R.id.my_orders_detail_email);
        my_orders_detail_status = findViewById(R.id.my_orders_detail_status);
        my_orders_detail_date= findViewById(R.id.my_orders_detail_date);
        my_orders_detail_name = findViewById(R.id.my_orders_detail_name);
        my_orders_detail_phone = findViewById(R.id.my_orders_detail_phone);
        my_orders_detail_item_total_amt = findViewById(R.id.my_orders_detail_item_total_amt);
        my_orders_detail_item_total = findViewById(R.id.my_orders_detail_item_total);
        my_orders_detail_total_amt = findViewById(R.id.my_orders_detail_total_amt);

        list_foods = (RecyclerView)findViewById(R.id.list_foods);
        list_foods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list_foods.setLayoutManager(layoutManager);

        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");

        my_orders_detail_id.setText(String.format("Order ID : %s",order_id_value));
        my_orders_detail_date.setText(String.format("Ordered on : %s",Common.currentRequest.getDate()));
        my_orders_detail_name.setText(String.format("Name : %s",Common.currentRequest.getName()));
        my_orders_detail_phone.setText(String.format("Phone no : %s",Common.currentRequest.getPhone()));
        my_orders_detail_email.setText(String.format("Email ID : %s",Common.currentRequest.getEmail()));
        my_orders_detail_status.setText(String.format("Status : %s",convertCodeToStatus(Common.currentRequest.getStatus())));

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        list_foods.setAdapter(adapter);

        my_orders_detail_item_total_amt.setText(String.format("%s",Common.currentRequest.getTotal()));
        my_orders_detail_item_total.setText(String.format("%s",Common.currentRequest.getTotal()));
        my_orders_detail_total_amt.setText(String.format("%s",Common.currentRequest.getTotal()));
    }

    private String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Order Placed (Your order is yet to be processed)";
        }
        else if(status.equals("1")){
            return "Order Ready (Collect your order at our premises)";
        }
        else {
            return "Order Cancelled";
        }
    }

}
