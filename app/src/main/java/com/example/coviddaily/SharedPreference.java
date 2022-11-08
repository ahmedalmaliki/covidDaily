package com.example.coviddaily;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

public class SharedPreference implements Serializable {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String CURR_COUNT = "curr_count";
    String CURR_DATE = "curr_date";
    public SharedPreference(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }
    public void saveCurrentCount(String curr_count) {
        editor.putString(CURR_COUNT, curr_count).commit();
    }

    public String returnCurrentCount() {
        return sharedPreferences.getString(CURR_COUNT, "");
    }
    public void saveCurrentDate(String curr_date) {
        editor.putString(CURR_DATE, curr_date).commit();
    }

    public String returnCurrentDate() {
        return sharedPreferences.getString(CURR_DATE, "");
    }

}
