package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class AddToFavDialog {

    Activity activity;
    AlertDialog dialog_add_to_fav;

    AddToFavDialog(Activity activity_add_to_fav){
        activity = activity_add_to_fav;
    }

    void startAddToFavDialog(){
        AlertDialog.Builder builder_add_to_fav = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_add_to_fav = activity.getLayoutInflater();
        View dialog_view = inflater_add_to_fav.inflate(R.layout.add_to_fav_dialog,null);
        builder_add_to_fav.setView(dialog_view);
        builder_add_to_fav.setCancelable(false);

        dialog_add_to_fav = builder_add_to_fav.create();
        if(dialog_add_to_fav .getWindow() != null){
            dialog_add_to_fav .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_add_to_fav.show();
    }

    void dismissDialog(){
        dialog_add_to_fav.dismiss();
    }

}
