package com.example.canteenautomationsystem.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Model.Order;
import com.example.canteenautomationsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

class AdminMyViewHolder extends RecyclerView.ViewHolder{

    FirebaseDatabase database;
    DatabaseReference snacks_item_image_ref,meals_item_image_ref,beverages_item_image_ref;

    public TextView admin_order_detail_product_name,admin_order_detail_product_quantity,admin_order_detail_product_price,admin_order_detail_product_discount;

    public ImageView admin_orders_detail_product_image;

    public AdminMyViewHolder(@NonNull View itemView) {
        super(itemView);

        database = FirebaseDatabase.getInstance();
        snacks_item_image_ref = database.getReference("Snacks");
        meals_item_image_ref = database.getReference("Meals");
        beverages_item_image_ref = database.getReference("Beverages");

        admin_order_detail_product_name = (TextView)itemView.findViewById(R.id.admin_order_detail_product_name);
        admin_order_detail_product_quantity = (TextView)itemView.findViewById(R.id.admin_order_detail_product_quantity);
        admin_order_detail_product_price = (TextView)itemView.findViewById(R.id.admin_order_detail_product_price);
        admin_order_detail_product_discount = (TextView)itemView.findViewById(R.id.admin_order_detail_product_discount);
        admin_orders_detail_product_image = (ImageView)itemView.findViewById(R.id.admin_orders_detail_product_image);
    }
}

public class AdminOrderDetailAdapter extends RecyclerView.Adapter<AdminMyViewHolder>{


    List<Order> myOrders;

    public AdminOrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public AdminMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_order_detail_layout,parent,false);
        return new AdminMyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdminMyViewHolder holder, int position) {

        Order order = myOrders.get(position);
        holder.admin_order_detail_product_name.setText(String.format("Name : %s",order.getProductName()));
        holder.admin_order_detail_product_quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.admin_order_detail_product_price.setText(String.format("Price : ₹%s.00/item",order.getPrice()));
        holder.admin_order_detail_product_discount.setText(String.format("Discount : ₹%s.00",order.getDiscount()));

        Query query_snacks = holder.snacks_item_image_ref.orderByChild("Name").equalTo(order.getProductName());
        query_snacks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image =""+ ds.child("Image").getValue();
                    Picasso.get().load(image).into(holder.admin_orders_detail_product_image);
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
                    Picasso.get().load(image).into(holder.admin_orders_detail_product_image);
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
                    Picasso.get().load(image).into(holder.admin_orders_detail_product_image);
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
