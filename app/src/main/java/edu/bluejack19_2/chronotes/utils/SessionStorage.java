package edu.bluejack19_2.chronotes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import edu.bluejack19_2.chronotes.model.User;

public class SessionStorage {

    private static final String SESSION_STORAGE = "SESSION_STORAGE";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String SESSION_NAME = "SESSION_NAME";
    private static final String SESSION_EMAIL = "SESSION_EMAIL";
    private static final String SESSION_PASSWORD = "SESSION_PASSWORD";
    private static final String SESSION_PICTURE = "SESSION_PICTURE";



    public static User getSessionStorage(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SESSION_STORAGE, Context.MODE_PRIVATE);

        if (!sharedpreferences.contains(SESSION_ID))
            return null;

        String id = sharedpreferences.getString(SESSION_ID, "");
        String name = sharedpreferences.getString(SESSION_NAME, "");
        String email = sharedpreferences.getString(SESSION_EMAIL, "");
        String password = sharedpreferences.getString(SESSION_PASSWORD, "");
        String picture = sharedpreferences.getString(SESSION_PICTURE, "");

        User user = new User(id, name, email, password, picture);
        return user;
    }

    public static void setSessionStorage(Context context, User user) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SESSION_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SESSION_ID, user.getId());
        editor.putString(SESSION_NAME, user.getName());
        editor.putString(SESSION_EMAIL, user.getEmail());
        editor.putString(SESSION_PASSWORD, user.getPassword());
        editor.putString(SESSION_PICTURE, user.getPicture());
        editor.apply();
    }

    public static void removeSessionStorage(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SESSION_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

}
