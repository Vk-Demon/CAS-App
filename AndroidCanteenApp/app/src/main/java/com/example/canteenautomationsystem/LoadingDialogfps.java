package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class LoadingDialogfps {

    Activity activity;
    AlertDialog dialogfps;

    LoadingDialogfps(Activity activityfps){
        activity = activityfps;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builderfps = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflaterfps = activity.getLayoutInflater();
        builderfps.setView(inflaterfps.inflate(R.layout.customdialogfps,null));
        builderfps.setCancelable(false);

        dialogfps = builderfps.create();
        if(dialogfps .getWindow() != null){
            dialogfps .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogfps.show();
    }

    void dismissDialog(){
        dialogfps.dismiss();
    }
}
