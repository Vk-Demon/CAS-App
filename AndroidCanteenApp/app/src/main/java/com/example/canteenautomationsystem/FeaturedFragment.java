package com.example.canteenautomationsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FeaturedFragment extends Fragment {

    // Declaring tabLayout Fragments Variable(s):

    private SnacksFragment snacksFragment;
    private MealsFragment mealsFragment;
    private BeveragesFragment beveragesFragment;
    TabLayout featured_fragment_tabLayout;
    ViewPager featured_fragment_viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ff_root_view = inflater.inflate(R.layout.nav_drawer_featured_fragment,container,false);

        // Initializing tabLayout variable(s):

        featured_fragment_tabLayout = (TabLayout)ff_root_view.findViewById(R.id.featured_fragment_tabBar);
        TabItem featured_fragment_tabSnacks = (TabItem)ff_root_view.findViewById(R.id.featured_fragment_tabSnacks);
        TabItem featured_fragment_tabMeals = (TabItem)ff_root_view.findViewById(R.id.featured_fragment_tabMeals);
        TabItem featured_fragment_tabBeverages = (TabItem)ff_root_view.findViewById(R.id.featured_fragment_tabBeverages);
        featured_fragment_viewPager = (ViewPager)ff_root_view.findViewById(R.id.featured_fragment_viewPager);
        snacksFragment = new SnacksFragment();
        mealsFragment = new MealsFragment();
        beveragesFragment = new BeveragesFragment();

        // Tab Layout template:

        featured_fragment_tabLayout.setupWithViewPager(featured_fragment_viewPager);
        FeaturedFragment.ViewPagerAdapter viewPagerAdapter = new FeaturedFragment.ViewPagerAdapter(getChildFragmentManager(),3);
        featured_fragment_viewPager.setPageTransformer(true,new ViewPagerZoomAnimation());
        viewPagerAdapter.addFragment(snacksFragment,"Snacks");
        viewPagerAdapter.addFragment(mealsFragment,"Meals");
        viewPagerAdapter.addFragment(beveragesFragment,"Beverages");
        featured_fragment_viewPager.setAdapter(viewPagerAdapter);
        featured_fragment_viewPager.setCurrentItem(0,true);

        featured_fragment_tabLayout.getTabAt(0).setIcon(R.drawable.ic_hamburger_24dp);
        featured_fragment_tabLayout.getTabAt(1).setIcon(R.drawable.ic_meal_24dp);
        featured_fragment_tabLayout.getTabAt(2).setIcon(R.drawable.ic_drink_24dp);


        // Badge Drawable for tabLayout Fragment(s) (Notifications purpose):

                /*BadgeDrawable badgeDrawable = featured_fragment_tabLayout.getTabAt(0).getOrCreateBadge();
                badgeDrawable.setVisible(true);
                badgeDrawable.setNumber(2);*/
      return ff_root_view;

    }

    // ViewPagerAdapter for tabLayout (Change the tabs view when the tab is selected or clicked):


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
