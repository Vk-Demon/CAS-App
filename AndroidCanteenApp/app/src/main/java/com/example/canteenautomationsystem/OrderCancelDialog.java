package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.canteenautomationsystem.Common.Common;

public class OrderCancelDialog {

    Activity activity;
    AlertDialog dialog_cancelled;

    OrderCancelDialog(Activity activity_cancelled){
        activity = activity_cancelled;
    }

    void startOrderCancelDialog(String oid){
        AlertDialog.Builder builder_cancel = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_cancelled = activity.getLayoutInflater();
        View dialog_view = inflater_cancelled.inflate(R.layout.order_cancel_dialog,null);
        builder_cancel.setView(dialog_view);
        builder_cancel.setCancelable(false);

        final TextView cancel_order_dialog_text = (TextView)dialog_view.findViewById(R.id.cancel_order_dialog_text);
        cancel_order_dialog_text.setText(String.format("Order %s has been cancelled", oid));

        dialog_cancelled = builder_cancel.create();
        if(dialog_cancelled.getWindow() != null){
            dialog_cancelled.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_cancelled.show();
    }

    void dismissDialog(){
        dialog_cancelled.dismiss();
    }

}
