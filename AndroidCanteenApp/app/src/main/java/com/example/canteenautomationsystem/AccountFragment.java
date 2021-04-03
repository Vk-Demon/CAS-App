package com.example.canteenautomationsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.readystatesoftware.viewbadger.BadgeView;
import com.valdesekamdem.library.mdtoast.MDToast;

import dmax.dialog.SpotsDialog;

public class AccountFragment extends Fragment {

    Toolbar account_fragment_toolbar;
    FrameLayout account_fragment_my_prof,account_fragment_change_password,account_fragment_close_account;

    // Declaring Firebase Authentication:

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View af_root_view =  inflater.inflate(R.layout.nav_drawer_account_fragment,container,false);

        // Establishing Connection to Firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        account_fragment_my_prof = (FrameLayout)af_root_view.findViewById(R.id.account_fragment_my_prof);
        account_fragment_change_password = (FrameLayout)af_root_view.findViewById(R.id.account_fragment_change_password);
        account_fragment_close_account = (FrameLayout)af_root_view.findViewById(R.id.account_fragment_close_account);
        account_fragment_toolbar = (Toolbar)af_root_view.findViewById(R.id.account_fragment_toolbar);

      /*  // Badge View for Account Fragment (Notifications purpose):

        final BadgeView af_my_prof_badge_view = new BadgeView(getActivity(),account_fragment_my_prof);
        af_my_prof_badge_view.setText("1");
        af_my_prof_badge_view.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);*/


        account_fragment_my_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyProfileFragment()).addToBackStack(null).commit();
            }
        });

        account_fragment_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialogProgress progressDialog_change_pass = new CustomDialogProgress(getActivity());

                final AlertDialog.Builder builder_alert = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alert_dialog,null);
                builder_alert.setView(dialog_view);
                builder_alert.setCancelable(false);

                final RelativeLayout alert_yes_rl = (RelativeLayout)dialog_view.findViewById(R.id.alert_yes_rl);
                final RelativeLayout alert_no_rl = (RelativeLayout)dialog_view.findViewById(R.id.alert_no_rl);
                final CardView alert_message_card = (CardView)dialog_view.findViewById(R.id.alert_message_card);

                final TextView alert_yes_text = (TextView)dialog_view.findViewById(R.id.alert_yes_text);
                final TextView alert_no_text = (TextView)dialog_view.findViewById(R.id.alert_no_text);
                final TextView alert_title_text = (TextView)dialog_view.findViewById(R.id.alert_title_text);
                final TextView alert_message_text = (TextView)dialog_view.findViewById(R.id.alert_message_text);
                final ImageView alert_message_icon = (ImageView)dialog_view.findViewById(R.id.alert_message_icon);

                alert_title_text.setText("Change Password");
                alert_message_text.setText("This will sign you out and redirect you to reset password page!");
                alert_message_icon.setImageResource(R.drawable.ic_vpn_key_alert_dialog_50dp);
                alert_no_text.setText("Don't change");
                alert_yes_text.setText("Change password");

                final AlertDialog dialog_alert = builder_alert.create();
                if(dialog_alert.getWindow() != null){
                    dialog_alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog_alert.show();

                alert_yes_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_alert.dismiss();
                        progressDialog_change_pass.startCustomDialogProgress("Signing out and redirecting...!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                                Intent prof_intent = new Intent(getActivity(),ForgotPasswordActivity.class);
                                prof_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                prof_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(prof_intent);
                                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                progressDialog_change_pass.dismissDialog();
                            }
                        },3000);
                    }
                });

                alert_no_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_alert.dismiss();
                    }
                });

                alert_message_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To Block Dialog dismiss
                    }
                });

            }
        });

        account_fragment_close_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             MDToast.makeText(getActivity(), "Unable to close your account due to limited users", MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
            }
        });

        return af_root_view;
    }
}
