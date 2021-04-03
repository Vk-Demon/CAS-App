package com.example.canteenautomationsystem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.R;

public class SnacksMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView snacks_menu_name;
    public ImageView snacks_menu_image;
    private ItemClickListener itemClickListener;

    public SnacksMenuViewHolder(@NonNull View itemView) {
        super(itemView);

        snacks_menu_name = (TextView)itemView.findViewById(R.id.snacks_menu_name);
        snacks_menu_image = (ImageView)itemView.findViewById(R.id.snacks_menu_image);

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
