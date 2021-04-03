package com.example.canteenautomationsystem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.R;

public class FavouritesMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView favourites_menu_name;
    public ImageView favourites_menu_image,favourites_menu_delete,favourites_menu_info;
    private ItemClickListener itemClickListener;

    public FavouritesMenuViewHolder(@NonNull View itemView) {
        super(itemView);

        favourites_menu_name = (TextView)itemView.findViewById(R.id.favourites_menu_name);
        favourites_menu_image = (ImageView)itemView.findViewById(R.id.favourites_menu_image);
        favourites_menu_delete = (ImageView)itemView.findViewById(R.id.favourites_menu_delete);
        favourites_menu_info = (ImageView)itemView.findViewById(R.id.favourites_menu_info);

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
