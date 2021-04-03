package com.example.canteenautomationsystem;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.valdesekamdem.library.mdtoast.MDToast;

public class SupportFragment extends Fragment {

    Toolbar support_fragment_toolbar;
    FrameLayout support_fragment_about_cas,support_fragment_grievances,support_fragment_share_CAS;
    SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View sf_root_view =  inflater.inflate(R.layout.nav_drawer_support_fragment,container,false);

        support_fragment_about_cas = (FrameLayout)sf_root_view.findViewById(R.id.support_fragment_about_us);
        support_fragment_grievances = (FrameLayout)sf_root_view.findViewById(R.id.support_fragment_grievances);
        support_fragment_share_CAS = (FrameLayout)sf_root_view.findViewById(R.id.support_fragment_share_CAS);
        support_fragment_toolbar = (Toolbar) sf_root_view.findViewById(R.id.support_fragment_toolbar);

        sharedPref = new SharedPref(getActivity());

        support_fragment_about_cas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder_info = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_info_dialog,null);
                builder_info.setView(dialog_view);
                builder_info.setCancelable(false);

                final CardView info_message_card = (CardView)dialog_view.findViewById(R.id.info_message_card);
                final RelativeLayout info_close_rl = (RelativeLayout)dialog_view.findViewById(R.id.info_close_rl);

                final TextView info_close_text = (TextView)dialog_view.findViewById(R.id.info_close_text);
                final TextView info_title_text = (TextView)dialog_view.findViewById(R.id.info_title_text);
                final TextView info_message_text = (TextView)dialog_view.findViewById(R.id.info_message_text);
                final ImageView info_message_icon = (ImageView)dialog_view.findViewById(R.id.info_message_icon);

                info_title_text.setText("About CAS");
                info_message_text.setGravity(Gravity.CENTER);
                info_message_text.setText("\nCAS is an android application that it greatly simplifies the ordering process for both the customer and the canteen");
                info_close_text.setText("Close");

                if(sharedPref.loadNightModeState()){
                    info_message_icon.setImageResource(R.drawable.lcdarklg);
                }
                else{
                    info_message_icon.setImageResource(R.drawable.lclightlb);
                }

                final AlertDialog dialog_info = builder_info.create();
                if(dialog_info.getWindow() != null){
                    dialog_info.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog_info.show();

                info_close_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_info.dismiss();
                    }
                });

                info_message_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To Block Dialog dismiss
                    }
                });

            }
        });

        support_fragment_grievances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder_info = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_info_dialog,null);
                builder_info.setView(dialog_view);
                builder_info.setCancelable(false);

                final CardView info_message_card = (CardView)dialog_view.findViewById(R.id.info_message_card);
                final RelativeLayout info_close_rl = (RelativeLayout)dialog_view.findViewById(R.id.info_close_rl);

                final TextView info_close_text = (TextView)dialog_view.findViewById(R.id.info_close_text);
                final TextView info_title_text = (TextView)dialog_view.findViewById(R.id.info_title_text);
                final TextView info_message_text = (TextView)dialog_view.findViewById(R.id.info_message_text);
                final ImageView info_message_icon = (ImageView)dialog_view.findViewById(R.id.info_message_icon);

                info_title_text.setText("Grievances");
                info_message_text.setText("Contact us for any issues about our app\n\nContact:\n\n\t\t\t\t\t\t\t  vk2049627@gmail.com\n\t\t\t\t\t\t   dineshrdk07@gmail.com");
                info_message_icon.setImageResource(R.drawable.ic_speaker_notes_info_50dp);
                info_close_text.setText("Close");

                final AlertDialog dialog_info = builder_info.create();
                if(dialog_info.getWindow() != null){
                    dialog_info.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog_info.show();

                info_close_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_info.dismiss();
                    }
                });

                info_message_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To Block Dialog dismiss
                    }
                });

            }
        });

        support_fragment_share_CAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDToast.makeText(getActivity(), "Unable to share our app because it is yet to be deployed in play store", MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
            }
        });


        return sf_root_view;
    }
}
