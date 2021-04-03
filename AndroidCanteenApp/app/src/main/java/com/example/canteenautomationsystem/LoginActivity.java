package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenautomationsystem.Common.Common;
import com.example.canteenautomationsystem.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import www.sanju.motiontoast.MotionToast;

public class LoginActivity extends AppCompatActivity {

    // Declaring variable(s):

    private ActivityLoginBinding mBinding;
    ConstraintLayout layout_login;
    TextView forgot_pass,usersignup;
    TextInputLayout mail_login_layout,pass_login_layout;
    TextInputEditText login_mail,login_pass;
    FrameLayout userlogin;
    MagicButton fb_btn, g_btn;

    // Declaring Firebase Authentication:

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);

        // Initializing variable(s):

        layout_login = findViewById(R.id.View);
        mail_login_layout = findViewById(R.id.textInputLayoutLogin1);
        pass_login_layout = findViewById(R.id.textInputLayoutLogin2);
        login_mail = findViewById(R.id.tipetlogin1);
        login_pass = findViewById(R.id.tipetlogin2);
        userlogin = findViewById(R.id.btnlogin);
        usersignup = findViewById(R.id.btnsignup);
        forgot_pass = findViewById(R.id.forgot_pass);
        fb_btn = findViewById(R.id.facebook_button);
        g_btn = findViewById(R.id.google_button);

        // Establishing Connection to Firebase:

        firebaseAuth = FirebaseAuth.getInstance();

        // Calling Login TextWatcher(s):

        login_mail.addTextChangedListener(loginTextWatcher);
        login_pass.addTextChangedListener(loginpassTextWatcher);

        // Login button Click Event(s):

        userlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userlogin.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.button_scale));
                String email_login = login_mail.getText().toString().trim();
                String password_login = login_pass.getText().toString().trim();

                if(email_login.equals("") || password_login.equals("")){

                    if(email_login.isEmpty() || !isValidEmailLogin(email_login)){
                        mail_login_layout.setErrorEnabled(true);
                        mail_login_layout.setErrorIconDrawable(null);
                        mail_login_layout.setError("Enter a valid Email ID");
                        mail_login_layout.setCounterEnabled(true);
                        mail_login_layout.setCounterMaxLength(320);
                        //requestFocusLogin(login_mail);
                        taptoBouncemll(v);
                    }
                    if(password_login.isEmpty()){
                        pass_login_layout.setErrorEnabled(true);
                        pass_login_layout.setError("Enter a valid password");
                        pass_login_layout.setErrorIconDrawable(null);
                        pass_login_layout.setCounterEnabled(true);
                        pass_login_layout.setCounterMaxLength(128);
                        //requestFocusLogin(login_pass);
                        taptoBouncepll(v);
                    }

                    if(!(email_login.isEmpty()) && isValidEmailLogin(email_login)){
                        mail_login_layout.setErrorEnabled(false);
                        mail_login_layout.setCounterEnabled(false);
                    }
                    if(!(password_login.isEmpty())){
                        pass_login_layout.setErrorEnabled(false);
                    }
                }
                else{
                    animateButtonWidth();
                    fadeOutTextAndSetProgressDialog();
                    getlogged();
                }

            }
        });

        // New User? Sign up Click Event(s):

        final SpannableString supass = new SpannableString(usersignup.getText().toString().trim());
        final ClickableSpan gosup = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                usersignup.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.blink));
                Intent s = new Intent(LoginActivity.this, SignupwithActivity.class);
                startActivity(s);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#FAED27"));
                ds.setUnderlineText(false);
            }
        };
        supass.setSpan(gosup,10,17,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        usersignup.setText(supass);
        usersignup.setMovementMethod(LinkMovementMethod.getInstance());
        usersignup.setHighlightColor(Color.TRANSPARENT);

        // Forgot password? Hyperlink:

        SpannableString fpass = new SpannableString(forgot_pass.getText().toString().trim());
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.parseColor("#CC99FF"));
        fpass.setSpan(fcsGreen,0,19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgot_pass.setText(fpass);

        // Forgot password? Click Event(s):

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot_pass.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.blink));
                Intent f = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(f);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        // Magic Buttons:

        fb_btn.setAutoCloseDuration(2);
        fb_btn.setAnimateIcon(true);

        fb_btn.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Facebook Sign in is currently unavailable.", Toast.LENGTH_SHORT).show();
            }
        });

        g_btn.setAutoCloseDuration(2);
        g_btn.setAnimateIcon(true);

        g_btn.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Google Sign in is currently unavailable.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Defining TextWatcher for Email ID field in Login:

    private TextWatcher loginTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mail_login_layout.setErrorEnabled(true);
            mail_login_layout.setError("Invalid email format");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String e_log = login_mail.getText().toString().trim();
            if(!e_log.isEmpty() && isValidEmailLogin(e_log)){
                mail_login_layout.setErrorEnabled(false);
                mail_login_layout.setCounterEnabled(false);
            }
            else{
                mail_login_layout.setErrorEnabled(true);
                mail_login_layout.setErrorIconDrawable(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Defining TextWatcher for Password field in Login:

    private TextWatcher loginpassTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String p_log = login_pass.getText().toString().trim();
            if(!p_log.isEmpty()){
                pass_login_layout.setErrorEnabled(false);
                pass_login_layout.setErrorIconDrawable(null);
                pass_login_layout.setCounterEnabled(false);
            }
            else{
                pass_login_layout.setErrorIconDrawable(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Email Validation:

    private static boolean isValidEmailLogin(String e){
        return !TextUtils.isEmpty(e) && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

  /*  // Focus on TextInputEditText:

    private void requestFocusLogin(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }  */

    // Processing Firebase Authentication:

    void getlogged(){
        final LoadingDialoglog loadingDialoglog = new LoadingDialoglog(LoginActivity.this);
        firebaseAuth.signInWithEmailAndPassword(login_mail.getText().toString(),login_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                final String admin_mail_check_on_user_login = login_mail.getText().toString().trim();

                if(admin_mail_check_on_user_login.equals(Common.ADMIN_EMAIL)){
                    if(task.isSuccessful()){
                        loadingDialoglog.startLoadingDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialoglog.dismissDialog();
                                admin_login_success_user_panel();
                            }
                        },1800);
                    }
                    else{
                        MotionToast.Companion.darkColorToast(LoginActivity.this, task.getException().getMessage(),
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(LoginActivity.this, R.font.helvetica_regular));
                        failedAction();
                    }
                }
                else {
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                            // Get user email and uid from auth:
                            String email = user.getEmail();
                            String uid = user.getUid();

                            // When user is registered store user info in firebase realtime database too using HashMap:
                            HashMap<Object, String> hashMap = new HashMap<>();

                            // Put info in HashMap:
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);

                            // Update profile values:
                            hashMap.put("name", "");
                            hashMap.put("phone", "");
                            hashMap.put("image", "");
                            hashMap.put("due","0.0");
                            hashMap.put("wallet","0.0");

                            // Firebase Database instance:
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                            // Path to store user data named "Users":
                            DatabaseReference reference = firebaseDatabase.getReference("Users");

                            // Path data within HashMap in database:
                            reference.child(uid).setValue(hashMap);
                        }

                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            loadingDialoglog.startLoadingDialog();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialoglog.dismissDialog();
                                    successAction();
                                }
                            }, 1800);

                        } else {
                            MotionToast.Companion.darkColorToast(LoginActivity.this, "Please verify your email address",
                                    MotionToast.TOAST_WARNING,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginActivity.this, R.font.helvetica_regular));
                            login_mail.clearFocus();
                            login_pass.clearFocus();
                            failedAction();
                        }
                    } else {
                        //loadingDialoglog.dismissDialog();
                        MotionToast.Companion.darkColorToast(LoginActivity.this, task.getException().getMessage(),
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(LoginActivity.this, R.font.helvetica_regular));
                        failedAction();
                    }
                }
            }
        });
    }

    private void animateButtonWidth(){
        ValueAnimator anim = ValueAnimator.ofInt(mBinding.btnlogin.getMeasuredWidth(),getFinalWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.btnlogin.getLayoutParams();
                layoutParams.width = value;
                mBinding.btnlogin.requestLayout();
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    private void fadeOutTextAndSetProgressDialog(){
        mBinding.logintext.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog();
            }
        }).start();
    }

    private void showProgressDialog(){
        mBinding.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#191A1E"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private void failedAction(){
        revealButton();
        fadeOutProgressDialog();
        delayedRefreshActivity();
    }

    private void successAction() {
        revealButton();
        fadeOutProgressDialog();
        delayedStartNextActivity();
    }

    private void admin_login_success_user_panel(){
        revealButton();
        fadeOutProgressDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,AdminProfileActivity.class));
            }
        },100);
    }

    private void revealButton(){
        mBinding.btnlogin.setElevation(0f);
        mBinding.revealView.setVisibility(View.VISIBLE);
        int x = mBinding.revealView.getWidth();
        int y = mBinding.revealView.getHeight();

        int startX = (int) (getFinalWidth()/2 + mBinding.btnlogin.getX());
        int startY = (int) (getFinalWidth()/2 + mBinding.btnlogin.getY());

        float radius = Math.max(x,y) * 1.2f;
        final Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.revealView,startX,startY,getFinalWidth(),radius);
        reveal.setDuration(400);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
        reveal.start();
    }

    private void fadeOutProgressDialog(){
        mBinding.progressBar.animate().alpha(0f).setDuration(200).start();
    }

    private void delayedRefreshActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,LoginActivity.class));
            }
        },100);
    }

    private void delayedStartNextActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
            }
        },100);
        }

    private int getFinalWidth(){
        return (int) getResources().getDimension(R.dimen.get_width);
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

    public void taptoBouncemll(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        mail_login_layout.startAnimation(animbtn);
    }

    public void taptoBouncepll(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        pass_login_layout.startAnimation(animbtn);
    }

    @Override
    public void onBackPressed() {
      /*  Intent back = new Intent(Intent.ACTION_MAIN);
        back.addCategory(Intent.CATEGORY_HOME);
        back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(back);*/
      finishAffinity();
      finish();
    }
}
