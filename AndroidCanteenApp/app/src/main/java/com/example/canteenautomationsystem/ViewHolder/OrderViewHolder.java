package com.example.canteenautomationsystem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public TextView my_orders_id,my_orders_date,my_orders_name,my_orders_status,my_orders_phone,my_orders_email;

    public TextView admin_request_orders_id,admin_request_orders_date,admin_request_orders_name,admin_request_orders_status,admin_request_orders_phone,admin_request_orders_email;

    public LinearLayout my_orders_view_order_details_ll,my_orders_cancel_order_ll;

    public LinearLayout admin_request_orders_view_order_details_ll,admin_request_orders_change_status_ll,admin_request_orders_delete_ll;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        // User send request:

        my_orders_id = (TextView)itemView.findViewById(R.id.my_orders_id);
        my_orders_status = (TextView)itemView.findViewById(R.id.my_orders_status);
        my_orders_name = (TextView)itemView.findViewById(R.id.my_orders_name);
        my_orders_date = (TextView)itemView.findViewById(R.id.my_orders_date);
        my_orders_phone = (TextView)itemView.findViewById(R.id.my_orders_phone);
        my_orders_email = (TextView)itemView.findViewById(R.id.my_orders_email);

        my_orders_view_order_details_ll= (LinearLayout)itemView.findViewById(R.id.my_orders_view_order_details_ll);
        my_orders_cancel_order_ll= (LinearLayout)itemView.findViewById(R.id.my_orders_cancel_order_ll);

        // Admin receive request:

        admin_request_orders_id = (TextView)itemView.findViewById(R.id.admin_request_orders_id);
        admin_request_orders_status = (TextView)itemView.findViewById(R.id.admin_request_orders_status);
        admin_request_orders_name = (TextView)itemView.findViewById(R.id.admin_request_orders_name);
        admin_request_orders_date = (TextView)itemView.findViewById(R.id.admin_request_orders_date);
        admin_request_orders_phone = (TextView)itemView.findViewById(R.id.admin_request_orders_phone);
        admin_request_orders_email = (TextView)itemView.findViewById(R.id.admin_request_orders_email);

        admin_request_orders_view_order_details_ll = (LinearLayout)itemView.findViewById(R.id.admin_request_orders_view_order_details_ll);
        admin_request_orders_change_status_ll = (LinearLayout)itemView.findViewById(R.id.admin_request_orders_change_status_ll);
        admin_request_orders_delete_ll = (LinearLayout)itemView.findViewById(R.id.admin_request_orders_delete_ll);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

    }

    public void setItemClickListener (ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }
}
