package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class LoadingDialogssup {

    Activity activity;
    AlertDialog dialogssup;

    LoadingDialogssup(Activity activityssup){
        activity = activityssup;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builderssup = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflaterssup = activity.getLayoutInflater();
        builderssup.setView(inflaterssup.inflate(R.layout.customdialogssup,null));
        builderssup.setCancelable(false);

        dialogssup = builderssup.create();
        if(dialogssup .getWindow() != null){
            dialogssup .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogssup.show();
    }

    void dismissDialog(){
        dialogssup.dismiss();
    }
}
