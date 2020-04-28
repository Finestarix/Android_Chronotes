package edu.bluejack19_2.chronotes.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;

public class GeneralHandler {

    public static boolean isEmpty(String string) {
        return string.length() == 0;
    }

    public static boolean isNotEmail(String email) {
        String regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        return !email.matches(regex);
    }

    public static void enableEditText(EditText editText) {
        editText.setEnabled(true);
    }

    public static void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }

    public static void enableCheckBox(CheckBox checkBox) {
        checkBox.setEnabled(true);
    }

    public static void disableCheckBox(CheckBox checkBox) {
        checkBox.setEnabled(false);
    }

    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
