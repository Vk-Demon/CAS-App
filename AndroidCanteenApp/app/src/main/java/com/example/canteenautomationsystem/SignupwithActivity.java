package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class SignupwithActivity extends AppCompatActivity {

    // Declaring variable(s):

    MagicButton fb_btn_sup, g_btn_sup, email_btn_sup;
    TextView back_log_suwp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupwith);

        // Initializing variable(s):

        g_btn_sup = findViewById(R.id.google_button_sup);
        fb_btn_sup = findViewById(R.id.facebook_button_sup);
        email_btn_sup = findViewById(R.id.email_button_sup);
        back_log_suwp = findViewById(R.id.back_log_supw);

        // Have an account? Login Click Event(s):

        SpannableString sups = new SpannableString(back_log_suwp.getText().toString().trim());
        ClickableSpan gsup = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                back_log_suwp.startAnimation(AnimationUtils.loadAnimation(SignupwithActivity.this,R.anim.blink));
                Intent stp = new Intent(SignupwithActivity.this, LoginActivity.class);
                startActivity(stp);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#39FF14"));
                ds.setUnderlineText(false);
            }
        };
        sups.setSpan(gsup,17,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        back_log_suwp.setText(sups);
        back_log_suwp.setMovementMethod(LinkMovementMethod.getInstance());
        back_log_suwp.setHighlightColor(Color.TRANSPARENT);

        // Magic Buttons:

        fb_btn_sup.setAutoCloseDuration(2);
        fb_btn_sup.setAnimateIcon(true);

        fb_btn_sup.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupwithActivity.this, "Facebook Sign in is currently unavailable.", Toast.LENGTH_SHORT).show();
            }
        });

        g_btn_sup.setAutoCloseDuration(2);
        g_btn_sup.setAnimateIcon(true);

        g_btn_sup.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupwithActivity.this, "Google Sign in is currently unavailable.", Toast.LENGTH_SHORT).show();
            }
        });

        email_btn_sup.setAutoCloseDuration(2);
        email_btn_sup.setAnimateIcon(true);

        email_btn_sup.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gtsup = new Intent(SignupwithActivity.this,SignupActivity.class);
                startActivity(gtsup);
                overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_top);
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignupwithActivity.this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
