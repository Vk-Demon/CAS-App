package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class LoadingDialogfp {

    Activity activity;
    AlertDialog dialogfp;

    LoadingDialogfp(Activity activityfp){
        activity = activityfp;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.customdialogloadfp,null));
        builder.setCancelable(false);

        dialogfp = builder.create();
        if(dialogfp .getWindow() != null){
            dialogfp .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogfp.show();
    }

    void dismissDialog(){
        dialogfp.dismiss();
    }
}
