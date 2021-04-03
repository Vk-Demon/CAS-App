package com.example.canteenautomationsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.canteenautomationsystem.Interface.ItemClickListener;
import com.example.canteenautomationsystem.Model.Meals;
import com.example.canteenautomationsystem.ViewHolder.MealsMenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MealsFragment extends Fragment {

    public MealsFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase database;
    DatabaseReference meals_ref;
    RecyclerView recycler_meals_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Meals> options;
    FirebaseRecyclerAdapter<Meals, MealsMenuViewHolder> adapter;
    ProgressBar meals_progress_bar;
    SwipeRefreshLayout meals_swipe_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mf_root_view =  inflater.inflate(R.layout.fragment_meals, container, false);

        meals_progress_bar = (ProgressBar)mf_root_view.findViewById(R.id.meals_progress_bar);
        meals_swipe_refresh = (SwipeRefreshLayout)mf_root_view.findViewById(R.id.meals_swipe_refresh);

        // Establishing connection to firebase:

        database = FirebaseDatabase.getInstance();
        meals_ref = database.getReference("Meals");

        recycler_meals_menu = (RecyclerView)mf_root_view.findViewById(R.id.recycler_meals_menu);
        recycler_meals_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_meals_menu.setLayoutManager(layoutManager);

        // Swipe Refresh Listener:

        meals_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onStart();
                        meals_swipe_refresh.setRefreshing(false);
                    }
                },1500);
            }
        });

        return mf_root_view;
    }

    @Override
    public void onStart() {
        super.onStart();

        meals_progress_bar.setVisibility(View.VISIBLE);

        options = new FirebaseRecyclerOptions.Builder<Meals>()
                .setQuery(meals_ref,Meals.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Meals, MealsMenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MealsMenuViewHolder menuViewHolder, int i, @NonNull Meals meals) {

                menuViewHolder.meals_menu_name.setText(meals.getName());
                Picasso.get().load(meals.getImage()).into(menuViewHolder.meals_menu_image);

                meals_progress_bar.setVisibility(View.INVISIBLE);

                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start new activity (Common for all meals item):
                        Intent meals_item = new Intent(getActivity(),MealsItemActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),menuViewHolder.meals_menu_image, Objects.requireNonNull(ViewCompat.getTransitionName(menuViewHolder.meals_menu_image)));
                        meals_item.putExtra("MealsId",adapter.getRef(position).getKey());
                        startActivity(meals_item,optionsCompat.toBundle());
                    }
                });
            }

            @NonNull
            @Override
            public MealsMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mf_mvh_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.meals_menu_item,parent,false);
                return new MealsMenuViewHolder(mf_mvh_view);
            }
        };

        recycler_meals_menu.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
