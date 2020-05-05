package com.razi.furnitar.Utils;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class UserPreference {
    public SharedPreferences pref;
    private static UserPreference mInstance = null;

    public static UserPreference getInstance() {
        if (mInstance == null) {
            mInstance = new UserPreference();
        }
        return  mInstance;
    }

    public void clearAll() {
        SharedPreferences.Editor edior = pref.edit();
        edior.clear();
        edior.apply();
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void set(String key, boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void set(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void set(String key, long value) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void set(String key, JSONObject jsonObject){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, jsonObject.toString());
        editor.apply();
    }

    public String get(String key, String defaultValue) {
        return pref.getString(key, defaultValue);
    }


    public int get(String key, int defaultValue) {
        return pref.getInt(key, defaultValue);
    }

    public long get(String key, long defaultValue) {
        return pref.getLong(key, defaultValue);
    }

    public boolean get(String key, boolean defaultValue) {
        return pref.getBoolean(key, defaultValue);
    }

    public JSONObject get(String key, JSONObject defaultValue) {
        String strJson = pref.getString(key, null);
        if(strJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(strJson);

                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}