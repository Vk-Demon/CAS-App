package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class CustomDialogProgress {

    Activity activity;
    AlertDialog dialog_progress;
    SharedPref sharedPref;

    CustomDialogProgress(Activity activity_progress){
        activity = activity_progress;
    }

    void startCustomDialogProgress(String message){
        AlertDialog.Builder builder_progress = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_add = activity.getLayoutInflater();
        View dialog_view = inflater_add.inflate(R.layout.custom_progress_dialog,null);
        builder_progress.setView(dialog_view);
        builder_progress.setCancelable(false);

        sharedPref = new SharedPref(activity);

        final LottieAnimationView lottieAnimationView = (LottieAnimationView)dialog_view.findViewById(R.id.lottie_progress_dialog);
        if(sharedPref.loadNightModeState()){
            lottieAnimationView.setAnimation(R.raw.loadingdark);

        }
        else{
            lottieAnimationView.setAnimation(R.raw.loadinglight);

        }

        final TextView textView = (TextView)dialog_view.findViewById(R.id.text_progress_dialog);
        textView.setText(message);

        dialog_progress = builder_progress.create();
        if(dialog_progress.getWindow() != null){
            dialog_progress.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_progress.show();
    }

    void dismissDialog(){
        dialog_progress.dismiss();
    }

}
