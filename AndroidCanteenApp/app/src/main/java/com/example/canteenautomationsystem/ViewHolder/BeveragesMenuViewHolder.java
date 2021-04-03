package com.example.canteenautomationsystem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.R;

public class BeveragesMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView beverages_menu_name;
    public ImageView beverages_menu_image;
    private ItemClickListener itemClickListener;

    public BeveragesMenuViewHolder(@NonNull View itemView) {
        super(itemView);

        beverages_menu_name = (TextView)itemView.findViewById(R.id.beverages_menu_name);
        beverages_menu_image = (ImageView)itemView.findViewById(R.id.beverages_menu_image);

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
