package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class RemoveFromFavDialog {

    Activity activity;
    AlertDialog dialog_remove_from_fav;

    RemoveFromFavDialog(Activity activity_remove_from_fav){
        activity = activity_remove_from_fav;
    }

    void startRemoveFromFavDialog(String message){
        AlertDialog.Builder builder_remove_from_fav = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_remove_from_fav = activity.getLayoutInflater();
        View dialog_view = inflater_remove_from_fav.inflate(R.layout.remove_from_fav_dialog,null);
        builder_remove_from_fav.setView(dialog_view);
        builder_remove_from_fav.setCancelable(false);

        final TextView remove_from_fav_dialog_message = (TextView)dialog_view.findViewById(R.id.remove_from_fav_dialog_message);
        remove_from_fav_dialog_message.setText(String.format("%s", message));

        dialog_remove_from_fav = builder_remove_from_fav.create();
        if(dialog_remove_from_fav .getWindow() != null){
            dialog_remove_from_fav .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_remove_from_fav.show();
    }

    void dismissDialog(){
        dialog_remove_from_fav.dismiss();
    }

}
