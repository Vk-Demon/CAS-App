package com.example.canteenautomationsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import info.hoang8f.widget.FButton;
import www.sanju.motiontoast.MotionToast;

public class MyProfileFragment extends Fragment {

    // Declaring Variable(s):

    EditText my_prof_name,my_prof_contact,my_prof_email;
    FButton my_prof_save_btn;

    // Declaring Firebase Authentication:

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View af_mp_root_view =  inflater.inflate(R.layout.acc_frag_my_profile,container,false);

        // Establishing Connection to Firebase:

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        // Initializing toolbar variable(s):

        my_prof_name = (EditText)af_mp_root_view.findViewById(R.id.my_prof_name);
        my_prof_contact = (EditText)af_mp_root_view.findViewById(R.id.my_prof_contact);
        my_prof_email = (EditText)af_mp_root_view.findViewById(R.id.my_prof_email);
        my_prof_save_btn = (FButton) af_mp_root_view.findViewById(R.id.my_prof_save_btn);

        my_prof_save_btn.setCornerRadius(50);
        my_prof_save_btn.setShadowEnabled(true);
        my_prof_save_btn.setShadowHeight(10);
        my_prof_save_btn.setButtonColor(Color.parseColor("#FAED27"));
        my_prof_save_btn.setShadowColor(Color.parseColor("#FDA50F"));

        /*  We have to get info of currently signed in user. We can get it using user's email or uid
            I'm gonna retrieve user detail using email.
            By using orderByChild query we will show the detail from a node
            whose key named email has value equal to currently signed in email.
            It will search all nodes, where the key matches it will get its detail.*/

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Checks until required data are fetched:
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    // Get data:
                    String name =""+ ds.child("name").getValue();
                    String phone =""+ ds.child("phone").getValue();

                    // Set data:
                    my_prof_name.setText(name);
                    my_prof_contact.setText(phone);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Save Button Click Events:

        my_prof_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String update_name = my_prof_name.getText().toString().trim();
                final String update_phone = my_prof_contact.getText().toString().trim();
                if(!TextUtils.isEmpty(update_name) && isValidName_mp(update_name)){
                    HashMap<String, Object> result_name = new HashMap<>();
                    result_name.put("name",update_name);

                    databaseReference.child(firebaseUser.getUid()).updateChildren(result_name)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    if(isValidName_mp(update_name) && isValidPhone_mp(update_phone)){
                                        MotionToast.Companion.darkColorToast(getActivity(), "Updated Successfully...!",
                                                MotionToast.TOAST_SUCCESS,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                                    }
                                    else{
                                        MotionToast.Companion.darkColorToast(getActivity(), "Update incomplete...!",
                                                MotionToast.TOAST_WARNING,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            MotionToast.Companion.darkColorToast(getActivity(), "" +e.getMessage(),
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                        }
                    });
                }

                if(!TextUtils.isEmpty(update_phone) && isValidPhone_mp(update_phone)){
                    HashMap<String, Object> result_phone = new HashMap<>();
                    result_phone.put("phone",update_phone);

                    databaseReference.child(firebaseUser.getUid()).updateChildren(result_phone)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    if(isValidName_mp(update_name) && isValidPhone_mp(update_phone)){
                                        MotionToast.Companion.darkColorToast(getActivity(), "Updated Successfully...!",
                                                MotionToast.TOAST_SUCCESS,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                                    }
                                    else{
                                        MotionToast.Companion.darkColorToast(getActivity(), "Update incomplete...!",
                                                MotionToast.TOAST_WARNING,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            MotionToast.Companion.darkColorToast(getActivity(), "" +e.getMessage(),
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                        }
                    });
                }

            }
        });

        // Calling Profile TextWatcher:

        my_prof_email.addTextChangedListener(my_prof_email_TextWatcher);
        my_prof_name.addTextChangedListener(my_prof_name_TextWatcher);
        my_prof_contact.addTextChangedListener(my_prof_phone_TextWatcher);

        // Updating Email ID from firebase:

        my_prof_email.setText(firebaseUser.getEmail());

        // To disable email EditText (Email ID cannot be changed):

        KeyListener keyListener = my_prof_email.getKeyListener();
        my_prof_email.setKeyListener(null);

        return af_mp_root_view;
    }

    // Defining TextWatcher for Email field in My Profile:

    private TextWatcher my_prof_email_TextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mp_email = my_prof_email.getText().toString().trim();

            if (!isValidEmail_mp(mp_email)) {
                my_prof_email.setError("Invalid email format");
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Defining TextWatcher for Name field in My Profile:

    private TextWatcher my_prof_name_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mp_name = my_prof_name.getText().toString().trim();

            if(!isValidName_mp(mp_name)){
                my_prof_name.setError("Enter your official name");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Defining TextWatcher for Contact field in My Profile:

    private TextWatcher my_prof_phone_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mp_phone = my_prof_contact.getText().toString().trim();

            if(!isValidPhone_mp(mp_phone)){
                my_prof_contact.setError("Enter a valid contact number");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Email Validation:

    private static boolean isValidEmail_mp(String e) {
        return !TextUtils.isEmpty(e) && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    private static boolean isValidName_mp(String n){
        return !TextUtils.isEmpty(n) && n.matches("^[a-zA-z]+([\\s][a-zA-Z]+)*$");
    }

    private static boolean isValidPhone_mp(String p){
        return !TextUtils.isEmpty(p) && p.matches("[0-9]{10}");
    }

  /*  // Empty Field Animation:

    public void tap_to_Bounce_mp_email(View view){
        final Animation anim_btn = AnimationUtils.loadAnimation(,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        anim_btn.setInterpolator(interpolator);
        my_prof_email.startAnimation(anim_btn);
    }

    public void tap_to_Bounce_mp_contact(View view){
        final Animation anim_btn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        anim_btn.setInterpolator(interpolator);
        my_prof_contact.startAnimation(anim_btn);
    }

    public void tap_to_Bounce_mp_name(View view){
        final Animation animbtn = AnimationUtils.loadAnimation(this,R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        animbtn.setInterpolator(interpolator);
        my_prof_name.startAnimation(animbtn);
    }*/

}
