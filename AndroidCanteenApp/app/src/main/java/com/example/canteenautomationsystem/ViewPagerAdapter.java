package com.example.canteenautomationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;


public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    // Arrays:

    public int[] slide_images = {
            R.raw.preorder,
            R.raw.freshfood,
            R.raw.cashonspot,
            R.raw.loginjs
    };

    public String[] slide_titles = {
            "Order at-ease",
            "Fresh foods",
            "Cash on Spot",
            "Update Profile"
    };

    public String[] slide_desc = {
            "Just add the food to cart and place your order",
            "Top classy meals with liquid and substantial refreshments",
            "Your orders are to be paid at our premises. Online payment process is not available",
            "Do not forget to update your profile before placing your order"
    };

    @Override
    public int getCount() {
        return slide_titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_slide_layout,container,false);

        //ImageView slideImageView = view.findViewById(R.id.vp_img);
        LottieAnimationView slideImageView = view.findViewById(R.id.vp_img);
        TextView slidetitle = view.findViewById(R.id.vp_title);
        TextView slidedesc = view.findViewById(R.id.vp_desc);
        slideImageView.setAnimation(slide_images[position]);
        slidetitle.setText(slide_titles[position]);
        slidedesc.setText(slide_desc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
