package com.example.canteenautomationsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.Model.Beverages;
import com.example.canteenautomationsystem.ViewHolder.BeveragesMenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class BeveragesFragment extends Fragment {

    public BeveragesFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase database;
    DatabaseReference beverages_ref;
    RecyclerView recycler_beverages_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Beverages> options;
    FirebaseRecyclerAdapter<Beverages, BeveragesMenuViewHolder> adapter;
    ProgressBar beverages_progress_bar;
    SwipeRefreshLayout beverages_swipe_refresh;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View bf_root_view = inflater.inflate(R.layout.fragment_beverages, container, false);

        beverages_progress_bar = (ProgressBar)bf_root_view.findViewById(R.id.beverages_progress_bar);
        beverages_swipe_refresh = (SwipeRefreshLayout)bf_root_view.findViewById(R.id.beverages_swipe_refresh);

        // Establishing connection to firebase:

        database = FirebaseDatabase.getInstance();
        beverages_ref = database.getReference("Beverages");

        // Load menu:

        recycler_beverages_menu = (RecyclerView)bf_root_view.findViewById(R.id.recycler_beverages_menu);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_beverages_menu.setLayoutManager(layoutManager);

        // Swipe Refresh Listener:

        beverages_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onStart();
                        beverages_swipe_refresh.setRefreshing(false);
                    }
                },1500);
            }
        });

        return bf_root_view;
    }

    @Override
    public void onStart() {
        super.onStart();

        beverages_progress_bar.setVisibility(View.VISIBLE);

        options = new FirebaseRecyclerOptions.Builder<Beverages>()
                .setQuery(beverages_ref,Beverages.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Beverages, BeveragesMenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BeveragesMenuViewHolder beveragesMenuViewHolder, final int i, @NonNull Beverages beverages) {

                beveragesMenuViewHolder.beverages_menu_name.setText(beverages.getName());
                Picasso.get().load(beverages.getImage()).into(beveragesMenuViewHolder.beverages_menu_image);

                beverages_progress_bar.setVisibility(View.INVISIBLE);

                beveragesMenuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent beverages_item = new Intent(getActivity(),BeveragesItemActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),beveragesMenuViewHolder.beverages_menu_image, Objects.requireNonNull(ViewCompat.getTransitionName(beveragesMenuViewHolder.beverages_menu_image)));
                        beverages_item.putExtra("BeveragesId",adapter.getRef(position).getKey());
                        startActivity(beverages_item,optionsCompat.toBundle());
                    }
                });

            }

            @NonNull
            @Override
            public BeveragesMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View bf_mvh_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.beverages_menu_item,parent,false);
                return new BeveragesMenuViewHolder(bf_mvh_view);
            }
        };

        recycler_beverages_menu.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
