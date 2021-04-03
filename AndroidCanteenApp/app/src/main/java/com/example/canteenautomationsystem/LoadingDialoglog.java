package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

public class LoadingDialoglog {

    Activity activity;
    AlertDialog dialoglog;

    LoadingDialoglog(Activity activitylog){
        activity = activitylog;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builderlog = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflaterlog = activity.getLayoutInflater();
        builderlog.setView(inflaterlog.inflate(R.layout.customdialoglog,null));
        builderlog.setCancelable(false);

        dialoglog = builderlog.create();
        if(dialoglog .getWindow() != null){
            dialoglog .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialoglog.show();
    }

    void dismissDialog(){
        dialoglog.dismiss();
    }
}
