package com.example.canteenautomationsystem.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.Model.Order;
import com.example.canteenautomationsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener{

    FirebaseDatabase database;
    DatabaseReference snacks_item_image_ref,meals_item_image_ref,beverages_item_image_ref;

    public TextView cart_item_name,cart_item_price;
    public ImageView cart_item_count,cart_item_image;

    private ItemClickListener itemClickListener;

    public void setCart_item_name(TextView cart_item_name) {
        this.cart_item_name = cart_item_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        database = FirebaseDatabase.getInstance();
        snacks_item_image_ref = database.getReference("Snacks");
        meals_item_image_ref = database.getReference("Meals");
        beverages_item_image_ref = database.getReference("Beverages");

        cart_item_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        cart_item_price = (TextView)itemView.findViewById(R.id.cart_item_price);
        cart_item_count = (ImageView)itemView.findViewById(R.id.cart_item_count);
        cart_item_image = (ImageView)itemView.findViewById(R.id.cart_item_image);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,0,getAdapterPosition(),"Delete");
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(),Color.TRANSPARENT);
        holder.cart_item_count.setImageDrawable(drawable);

        Locale locale = new Locale("hi","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.cart_item_price.setText(fmt.format(price));
        holder.cart_item_name.setText(listData.get(position).getProductName());

        Query query_snacks = holder.snacks_item_image_ref.orderByChild("Name").equalTo(listData.get(position).getProductName());
        query_snacks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image =""+ ds.child("Image").getValue();
                    Picasso.get().load(image).into(holder.cart_item_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query_meals = holder.meals_item_image_ref.orderByChild("Name").equalTo(listData.get(position).getProductName());
        query_meals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image =""+ ds.child("Image").getValue();
                    Picasso.get().load(image).into(holder.cart_item_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query_beverages = holder.beverages_item_image_ref.orderByChild("Name").equalTo(listData.get(position).getProductName());
        query_beverages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String image =""+ ds.child("Image").getValue();
                    Picasso.get().load(image).into(holder.cart_item_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}