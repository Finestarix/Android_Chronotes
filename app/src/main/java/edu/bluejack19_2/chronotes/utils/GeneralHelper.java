package edu.bluejack19_2.chronotes.utils;

import android.widget.EditText;

public class GeneralHelper {

    public static boolean isEmpty(String string) {
        return string.length() == 0;
    }

    public static boolean isEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static void enableEditText(EditText editText) {
        editText.setEnabled(true);
    }

    public static void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }
}
