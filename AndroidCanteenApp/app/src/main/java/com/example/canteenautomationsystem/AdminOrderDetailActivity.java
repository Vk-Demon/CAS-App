package com.example.canteenautomationsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.canteenautomationsystem.Common.Common;
import com.example.canteenautomationsystem.ViewHolder.AdminOrderDetailAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminOrderDetailActivity extends AppCompatActivity {

    TextView admin_orders_detail_id,admin_orders_detail_date,admin_orders_detail_email,admin_orders_detail_name,admin_orders_detail_phone,admin_orders_detail_status,admin_orders_detail_total_amt,admin_order_detail_item_total,admin_order_detail_item_total_amt;
    String admin_order_id_value="";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    RecyclerView admin_list_foods;
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
        setContentView(R.layout.activity_admin_order_detail);

        // Initializing variables:

        admin_orders_detail_id = findViewById(R.id.admin_orders_detail_id);
        admin_orders_detail_email = findViewById(R.id.admin_orders_detail_email);
        admin_orders_detail_status = findViewById(R.id.admin_orders_detail_status);
        admin_orders_detail_date = findViewById(R.id.admin_orders_detail_date);
        admin_orders_detail_name = findViewById(R.id.admin_orders_detail_name);
        admin_orders_detail_phone = findViewById(R.id.admin_orders_detail_phone);
        admin_orders_detail_total_amt = findViewById(R.id.admin_orders_detail_total_amt);
        admin_order_detail_item_total = findViewById(R.id.admin_order_detail_item_total);
        admin_order_detail_item_total_amt = findViewById(R.id.admin_order_detail_item_total_amt);

        admin_list_foods = (RecyclerView) findViewById(R.id.admin_list_foods);
        admin_list_foods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        admin_list_foods.setLayoutManager(layoutManager);

        if(getIntent() != null)
            admin_order_id_value = getIntent().getStringExtra("OrderId");

        admin_orders_detail_id.setText(String.format("Order ID : %s",admin_order_id_value));
        admin_orders_detail_date.setText(String.format("Ordered on : %s",Common.currentRequest.getDate()));
        admin_orders_detail_name.setText(String.format("Name : %s",Common.currentRequest.getName()));
        admin_orders_detail_phone.setText(String.format("Phone no : %s",Common.currentRequest.getPhone()));
        admin_orders_detail_email.setText(String.format("Email ID : %s",Common.currentRequest.getEmail()));
        admin_orders_detail_status.setText(String.format("Status : %s",convertCodeToStatus(Common.currentRequest.getStatus())));

        AdminOrderDetailAdapter adapter = new AdminOrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        admin_list_foods.setAdapter(adapter);

        admin_order_detail_item_total_amt.setText(String.format("%s",Common.currentRequest.getTotal()));
        admin_order_detail_item_total.setText(String.format("%s",Common.currentRequest.getTotal()));
        admin_orders_detail_total_amt.setText(String.format("%s",Common.currentRequest.getTotal()));

    }

    private String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Order Placed (Your order is yet to be processed)";
        }
        else if(status.equals("1")){
            return "Order Ready (Please collect your order at our premises)";
        }
        else {
            return "Order Cancelled";
        }
    }

}
