package com.example.canteenautomationsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
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
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenautomationsystem.databinding.ActivitySignupBinding;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.sanju.motiontoast.MotionToast;

public class SignupActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks {


    // Declaring variable(s):

    private ActivitySignupBinding sBinding;
    TextInputLayout mail_layout,pass_layout,cpass_layout;
    TextInputEditText mail,pass,cpass;
    TextView loginback;
    FrameLayout reg;
    CheckBox sup_recaptcha;
    GoogleApiClient googleApiClient;

    // Declaring Firebase Authentication:

    FirebaseAuth firebaseAuth;

    // Password Validation:

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
                    "^" +
                    "(?=.*[0-9])" +            // at least 1 digit
                    "(?=.*[a-z])" +            // at least 1 lower case letter
                    "(?=.*[A-Z])" +            // at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +       // any letter
                    "(?=.*[@#$%^&+=])" +       // at least 1 special character
                    "(?=\\S+$)" +              // no white spaces
                    ".{6,}" +                  // at least 6 characters
                    "$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sBinding = DataBindingUtil.setContentView(this,R.layout.activity_signup);

        // Initializing variable(s):

        mail_layout = findViewById(R.id.textInputLayoutSignup);
        pass_layout = findViewById(R.id.textInputLayoutSignup2);
        cpass_layout = findViewById(R.id.textInputLayoutSignup3);
        mail = findViewById(R.id.tipetsignup1);
        pass = findViewById(R.id.tipetsignup2);
        cpass = findViewById(R.id.tipetsignup3);
        reg = findViewById(R.id.bt3signup1);
        loginback = findViewById(R.id.loginback);
        sup_recaptcha = findViewById(R.id.sup_recaptcha);

        // Put SiteKey as a String:

        final String SiteKey = "6LeF9ekUAAAAAH3YU2miAyYc9d9PyusN2c9VNCCQ";

        // Establishing Connection to Firebase:

        firebaseAuth = firebaseAuth.getInstance();

        // Calling Signup TextWatcher(s):

        mail.addTextChangedListener(supmailTextWatcher);
        pass.addTextChangedListener(suppassTextWatcher);
        cpass.addTextChangedListener(supcpassTextWatcher);

        // Create Google Api Client:

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(SignupActivity.this)
                .build();
        googleApiClient.connect();

        // Recaptcha checkbox Click Events:

        sup_recaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sup_recaptcha.isChecked()){
                    sup_recaptcha.setChecked(false);
                    fadeoutcheckboxsetprogressdialog();
                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient,SiteKey)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {
                                    Status status = recaptchaTokenResult.getStatus();
                                    if((status!=null) && status.isSuccess()){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                fadeOutProgressDialog();
                                                sup_recaptcha.setChecked(true);
                                                sup_recaptcha.setClickable(false);
                                            }
                                        },2000);
                                    }
                                }
                            });
                }
                else{
                    fadeOutProgressDialog();
                    sup_recaptcha.setChecked(false);
                }
            }
        });

        // REGISTER button Click Event(s):

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reg.startAnimation(AnimationUtils.loadAnimation(SignupActivity.this,R.anim.button_scale));
                final String email = mail.getText().toString().trim();
                final String password = pass.getText().toString().trim();
                final String checkpassword = cpass.getText().toString().trim();
                Boolean checksupr = sup_recaptcha.isChecked();

                // Signup Validator:

                      if(PASSWORD_PATTERN.matcher(password).matches() && password.equals(checkpassword) && isValidEmail(email)) {
                          if(checksupr == false){
                              MotionToast.Companion.darkColorToast(SignupActivity.this, "Check reCAPTCHA",
                                      MotionToast.TOAST_INFO,
                                      MotionToast.GRAVITY_BOTTOM,
                                      MotionToast.LONG_DURATION,
                                      ResourcesCompat.getFont(SignupActivity.this,R.font.helvetica_regular));
                          }
                          else
                            getregistered();
                        }
                      else{
                          if (!PASSWORD_PATTERN.matcher(password).matches() && !password.equals(checkpassword) && !isValidEmail(email)) {
                              MotionToast.Companion.darkColorToast(SignupActivity.this, "Entered email or password is invalid",
                                      MotionToast.TOAST_ERROR,
                                      MotionToast.GRAVITY_BOTTOM,
                                      MotionToast.LONG_DURATION,
                                      ResourcesCompat.getFont(SignupActivity.this,R.font.helvetica_regular));
                          }
                          if (!PASSWORD_PATTERN.matcher(password).matches() || !password.equals(checkpassword) || !isValidEmail(email)) {
                              if (email.isEmpty() || !isValidEmail(email)) {
                                  mail_layout.setErrorEnabled(true);
                                  mail_layout.setErrorIconDrawable(null);
                                  mail_layout.setError("Enter a valid email address");
                                  mail_layout.setCounterEnabled(true);
                                  mail_layout.setCounterMaxLength(320);
                                  //requestFocus(mail);
                                  taptoBounceml(v);
                              }
                              if (password.isEmpty() || !PASSWORD_PATTERN.matcher(password).matches()) {
                                  pass_layout.setErrorEnabled(true);
                                  pass_layout.setError("Enter a valid Password");
                                  pass_layout.setErrorIconDrawable(null);
                                  pass_layout.setCounterEnabled(true);
                                  pass_layout.setCounterMaxLength(128);
                                  //requestFocus(pass);
                                  taptoBouncepl(v);
                              }
                              if (checkpassword.isEmpty() || !PASSWORD_PATTERN.matcher(checkpassword).matches()) {
                                  cpass_layout.setErrorEnabled(true);
                                  cpass_layout.setErrorIconDrawable(null);
                                  cpass_layout.setError("Enter a valid Confirm Password");
                                  cpass_layout.setCounterEnabled(true);
                                  cpass_layout.setCounterMaxLength(128);
                                  //requestFocus(cpass);
                                  taptoBouncecpl(v);
                              }
                              if (!(email.isEmpty()) && isValidEmail(email)) {
                                  mail_layout.setErrorEnabled(false);
                                  mail_layout.setCounterEnabled(false);
                              }
                              if (!(password.isEmpty()) && PASSWORD_PATTERN.matcher(password).matches()) {
                                  pass_layout.setErrorEnabled(false);
                                  pass_layout.setCounterEnabled(false);
                              }
                              if (!(checkpassword.isEmpty()) && PASSWORD_PATTERN.matcher(checkpassword).matches()) {
                                  cpass_layout.setErrorEnabled(false);
                                  cpass_layout.setCounterEnabled(false);
                              }
                              if (!(password.equals(checkpassword))) {
                                  MotionToast.Companion.darkColorToast(SignupActivity.this, "Password do not match",
                                          MotionToast.TOAST_ERROR,
                                          MotionToast.GRAVITY_BOTTOM,
                                          MotionToast.LONG_DURATION,
                                          ResourcesCompat.getFont(SignupActivity.this,R.font.helvetica_regular));
                              }

                          }
                      }
                }
        });

        // Back to Sign in Click Event(s):

        final SpannableString blpass = new SpannableString(loginback.getText().toString().trim());
        final ClickableSpan blogin = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                loginback.startAnimation(AnimationUtils.loadAnimation(SignupActivity.this,R.anim.blink));
                Intent bl = new Intent(SignupActivity.this,SignupwithActivity.class);
                startActivity(bl);
                overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_bottom);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#39FF14"));
                ds.setUnderlineText(false);
            }
        };
        blpass.setSpan(blogin,8,15,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginback.setText(blpass);
        loginback.setMovementMethod(LinkMovementMethod.getInstance());
        loginback.setHighlightColor(Color.TRANSPARENT);
    }

    // Defining TextWatcher for Email field in Signup:

    private TextWatcher supmailTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String e_sup = mail.getText().toString().trim();
            Pattern p_sup = Pattern.compile("[a-zA-Z0-9]*");

            if(!e_sup.isEmpty() && isValidEmail(e_sup)){
                mail_layout.setErrorEnabled(false);
                mail_layout.setCounterEnabled(false);
                mail_layout.setErrorIconDrawable(null);
            }
            else{
                mail_layout.setErrorEnabled(true);
                mail_layout.setErrorIconDrawable(null);
                mail_layout.setCounterEnabled(true);
                mail_layout.setCounterMaxLength(320);
                Boolean at = e_sup.contains("@");
                Boolean dot = e_sup.contains(".");
                Boolean hn = e_sup.contains("@gmail.") || e_sup.contains("@outlook.") || e_sup.contains("@yahoo.") ? true : false;
                Boolean hnp = e_sup.contains(".") ? true : false;
                Boolean hgn = e_sup.contains("@gmail") || e_sup.contains("@outlook") || e_sup.contains("@yahoo")? true : false;
                Matcher m_sup = p_sup.matcher(e_sup);

                if(!m_sup.matches() && e_sup.length()==1){
                    mail_layout.setError("invalid format: invalid input");
                    mail_layout.setErrorIconDrawable(null);
                    mail.setText("");
                }
                else{
                if(at == false && hn == false){
                    if(Pattern.matches("[a-zA-Z0-9\\!\\#\\$\\&\\'\\+\\^\\?\\/\\{\\|\\=\\.\\_\\%\\-\\+]{1,256}",e_sup) == true) {
                        mail_layout.setError(e_sup + " should include '@'");
                        mail_layout.setErrorIconDrawable(null);
                    }
                    else {
                        mail_layout.setError("invalid format: invalid input");
                        mail_layout.setErrorIconDrawable(null);
                    }
                }}
                if(at == true && hn == false) {
                       if(Pattern.matches("[a-zA-Z0-9\\!\\#\\$\\&\\'\\+\\^\\?\\/\\{\\|\\=\\.\\_\\%\\-\\+]*[@]?[a-zA-Z0-9]*",e_sup) == true) {
                           mail_layout.setError(e_sup + " should include valid hostname ('Eg: gmail')");
                           mail_layout.setErrorIconDrawable(null);
                       }
                       else{
                           mail_layout.setError("invalid format: cannot include '@' ");
                           mail_layout.setErrorIconDrawable(null);
                           mail.setText("");
                    }}
                if(hgn == true) {
                    mail_layout.setError(e_sup + " should include '.'");
                    mail_layout.setErrorIconDrawable(null);
                }
                if(hn==true && hnp==true && !isValidEmail(e_sup)) {
                    mail_layout.setError(e_sup + " should include dns_label ('Eg: com')");
                    mail_layout.setErrorIconDrawable(null);
                    if(e_sup.charAt(e_sup.length()-2) == '.' && e_sup.charAt(e_sup.length()-1) != 'c')
                        mail.setText("");
                }
                if(hn==true && hnp==false)
                    mail.setText("");

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Defining TextWatcher for Password field in Signup:

    private TextWatcher suppassTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            String passp_sup = pass.getText().toString().trim();
            if(!passp_sup.isEmpty()){
                pass_layout.setErrorIconDrawable(null);
                pass_layout.setCounterEnabled(true);
                pass_layout.setCounterMaxLength(128);
            }
            else{
                pass_layout.setErrorEnabled(true);
                pass_layout.setCounterEnabled(true);
                pass_layout.setCounterMaxLength(128);
                pass_layout.setErrorIconDrawable(null);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        String pass_sup = pass.getText().toString().trim();
            Pattern od_sup = Pattern.compile("^(?=.*[0-9]).*$");
            Pattern ol_sup = Pattern.compile("^(?=.*[a-z]).*$");
            Pattern ou_sup = Pattern.compile("^(?=.*[A-Z]).*$");
            Pattern osp_sup = Pattern.compile("^(?=.*[@#$%^&+=]).*$");
            Pattern nw_sup = Pattern.compile("^(?=\\S+$).*$");
            Matcher mod_sup = od_sup.matcher(pass_sup);
            Matcher mol_sup = ol_sup.matcher(pass_sup);
            Matcher mou_sup = ou_sup.matcher(pass_sup);
            Matcher mosp_sup = osp_sup.matcher(pass_sup);
            Matcher mnw_sup = nw_sup.matcher(pass_sup);

            if(mnw_sup.matches()==false && pass_sup.isEmpty())
                pass_layout.setError("invalid: Your password is either empty or contains white spaces");
            else if(mnw_sup.matches()==false && !pass_sup.isEmpty())
                pass_layout.setError("invalid: Your password should not contain white spaces");
            else {
                if (pass_sup.length() >= 2 && pass_sup.length() < 4)
                    pass_layout.setError("Password too weak");
                if (pass_sup.length() <= 1)
                    pass_layout.setError("Your password should be 6-64 characters long");
                if (pass_sup.length() >= 4) {
                    if (mol_sup.matches() == false)
                        pass_layout.setError("Your password should include atleast one lowercase");
                    else if (mod_sup.matches() == false)
                        pass_layout.setError("Your password should include atleast one digit");
                    else if (mou_sup.matches() == false)
                        pass_layout.setError("Your password should include atleast one uppercase");
                    else if (mosp_sup.matches() == false)
                        pass_layout.setError("Your password should include atleast one special character");
                    else if(pass_sup.length()<6)
                        pass_layout.setError("Your password should be 6-64 characters long");
                    else {
                        pass_layout.setErrorEnabled(false);
                        pass_layout.setCounterEnabled(false);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Defining TextWatcher for Confirm Password field in Signup:

    private TextWatcher supcpassTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String pp_sup = pass.getText().toString().trim();
            String pc_sup = cpass.getText().toString().trim();
            if(pc_sup.equals(pp_sup)) {
                cpass_layout.setErrorEnabled(false);
                cpass_layout.setCounterEnabled(false);
                cpass_layout.setErrorIconDrawable(null);
            }
            else {
                cpass_layout.setError("Password do not match");
                cpass_layout.setErrorIconDrawable(null);
                cpass_layout.setCounterEnabled(true);
                cpass_layout.setCounterMaxLength(128);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Email validation:

    private static boolean isValidEmail(String e){
        return !TextUtils.isEmpty(e) && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    /*// Focus on TextInputEditText:

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }*/

    // Processing Firebase Authentication:

    public void getregistered(){
        final LoadingDialogsup loadingDialogsup = new LoadingDialogsup(SignupActivity.this);
        final LoadingDialogssup loadingDialogssup = new LoadingDialogssup(SignupActivity.this);
        loadingDialogsup.startLoadingDialog();
        // Creating Email and Password with a set of operations determining the status of Signup:

            firebaseAuth.createUserWithEmailAndPassword(mail.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        loadingDialogsup.dismissDialog();
                        loadingDialogssup.startLoadingDialog();

                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    // Get user email and uid from auth:
                                    String email = user.getEmail();
                                    String uid = user.getUid();

                                    // When user is registered store user info in firebase realtime database too using HashMap:
                                    HashMap<Object, String> hashMap = new HashMap<>();

                                    // Put info in HashMap:
                                    hashMap.put("email",email);
                                    hashMap.put("uid",uid);

                                    // Update profile values:
                                    hashMap.put("name","");
                                    hashMap.put("phone","");
                                    hashMap.put("image","");
                                    hashMap.put("due","0.0");
                                    hashMap.put("wallet","0.0");

                                    // Firebase Database instance:
                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                                    // Path to store user data named "Users":
                                    DatabaseReference reference = firebaseDatabase.getReference("Users");

                                    // Path data within HashMap in database:
                                    reference.child(uid).setValue(hashMap);


                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingDialogssup.dismissDialog();
                                            MotionToast.Companion.darkColorToast(SignupActivity.this, "Registered Successfully... Please check your email for verification",
                                                    MotionToast.TOAST_SUCCESS,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(SignupActivity.this,R.font.helvetica_regular));
                                            mail.setText("");
                                            pass.setText("");
                                            cpass.setText("");
                                            mail.clearFocus();
                                            pass.clearFocus();
                                            cpass.clearFocus();
                                            mail_layout.setErrorEnabled(false);
                                            mail_layout.setCounterEnabled(false);
                                            pass_layout.setErrorEnabled(false);
                                            pass_layout.setCounterEnabled(false);
                                            cpass_layout.setErrorEnabled(false);
                                            cpass_layout.setCounterEnabled(false);
                                            sup_recaptcha.setChecked(false);
                                            sup_recaptcha.setClickable(true);
                                            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                        }
                                    },2000);
                                }
                                else{
                                    MotionToast.Companion.darkColorToast(SignupActivity.this, task.getException().getMessage(),
                                            MotionToast.TOAST_ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(SignupActivity.this,R.font.helvetica_regular));
                                }
                            }
                        });


                    }
                    else {
                        loadingDialogsup.dismissDialog();
                        MotionToast.Companion.darkColorToast(SignupActivity.this, task.getException().getMessage(),
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(SignupActivity.this,R.font.helvetica_regular));
                        mail.setText("");
                        pass.setText("");
                        cpass.setText("");
                        mail.clearFocus();
                        pass.clearFocus();
                        cpass.clearFocus();
                        mail_layout.setErrorEnabled(false);
                        mail_layout.setCounterEnabled(false);
                        pass_layout.setErrorEnabled(false);
                        pass_layout.setCounterEnabled(false);
                        cpass_layout.setErrorEnabled(false);
                        cpass_layout.setCounterEnabled(false);
                        sup_recaptcha.setChecked(false);
                        sup_recaptcha.setClickable(true);
                        startActivity(new Intent(SignupActivity.this,SignupActivity.class));
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
        sBinding.supRecaptcha.setVisibility(View.GONE);
        showProgressDialog();
    }

    private void showProgressDialog(){
        sBinding.progressBar3.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1e90ff"), PorterDuff.Mode.SRC_IN);
        sBinding.progressBar3.setVisibility(View.VISIBLE);
    }

    private void fadeOutProgressDialog(){
        sBinding.progressBar3.animate().alpha(0f).setDuration(200).start();
        sBinding.supRecaptcha.setVisibility(View.VISIBLE);
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

    public void taptoBounceml(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        mail_layout.startAnimation(animbtn);
    }

    // Empty Field Animation:

    public void taptoBouncepl(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        pass_layout.startAnimation(animbtn);
    }
    // Empty Field Animation:

    public void taptoBouncecpl(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        cpass_layout.startAnimation(animbtn);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignupActivity.this,SignupwithActivity.class));
        overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_bottom);
    }

}
