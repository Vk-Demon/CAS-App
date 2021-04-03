package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenautomationsystem.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import www.sanju.motiontoast.MotionToast;

public class ForgotPasswordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    // Declaring variable(s):

    private ActivityForgotPasswordBinding fBinding;
    TextView fbacklogin;
    TextInputLayout textInputLayoutfpass;
    TextInputEditText mailfpass;
    FrameLayout btnmfp;
    CheckBox fp_recaptcha;
    GoogleApiClient googleApiClient;

    // Declaring Firebase Authentication:

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        fBinding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_password);

        // Initializing variable(s):

        textInputLayoutfpass = findViewById(R.id.textinputlayoutmailfp);
        mailfpass = findViewById(R.id.tipetmfp);
        btnmfp = findViewById(R.id.btnmfp);
        fbacklogin = findViewById(R.id.fbacklogin);
        fp_recaptcha = findViewById(R.id.fp_recaptcha);

        // Put SiteKey as a String:

        final String SiteKey = "6LeF9ekUAAAAAH3YU2miAyYc9d9PyusN2c9VNCCQ";

        // Establishing Connection to Firebase:

        firebaseAuth = FirebaseAuth.getInstance();

        // Calling ForgetPassword TextWatcher:

        mailfpass.addTextChangedListener(loginfpTextWatcher);

        // Create Google Api Client:

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(ForgotPasswordActivity.this)
                .build();
        googleApiClient.connect();

        // Recaptcha checkbox Click Events:

        fp_recaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fp_recaptcha.isChecked()) {
                    fp_recaptcha.setChecked(false);
                    fadeoutcheckboxsetprogressdialog();
                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient, SiteKey)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {
                                    Status status = recaptchaTokenResult.getStatus();
                                    if ((status!=null) && status.isSuccess()) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                fadeOutProgressDialog();
                                                fp_recaptcha.setChecked(true);
                                                fp_recaptcha.setClickable(false);
                                            }
                                        },2000);
                                    }
                                    else {
                                        disableProgressDialog();
                                        fp_recaptcha.setChecked(false);
                                        fp_recaptcha.setClickable(true);
                                    }
                                }
                            });
                } else {
                    disableProgressDialog();
                    fp_recaptcha.setChecked(false);
                    fp_recaptcha.setClickable(true);
                }
            }
        });

        // Forgot Password button Click Event(s):

        btnmfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnmfp.startAnimation(AnimationUtils.loadAnimation(ForgotPasswordActivity.this,R.anim.button_scale));
                String mail_fp = mailfpass.getText().toString().trim();
                Boolean checkfpr = fp_recaptcha.isChecked();

                if (mail_fp.isEmpty() || !isValidEmailfp(mail_fp)) {
                    textInputLayoutfpass.setErrorEnabled(true);
                    textInputLayoutfpass.setError("Enter a valid Email ID");
                    textInputLayoutfpass.setErrorIconDrawable(null);
                    textInputLayoutfpass.setCounterEnabled(true);
                    textInputLayoutfpass.setCounterMaxLength(320);
                    requestFocusfp(mailfpass);
                    taptoBounce(v);
                } else {
                    if (checkfpr == false) {
                        MotionToast.Companion.darkColorToast(ForgotPasswordActivity.this, "Check reCAPTCHA",
                                MotionToast.TOAST_INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(ForgotPasswordActivity.this, R.font.helvetica_regular));
                    } else
                        resetpass();
                }

            }
        });


        // << Back to Login Click Event(s):

        SpannableString fblpass = new SpannableString(fbacklogin.getText().toString().trim());
        final ClickableSpan fpblogin = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                fbacklogin.startAnimation(AnimationUtils.loadAnimation(ForgotPasswordActivity.this,R.anim.blink));
                Intent fbl = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(fbl);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#39FF14"));
                ds.setUnderlineText(false);
            }
        };
        fblpass.setSpan(fpblogin, 8, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fbacklogin.setText(fblpass);
        fbacklogin.setMovementMethod(LinkMovementMethod.getInstance());
        fbacklogin.setHighlightColor(Color.TRANSPARENT);
    }

    // Defining TextWatcher for Email field in Forgot Password:

    private TextWatcher loginfpTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            textInputLayoutfpass.setErrorEnabled(true);
            textInputLayoutfpass.setError("Invalid email format");
            textInputLayoutfpass.setErrorIconDrawable(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String e_logfp = mailfpass.getText().toString().trim();
            if (!e_logfp.isEmpty() && isValidEmailfp(e_logfp)) {
                textInputLayoutfpass.setErrorEnabled(false);
                textInputLayoutfpass.setCounterEnabled(false);
            } else {
                textInputLayoutfpass.setErrorEnabled(true);
                textInputLayoutfpass.setErrorIconDrawable(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Email Validation:

    private static boolean isValidEmailfp(String e) {
        return !TextUtils.isEmpty(e) && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    // Focus on TextInputEditText:

    private void requestFocusfp(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Processing Firebase Authentication:

    void resetpass() {
        final LoadingDialogfp loadingDialogfp = new LoadingDialogfp(ForgotPasswordActivity.this);
        final LoadingDialogfps loadingDialogfps = new LoadingDialogfps(ForgotPasswordActivity.this);
        loadingDialogfp.startLoadingDialog();
        firebaseAuth.sendPasswordResetEmail(mailfpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingDialogfp.dismissDialog();
                    loadingDialogfps.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialogfps.dismissDialog();
                            MotionToast.Companion.darkColorToast(ForgotPasswordActivity.this, "Requested link send to your email",
                                    MotionToast.TOAST_SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(ForgotPasswordActivity.this, R.font.helvetica_regular));
                            mailfpass.setText("");
                            textInputLayoutfpass.setErrorEnabled(false);
                            textInputLayoutfpass.setCounterEnabled(false);
                            mailfpass.clearFocus();
                            fp_recaptcha.setChecked(false);
                            fp_recaptcha.setClickable(true);
                            startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                        }
                    },2000);

                } else {
                    loadingDialogfp.dismissDialog();
                    MotionToast.Companion.darkColorToast(ForgotPasswordActivity.this, task.getException().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(ForgotPasswordActivity.this, R.font.helvetica_regular));
                    mailfpass.setText("");
                    textInputLayoutfpass.setErrorEnabled(false);
                    textInputLayoutfpass.setCounterEnabled(false);
                    mailfpass.clearFocus();
                    fp_recaptcha.setChecked(false);
                    fp_recaptcha.setClickable(true);
                    startActivity(new Intent(ForgotPasswordActivity.this,ForgotPasswordActivity.class));
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            }
        });
    }

    // Google Api Client Methods:

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void fadeoutcheckboxsetprogressdialog(){
        fBinding.fpRecaptcha.setVisibility(View.GONE);
        showProgressDialog();
    }

    private void showProgressDialog(){
        fBinding.progressBar2.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1e90ff"), PorterDuff.Mode.SRC_IN);
        fBinding.progressBar2.setVisibility(View.VISIBLE);
    }

    private void fadeOutProgressDialog(){
        fBinding.progressBar2.animate().alpha(0f).setDuration(200).start();
        fBinding.fpRecaptcha.setVisibility(View.VISIBLE);
    }

    private void disableProgressDialog(){
        fBinding.progressBar2.setVisibility(View.GONE);
        fBinding.fpRecaptcha.setVisibility(View.VISIBLE);
    }

    // Clear Focus when clicked outside inputField:

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof TextInputEditText || v instanceof CheckBox) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Empty Field Animation:

    public void taptoBounce(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        textInputLayoutfpass.startAnimation(animbtn);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}