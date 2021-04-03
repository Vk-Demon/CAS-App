package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class OrderPlacedDialog {
    Activity activity;
    AlertDialog dialog_placed;
    SharedPref sharedPref;

    OrderPlacedDialog(Activity activity_placed){
        activity = activity_placed;
    }

    void startOrderPlacedDialog(){
        AlertDialog.Builder builder_placed = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_placed = activity.getLayoutInflater();
        View dialog_view = inflater_placed.inflate(R.layout.order_placed_dialog,null);
        builder_placed.setView(dialog_view);
        builder_placed.setCancelable(false);

        sharedPref = new SharedPref(activity);

        final LottieAnimationView lottieAnimationView = (LottieAnimationView)dialog_view.findViewById(R.id.lottie_order_placed_dialog);
        if(sharedPref.loadNightModeState()){
            lottieAnimationView.setAnimation(R.raw.orderplaceddark);

        }
        else{
            lottieAnimationView.setAnimation(R.raw.orderplacedlightx);

        }

        dialog_placed = builder_placed.create();
        if(dialog_placed .getWindow() != null){
            dialog_placed .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_placed.show();
    }

    void dismissDialog(){
        dialog_placed.dismiss();
    }

}
