package com.example.canteenautomationsystem;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.Model.Snacks;
import com.example.canteenautomationsystem.ViewHolder.SnacksMenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SnacksFragment extends Fragment {

    public SnacksFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase database;
    DatabaseReference snacks_ref;
    RecyclerView recycler_snacks_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Snacks> options;
    FirebaseRecyclerAdapter<Snacks, SnacksMenuViewHolder> adapter;
    ProgressBar snacks_progress_bar;
    SwipeRefreshLayout snacks_swipe_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sf_root_view = inflater.inflate(R.layout.fragment_snacks, container, false);

        snacks_progress_bar = (ProgressBar)sf_root_view.findViewById(R.id.snacks_progress_bar);
        snacks_swipe_refresh = (SwipeRefreshLayout)sf_root_view.findViewById(R.id.snacks_swipe_refresh);

        // Establishing connection to firebase:

        database = FirebaseDatabase.getInstance();
        snacks_ref = database.getReference("Snacks");

        // Load menu:

        recycler_snacks_menu = (RecyclerView)sf_root_view.findViewById(R.id.recycler_snacks_menu);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_snacks_menu.setLayoutManager(layoutManager);

        // Swipe Refresh Listener:

        snacks_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onStart();
                        snacks_swipe_refresh.setRefreshing(false);
                    }
                },1500);
            }
        });

        return sf_root_view;
    }

    @Override
    public void onStart() {
        super.onStart();

        snacks_progress_bar.setVisibility(View.VISIBLE);

        options = new FirebaseRecyclerOptions.Builder<Snacks>()
                .setQuery(snacks_ref,Snacks.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Snacks, SnacksMenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SnacksMenuViewHolder menuViewHolder, int i, @NonNull Snacks snacks) {

                menuViewHolder.snacks_menu_name.setText(snacks.getName());
                Picasso.get().load(snacks.getImage()).into(menuViewHolder.snacks_menu_image);

                snacks_progress_bar.setVisibility(View.INVISIBLE);

                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        // Start new activity (Common for all snacks item):
                        Intent snacks_item = new Intent(getActivity(),SnacksItemActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),menuViewHolder.snacks_menu_image, Objects.requireNonNull(ViewCompat.getTransitionName(menuViewHolder.snacks_menu_image)));
                        snacks_item.putExtra("SnacksId",adapter.getRef(position).getKey());
                        startActivity(snacks_item,optionsCompat.toBundle());
                    }
                });
            }

            @NonNull
            @Override
            public SnacksMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View sf_mvh_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.snacks_menu_item,parent,false);
                return new SnacksMenuViewHolder(sf_mvh_view);
            }
        };

        recycler_snacks_menu.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
