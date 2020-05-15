package edu.bluejack19_2.chronotes.utils.session;

import android.content.Context;
import android.content.SharedPreferences;

public class RememberMeStorage {

    private static final String REMEMBER_ME_STORAGE = "REMEMBER_ME_STORAGE";
    private static final String REMEMBER_ME_EMAIL = "REMEMBER_ME_EMAIL";
    private static final String REMEMBER_ME_PASS = "REMEMBER_ME_PASS";

    public static String getRememberMeEmail(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(REMEMBER_ME_STORAGE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(REMEMBER_ME_EMAIL, "");
    }

    public static String getRememberMePassword(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(REMEMBER_ME_STORAGE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(REMEMBER_ME_PASS, "");
    }

    public static void setRememberMe(Context context, String email, String password) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(REMEMBER_ME_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(REMEMBER_ME_EMAIL, email);
        editor.putString(REMEMBER_ME_PASS, password);
        editor.apply();
    }

    public static void removeRememberMe(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(REMEMBER_ME_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

}
