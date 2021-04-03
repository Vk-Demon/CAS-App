package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;


import com.airbnb.lottie.LottieAnimationView;

public class AddToCartDialog {

    Activity activity;
    AlertDialog dialog_add_to_cart;
    SharedPref sharedPref;

    AddToCartDialog(Activity activity_add){
        activity = activity_add;
    }

    void startAddToCartDialog(){
        AlertDialog.Builder builder_add = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_add = activity.getLayoutInflater();
        View dialog_view = inflater_add.inflate(R.layout.add_to_cart_dialog,null);
        builder_add.setView(dialog_view);
        builder_add.setCancelable(false);

        sharedPref = new SharedPref(activity);

        final LottieAnimationView lottieAnimationView = (LottieAnimationView)dialog_view.findViewById(R.id.lottie_add_to_cart_dialog);
        if(sharedPref.loadNightModeState()){
            lottieAnimationView.setAnimation(R.raw.addtocartdark);

        }
        else{
           lottieAnimationView.setAnimation(R.raw.addtocartlightx);

        }

        dialog_add_to_cart = builder_add.create();
        if(dialog_add_to_cart.getWindow() != null){
            dialog_add_to_cart.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_add_to_cart.show();
    }

    void dismissDialog(){
        dialog_add_to_cart.dismiss();
    }
}
