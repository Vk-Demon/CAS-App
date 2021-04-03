package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class LoadingDialogsup {

    Activity activity;
    AlertDialog dialogsup;

    LoadingDialogsup(Activity activitysup){
        activity = activitysup;
    }

    void startLoadingDialog(){
        AlertDialog.Builder buildersup = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflatersup = activity.getLayoutInflater();
        buildersup.setView(inflatersup.inflate(R.layout.customdialogloadsup,null));
        buildersup.setCancelable(false);

        dialogsup = buildersup.create();
        if(dialogsup .getWindow() != null){
            dialogsup .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogsup.show();
    }

    void dismissDialog(){
        dialogsup.dismiss();
    }
}
