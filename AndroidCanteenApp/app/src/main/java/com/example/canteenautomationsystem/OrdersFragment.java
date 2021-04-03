package com.example.canteenautomationsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class OrdersFragment extends Fragment {

    Toolbar orders_fragment_toolbar;
    FrameLayout orders_fragment_my_cart,orders_fragment_my_orders,orders_fragment_my_favourites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View of_root_view =  inflater.inflate(R.layout.nav_drawer_orders_fragment,container,false);

        orders_fragment_toolbar = (Toolbar)of_root_view.findViewById(R.id.orders_fragment_toolbar);
        orders_fragment_my_cart = (FrameLayout)of_root_view.findViewById(R.id.orders_fragment_my_cart);
        orders_fragment_my_orders = (FrameLayout)of_root_view.findViewById(R.id.orders_fragment_my_orders);
        orders_fragment_my_favourites = (FrameLayout)of_root_view.findViewById(R.id.orders_fragment_my_favourites);

        orders_fragment_my_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(getActivity(),CartActivity.class);
                cart.putExtra("Order","Cart");
                startActivity(cart);
                ((Activity) Objects.requireNonNull(getActivity())).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        orders_fragment_my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyOrdersFragment()).addToBackStack(null).commit();
            }
        });

        orders_fragment_my_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),FavouritesActivity.class));
                ((Activity) Objects.requireNonNull(getActivity())).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        return of_root_view;
    }
}
