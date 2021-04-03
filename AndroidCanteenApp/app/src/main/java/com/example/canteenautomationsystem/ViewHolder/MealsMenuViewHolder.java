package com.example.canteenautomationsystem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.R;

public class MealsMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView meals_menu_name;
    public ImageView meals_menu_image;
    private ItemClickListener itemClickListener;

    public MealsMenuViewHolder(@NonNull View itemView) {
        super(itemView);

        meals_menu_name = (TextView)itemView.findViewById(R.id.meals_menu_name);
        meals_menu_image = (ImageView)itemView.findViewById(R.id.meals_menu_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener (ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
