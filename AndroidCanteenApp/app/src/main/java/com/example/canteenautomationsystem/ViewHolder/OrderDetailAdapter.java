package com.example.canteenautomationsystem.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Model.Meals;
import com.example.canteenautomationsystem.Model.Order;
import com.example.canteenautomationsystem.Model.Snacks;
import com.example.canteenautomationsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{

    FirebaseDatabase database;
    DatabaseReference snacks_item_image_ref,meals_item_image_ref,beverages_item_image_ref;

    public TextView my_orders_detail_product_name,my_orders_detail_product_quantity,my_orders_detail_product_price,my_orders_detail_product_discount;

    public ImageView my_orders_detail_product_image;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        database = FirebaseDatabase.getInstance();
        snacks_item_image_ref = database.getReference("Snacks");
        meals_item_image_ref = database.getReference("Meals");
        beverages_item_image_ref = database.getReference("Beverages");

        my_orders_detail_product_name = (TextView)itemView.findViewById(R.id.my_orders_detail_product_name);
        my_orders_detail_product_quantity = (TextView)itemView.findViewById(R.id.my_orders_detail_product_quantity);
        my_orders_detail_product_price = (TextView)itemView.findViewById(R.id.my_orders_detail_product_price);
        my_orders_detail_product_discount = (TextView)itemView.findViewById(R.id.my_orders_detail_product_discount);
        my_orders_detail_product_image = (ImageView)itemView.findViewById(R.id.my_orders_detail_product_image);

    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Order order = myOrders.get(position);
        holder.my_orders_detail_product_name.setText(String.format("Name : %s",order.getProductName()));
        holder.my_orders_detail_product_quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.my_orders_detail_product_price.setText(String.format("Price : ₹%s.00/item",order.getPrice()));
        holder.my_orders_detail_product_discount.setText(String.format("Discount : ₹%s.00",order.getDiscount()));

        Query query_snacks = holder.snacks_item_image_ref.orderByChild("Name").equalTo(order.getProductName());
        query_snacks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                String image =""+ ds.child("Image").getValue();
                Picasso.get().load(image).into(holder.my_orders_detail_product_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query_meals = holder.meals_item_image_ref.orderByChild("Name").equalTo(order.getProductName());
        query_meals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image =""+ ds.child("Image").getValue();
                    Picasso.get().load(image).into(holder.my_orders_detail_product_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query_beverages = holder.beverages_item_image_ref.orderByChild("Name").equalTo(order.getProductName());
        query_beverages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image =""+ ds.child("Image").getValue();
                    Picasso.get().load(image).into(holder.my_orders_detail_product_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
