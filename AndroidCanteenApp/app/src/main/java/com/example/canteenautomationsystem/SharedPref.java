package com.example.canteenautomationsystem;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    SharedPreferences mySharedPref;
    public SharedPref(Context context){
        mySharedPref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }

    // This method will save the dark mode state (true/false):
    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }
    public Boolean loadNightModeState(){
        Boolean state = mySharedPref.getBoolean("NightMode",true);
        return state;
    }

    // This method will save the viewPager(StartActivity) state (true/false):

    public void setStartActivityPager(Boolean startActivityPager){
        SharedPreferences.Editor editor_start = mySharedPref.edit();
        editor_start.putBoolean("StartPager",startActivityPager);
        editor_start.commit();
    }

    public Boolean loadStartActivityPager(){
        Boolean startActivityPager = mySharedPref.getBoolean("StartPager",true);
        return startActivityPager;
    }

}
