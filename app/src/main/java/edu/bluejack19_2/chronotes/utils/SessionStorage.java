package edu.bluejack19_2.chronotes.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionStorage {

    private static final String SESSION_STORAGE = "SESSION_STORAGE";
    private static final String SESSION_ID = "SESSION_ID";

    public static String getSessionStorage(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SESSION_STORAGE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(SESSION_ID, "");
    }

    public static void setSessionStorage(Context context, String id) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SESSION_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SESSION_ID, id);
        editor.apply();
    }

    public static void removeSessionStorage(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SESSION_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

}
