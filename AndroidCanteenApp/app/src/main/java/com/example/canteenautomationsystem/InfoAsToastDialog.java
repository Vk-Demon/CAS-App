package com.example.canteenautomationsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

public class InfoAsToastDialog {

    Activity activity;
    AlertDialog dialog_info_toast;
    SharedPref sharedPref;

    InfoAsToastDialog(Activity activity_info_toast){
        activity = activity_info_toast;
    }

    void startInfoAsToastDialog(String title, String message){
        AlertDialog.Builder builder_info_toast = new AlertDialog.Builder(activity,R.style.AlertDialogTheme);
        LayoutInflater inflater_info_toast = activity.getLayoutInflater();
        View dialog_view = inflater_info_toast.inflate(R.layout.info_toast_as_dialog,null);
        builder_info_toast.setView(dialog_view);
        builder_info_toast.setCancelable(false);

        sharedPref = new SharedPref(activity);

        final LottieAnimationView lottieAnimationView = (LottieAnimationView)dialog_view.findViewById(R.id.lottie_info_toast_dialog);
        if(sharedPref.loadNightModeState()){
            lottieAnimationView.setAnimation(R.raw.infodarkx);

        }
        else{
            lottieAnimationView.setAnimation(R.raw.infolightz);

        }

        final CardView info_toast_message_card = (CardView)dialog_view.findViewById(R.id.info_toast_message_card);
        info_toast_message_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To block click event(s)
            }
        });

        final TextView info_toast_dialog_title = (TextView)dialog_view.findViewById(R.id.info_toast_dialog_title);
        info_toast_dialog_title.setText(String.format("%s", title));

        final TextView info_toast_dialog_message = (TextView)dialog_view.findViewById(R.id.info_toast_dialog_message);
        info_toast_dialog_message.setText(String.format("%s", message));

        dialog_info_toast = builder_info_toast.create();
        if(dialog_info_toast .getWindow() != null){
            dialog_info_toast .getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog_info_toast.show();
    }

    void dismissDialog(){
        dialog_info_toast.dismiss();
    }

}
