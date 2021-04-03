package com.example.canteenautomationsystem;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerZoomAnimation implements ViewPager.PageTransformer {

    private final float min_scale = 0.70f;
    private final float min_alpha = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {

        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if(position < -1){
            page.setAlpha(0f);
        }
        else if(position <= 1){

            float scaleFactor = Math.max(min_scale,1 - Math.abs(position));
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;
            if(position < 0){
                page.setTranslationX(horizontalMargin - verticalMargin / 2);
            }
            else{
                page.setTranslationX(- horizontalMargin + verticalMargin / 2);
            }
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            page.setAlpha(min_alpha + (scaleFactor - min_scale) / (1 - min_scale) * (1 - min_alpha));
        }
        else{
            page.setAlpha(0f);
        }

    }
}
